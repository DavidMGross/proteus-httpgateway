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
package com.netifi.proteus.httpgateway.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Error message returned to HTTP clients.
 */
@JsonPropertyOrder(value = {
        "httpStatus",
        "httpStatusMessage",
        "timestamp",
        "message"
})
public class HttpErrorResponse implements Serializable {
    private static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @JsonProperty
    private Integer httpStatus;

    @JsonProperty
    private String httpStatusMessage;

    @JsonProperty
    private String timestamp;

    @JsonProperty
    private String message;

    public HttpErrorResponse() {

    }

    /**
     * Creates an {@link HttpErrorResponse}.
     *
     * @param httpStatus http status
     * @param message error message
     * @return http error response
     */
    public static HttpErrorResponse of(HttpStatus httpStatus, String message) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN)
                .withZone(ZoneId.systemDefault());

        HttpErrorResponse response = new HttpErrorResponse();
        response.setHttpStatus(httpStatus.value());
        response.setHttpStatusMessage(httpStatus.getReasonPhrase());
        response.setTimestamp(dtf.format(Instant.now()));
        response.setMessage(message);

        return response;
    }

    /**
     * Creates an {@link HttpErrorResponse}.
     *
     * @param httpStatus http status
     * @param throwable exception
     * @return http error response
     */
    public static HttpErrorResponse of(HttpStatus httpStatus, Throwable throwable) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN)
                .withZone(ZoneId.systemDefault());

        HttpErrorResponse response = new HttpErrorResponse();
        response.setHttpStatus(httpStatus.value());
        response.setHttpStatusMessage(httpStatus.getReasonPhrase());
        response.setTimestamp(dtf.format(Instant.now()));
        response.setMessage(throwable.getMessage());

        return response;
    }

    /**
     * Creates an {@link HttpErrorResponse}.
     *
     * @param throwable exception
     * @return http error response
     */
    public static HttpErrorResponse of(Throwable throwable) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN)
                .withZone(ZoneId.systemDefault());

        HttpErrorResponse response = new HttpErrorResponse();
        response.setHttpStatus(HttpStatus.BAD_GATEWAY.value());
        response.setHttpStatusMessage(HttpStatus.BAD_GATEWAY.getReasonPhrase());
        response.setTimestamp(dtf.format(Instant.now()));
        response.setMessage(throwable.getMessage());

        return response;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getHttpStatusMessage() {
        return httpStatusMessage;
    }

    public void setHttpStatusMessage(String httpStatusMessage) {
        this.httpStatusMessage = httpStatusMessage;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
