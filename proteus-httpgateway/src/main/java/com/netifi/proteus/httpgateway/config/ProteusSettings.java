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
package com.netifi.proteus.httpgateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Component
@ConfigurationProperties("netifi.proteus")
public class ProteusSettings {
    public static final String DEFAULT_BROKER_HOSTNAME = "localhost";
    public static final Integer DEFAULT_BROKER_PORT = 8001;

    private String brokerHostname;

    @Min(value = 0)
    @Max(value = 65_535)
    private Integer brokerPort;

    @NotNull
    private Long accessKey;

    @NotNull
    private String accessToken;

    @PostConstruct
    public void init() {
        if (StringUtils.isEmpty(brokerHostname)) {
            brokerHostname = DEFAULT_BROKER_HOSTNAME;
        }

        if (brokerPort == null) {
            brokerPort = DEFAULT_BROKER_PORT;
        }
    }

    public String getBrokerHostname() {
        return brokerHostname;
    }

    public void setBrokerHostname(String brokerHostname) {
        this.brokerHostname = brokerHostname;
    }

    public Integer getBrokerPort() {
        return brokerPort;
    }

    public void setBrokerPort(Integer brokerPort) {
        this.brokerPort = brokerPort;
    }

    public Long getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(Long accessKey) {
        this.accessKey = accessKey;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
