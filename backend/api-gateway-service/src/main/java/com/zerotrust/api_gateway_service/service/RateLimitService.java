package com.zerotrust.api_gateway_service.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class RateLimitService {
    private static final int USER_LIMIT_PER_MINUTE = 60;
    private static final int FAILED_LOGIN_LIMIT_PER_WINDOW = 10;

    private final RedisCounterHelper counterHelper;

    public RateLimitService(RedisCounterHelper counterHelper) {
        this.counterHelper = counterHelper;
    }

    public Mono<Boolean> userAllowed(String userId) {
        String key = "rate:user:" + userId;
        return counterHelper.incrementWithExpiry(key, Duration.ofMinutes(1))
                .map(count -> count <= USER_LIMIT_PER_MINUTE);
    }

    public Mono<Boolean> failedLoginIpAllowed(String ip) {
        String key = "rate:failed-login:ip:" + ip;
        return counterHelper.incrementWithExpiry(key, Duration.ofMinutes(15))
                .map(count -> count <= FAILED_LOGIN_LIMIT_PER_WINDOW);
    }
}
