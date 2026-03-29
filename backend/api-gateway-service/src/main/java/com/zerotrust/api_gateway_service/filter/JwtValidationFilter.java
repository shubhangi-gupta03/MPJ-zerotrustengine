package com.zerotrust.api_gateway_service.filter;

import com.zerotrust.api_gateway_service.exception.GatewayException;
import com.zerotrust.api_gateway_service.service.RateLimitService;
import com.zerotrust.common.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import reactor.core.publisher.Mono;

@Component
public class JwtValidationFilter implements GlobalFilter, Ordered {
    private final JwtUtil jwtUtil;
    private final RateLimitService rateLimitService;

    public JwtValidationFilter(RateLimitService rateLimitService) {
        this.jwtUtil = JwtUtil.fromEnvironment();
        this.rateLimitService = rateLimitService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (path.startsWith("/api/auth/")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.error(new GatewayException(HttpStatus.UNAUTHORIZED, "Missing bearer token"));
        }

        String token = authHeader.substring(7);
        Claims claims;
        try {
            claims = jwtUtil.parseAndValidate(token);
        } catch (Exception ex) {
            return Mono.error(new GatewayException(HttpStatus.UNAUTHORIZED, "Invalid bearer token"));
        }

        String subject = claims.getSubject();
        if (subject == null || subject.isBlank()) {
            return Mono.error(new GatewayException(HttpStatus.UNAUTHORIZED, "Invalid token subject"));
        }

        return rateLimitService.userAllowed(subject)
                .flatMap(allowed -> {
                    if (!allowed) {
                        return Mono.error(new GatewayException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded"));
                    }
                    ServerWebExchange mutated = exchange.mutate()
                            .request(exchange.getRequest().mutate().header("X-Authenticated-User", subject).build())
                            .build();
                    return chain.filter(mutated);
                });
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
