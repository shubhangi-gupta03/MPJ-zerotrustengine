package com.zerotrust.api_gateway_service.filter;

import com.zerotrust.api_gateway_service.exception.GatewayException;
import com.zerotrust.api_gateway_service.service.RateLimitService;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class FailedLoginIpRateLimitFilter implements GlobalFilter, Ordered {
    private final RateLimitService rateLimitService;

    public FailedLoginIpRateLimitFilter(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (!"/api/auth/login".equals(path)) {
            return chain.filter(exchange);
        }
        String ip = exchange.getRequest().getRemoteAddress() != null
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : "unknown";

        return rateLimitService.failedLoginIpAllowed(ip)
                .flatMap(allowed -> {
                    if (!allowed) {
                        return Mono.error(new GatewayException(
                                HttpStatus.TOO_MANY_REQUESTS,
                                "Too many failed login attempts from this IP"
                        ));
                    }
                    return chain.filter(exchange);
                });
    }

    @Override
    public int getOrder() {
        return -101;
    }
}
