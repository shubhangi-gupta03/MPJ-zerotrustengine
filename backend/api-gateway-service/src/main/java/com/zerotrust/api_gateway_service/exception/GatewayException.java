package com.zerotrust.api_gateway_service.exception;

import org.springframework.http.HttpStatus;

public class GatewayException extends RuntimeException {
    private final HttpStatus status;

    public GatewayException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
