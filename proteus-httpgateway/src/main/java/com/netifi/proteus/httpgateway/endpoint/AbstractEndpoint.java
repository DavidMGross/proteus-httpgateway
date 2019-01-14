/**
 * Copyright 2018 Netifi Inc.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netifi.proteus.httpgateway.endpoint;

import com.google.protobuf.*;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.util.JsonFormat;
import com.netifi.proteus.httpgateway.rsocket.RSocketSupplier;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.rpc.frames.Metadata;
import io.rsocket.util.ByteBufPayload;
import org.reactivestreams.Publisher;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerResponse;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static com.netifi.proteus.httpgateway.util.ProtoUtil.EMPTY_MESSAGE;

public abstract class AbstractEndpoint<T> implements Endpoint {
  private final Descriptor request;
  private final Descriptor response;
  private final String defaultGroup;
  private final RSocketSupplier rSocketSupplier;
  private final boolean hasTimeout;
  private final Duration timeout;
  private final int maxConcurrency;
  private final AtomicInteger outstandingRequests;
  private final String service;
  private final String method;
  private final JsonFormat.TypeRegistry typeRegistry;
  private final boolean requestEmpty;

  public AbstractEndpoint(
      String service,
      String method,
      Descriptor request,
      Descriptor response,
      String defaultGroup,
      RSocketSupplier rSocketSupplier,
      boolean hasTimeout,
      Duration timeout,
      int maxConcurrency,
      JsonFormat.TypeRegistry typeRegistry) {
    this.service = service;
    this.method = method;
    this.request = request;
    this.response = response;
    this.defaultGroup = defaultGroup;
    this.rSocketSupplier = rSocketSupplier;
    this.hasTimeout = hasTimeout;
    this.timeout = timeout;
    this.maxConcurrency = maxConcurrency;
    this.outstandingRequests = new AtomicInteger();
    this.typeRegistry = typeRegistry;
    this.requestEmpty = EMPTY_MESSAGE.equals(request.getFullName());
  }

  @Override
  public boolean isRequestEmpty() {
    return requestEmpty;
  }

  String parseResponseToJson(ByteBuf byteBuf) {
    try {
      ByteBuffer byteBuffer = byteBuf.internalNioBuffer(0, byteBuf.readableBytes());
      DynamicMessage message =
          DynamicMessage.parseFrom(response, CodedInputStream.newInstance(byteBuffer));
      return JsonFormat.printer()
          .omittingInsignificantWhitespace()
          .usingTypeRegistry(typeRegistry)
          .print(message);
    } catch (Throwable t) {
      throw Exceptions.propagate(t);
    }
  }

  @Override
  public Publisher<Void> apply(HttpHeaders headers, String json, HttpServerResponse response) {
    try {
      return start(response)
          .thenMany(
              Flux.defer(
                  () -> {
                    try {
                      RSocket rSocket = rSocketSupplier.apply(defaultGroup, headers);

                      Message message;
                      if (isRequestEmpty()) {
                        message = Empty.getDefaultInstance();
                      } else {
                        DynamicMessage.Builder builder = DynamicMessage.newBuilder(request);
                        JsonFormat.parser().merge(json, builder);
                        message = builder.build();
                      }

                      ByteBuf data = serialize(message);
                      ByteBuf metadata =
                          Metadata.encode(
                              ByteBufAllocator.DEFAULT, service, method, Unpooled.EMPTY_BUFFER);
                      Payload request = ByteBufPayload.create(data, metadata);

                      return applyTimeout(doApply(rSocket, request))
                          .flatMap(t -> doHandleResponse(t, response))
                          .onErrorResume(
                              throwable -> {
                                if (throwable instanceof TimeoutException) {
                                  response.status(HttpResponseStatus.REQUEST_TIMEOUT);
                                } else {
                                  response.status(HttpResponseStatus.INTERNAL_SERVER_ERROR);
                                }

                                return response.send();
                              })
                          .doFinally(s -> end());
                    } catch (Exception e) {
                      return Flux.error(e);
                    }
                  }));
    } finally {
      end();
    }
  }

  private ByteBuf serialize(MessageLite message) {
    int length = message.getSerializedSize();
    ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer(length);
    try {
      message.writeTo(CodedOutputStream.newInstance(byteBuf.internalNioBuffer(0, length)));
      byteBuf.writerIndex(length);
      return byteBuf;
    } catch (Throwable t) {
      byteBuf.release();
      throw Exceptions.propagate(t);
    }
  }

  private Flux<T> applyTimeout(Publisher<T> target) {
    Flux<T> from = Flux.from(target);
    if (hasTimeout) {
      from = from.timeout(timeout);
    }

    return from;
  }

  private Mono<Void> start(HttpServerResponse response) {
    if (outstandingRequests.incrementAndGet() >= maxConcurrency) {
      response.status(HttpResponseStatus.TOO_MANY_REQUESTS);
      return response.send();
    } else {
      return Mono.empty();
    }
  }

  private void end() {
    outstandingRequests.decrementAndGet();
  }

  abstract Publisher<T> doApply(RSocket rSocket, Payload request);

  abstract Publisher<Void> doHandleResponse(T source, HttpServerResponse response);
}
