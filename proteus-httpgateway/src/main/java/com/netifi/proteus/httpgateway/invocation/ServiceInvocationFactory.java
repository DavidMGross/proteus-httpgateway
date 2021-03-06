/**
 * Copyright 2018 Netifi Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netifi.proteus.httpgateway.invocation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.protobuf.Empty;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.util.JsonFormat;
import com.netifi.proteus.httpgateway.registry.ProteusRegistry;
import com.netifi.proteus.httpgateway.registry.ProteusRegistryEntry;
import io.micrometer.core.instrument.MeterRegistry;
import io.netifi.proteus.Proteus;
import io.rsocket.rpc.annotations.internal.GeneratedMethod;
import io.netifi.proteus.rsocket.ProteusSocket;
import io.rsocket.RSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory for creating {@link RequestReplyServiceInvocation} instances that wrap calls to Proteus services.
 */
@Component
public class ServiceInvocationFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceInvocationFactory.class);

    private final ProteusRegistry proteusRegistry;
    private final Proteus proteus;
    private final Map<String, ProteusSocket> groupSockets = new ConcurrentHashMap<>();
    private final Map<String, ProteusSocket> destinationSockets = new ConcurrentHashMap<>();

    @Autowired
    private Optional<MeterRegistry> meterRegistry;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    public ServiceInvocationFactory(ProteusRegistry proteusRegistry, Proteus proteus) {
        this.proteusRegistry = proteusRegistry;
        this.proteus = proteus;
    }

    /**
     * Creates a {@link ServiceInvocation} to invoke a Proteus method.
     *
     * @param group proteus group name
     * @param service proteus service name
     * @param method proteus method name
     * @param body request body
     * @return the {@link ServiceInvocation} to invoke
     */
    public ServiceInvocation create(String group, String service, String method, String body) {
        if (!groupSockets.containsKey(group)) {
            LOGGER.debug("Creating new proteus socket for group. [group='{}']", group);
            groupSockets.put(group, proteus.group(group));
        }

        return doCreate(groupSockets.get(group), service, method, body);
    }

    /**
     * Creates a {@link ServiceInvocation} to invoke a Proteus method.
     *
     * @param group proteus group name
     * @param destination proteus destination name
     * @param service proteus service name
     * @param method proteus method name
     * @param body request body
     * @return the {@link RequestReplyServiceInvocation} to invoke
     */
    public ServiceInvocation create(String group, String destination, String service, String method, String body) {
        String key = group + destination;
        if (!destinationSockets.containsKey(key)) {
            LOGGER.debug("Creating new proteus socket for destination. [group='{}', destination='{}']", group, destination);
            destinationSockets.put(key, proteus.destination(destination, group));
        }

        return doCreate(destinationSockets.get(key), service, method, body);
    }

    /**
     * Creates a {@link ServiceInvocation} to invoke a Proteus method.
     *
     * @param proteusSocket proteus socket that connects to the broker
     * @param service proteus service name
     * @param method proteus method name
     * @param body http body to convert to protobuf and send to the Proteus service
     * @return the {@link ServiceInvocation} to invoke
     */
    private ServiceInvocation doCreate(ProteusSocket proteusSocket, String service, String method, String body) {
        ProteusRegistryEntry regEntry = proteusRegistry.get(service, method);

        try {
            Object client;
            Method methodToInvoke = null;
            List<Class<?>> parameterTypes = new ArrayList<>();
            List<Object> parameters = new ArrayList<>();
            Class<?> responseType = null;
            Object responseBuilder = null;

            // Instantiate client
            if (meterRegistry.isPresent()) {
                Constructor ctor = regEntry.getClientClass().getConstructor(RSocket.class, MeterRegistry.class);
                client = ctor.newInstance(proteusSocket, meterRegistry);
            } else {
                Constructor ctor = regEntry.getClientClass().getConstructor(RSocket.class);
                client = ctor.newInstance(proteusSocket);
            }

            // Split the body into multiple parameters if it is an array
            List<String> bodyParts = splitBody(body);

            for(Method clientMethod: regEntry.getClientMethods()) {
                // Find client method to invoke
                if (clientMethod.getName().equalsIgnoreCase(method)) {
                    if (clientMethod.getParameterCount() == bodyParts.size()) {
                        GeneratedMethod annotation = clientMethod.getAnnotation(GeneratedMethod.class);
                        methodToInvoke = clientMethod;
                        parameterTypes = new ArrayList<>(Arrays.asList(clientMethod.getParameterTypes()));
                        responseType = annotation.returnTypeClass();

                        for (int i = 0; i < parameterTypes.size(); i++) {
                            parameters.add(createParameter(parameterTypes.get(i), bodyParts.get(i)));
                        }

                        responseBuilder = createBuilder(responseType);

                        break;
                    }
                }
            }

            if (isFireAndForget(methodToInvoke)) {
                return new FireAndForgetServiceInvocation(client, methodToInvoke, parameterTypes, parameters);
            } else {
                return new RequestReplyServiceInvocation(client, methodToInvoke, parameterTypes, parameters, responseType, responseBuilder);
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("Error occurred invoking Proteus service. [service='%s', method='%s'", service, method), e);
        }
    }

    /**
     * Checks whether or not the method to call is a fire-and-forget method.
     *
     * @param method method to check
     * @return <code>true</code> if the method is fire-and-forget, otherwise <code>false</code>
     */
    private boolean isFireAndForget(Method method) {
        GeneratedMethod annotation = method.getAnnotation(GeneratedMethod.class);
        Class<?> responseType = annotation.returnTypeClass();

        return responseType.isAssignableFrom(Empty.class);
    }

    /**
     * Maps the supplied raw string json into a protobuf parameter type.
     *
     * @param clazz parameter class
     * @param rawValue raw string to convert to the parameter
     * @return protobuf parameter type
     * @throws Exception
     */
    private Object createParameter(Class<?> clazz, String rawValue) throws Exception {
        Method method = clazz.getMethod("newBuilder");
        GeneratedMessageV3.Builder builder = (GeneratedMessageV3.Builder) method.invoke(null);

        JsonFormat.parser().merge(rawValue, builder);
        return builder.build();
    }

    /**
     * Creates a protobuf builder from the supplied Proteus class.
     *
     * @param clazz proteus class
     * @return protobuf builder
     * @throws Exception
     */
    private Object createBuilder(Class<?> clazz) throws Exception {
        Method method = clazz.getMethod("newBuilder");
        return method.invoke(null);
    }

    /**
     * Splits the incoming json into multiple parameters.
     *
     * @param body string body
     * @return a list of strings that make up the parameters in the body.
     * @throws Exception
     */
    private List<String> splitBody(String body) throws Exception {
        List<String> parts = Lists.newArrayList();

        if (!StringUtils.isEmpty(body)) {
            JsonNode rootNode = mapper.readTree(body);

            if (rootNode.isArray()) {
                for (JsonNode node : rootNode) {
                    parts.add(node.toString());
                }
            } else {
                parts.add(body);
            }
        }

        return parts;
    }
}
