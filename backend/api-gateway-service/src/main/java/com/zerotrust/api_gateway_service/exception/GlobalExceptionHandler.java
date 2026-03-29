package com.zerotrust.api_gateway_service.exception;

import com.zerotrust.common.api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class GlobalExceptionHandler implements WebExceptionHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Gateway request failed";

        if (ex instanceof GatewayException gatewayException) {
            status = gatewayException.getStatus();
            message = gatewayException.getMessage();
        }

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        ApiResponse<Object> apiResponse = ApiResponse.error(message);
        byte[] body;
        try {
            body = objectMapper.writeValueAsBytes(apiResponse);
        } catch (JsonProcessingException e) {
            body = "{\"success\":false,\"message\":\"Gateway request failed\"}".getBytes();
        }
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body)));
    }
}
