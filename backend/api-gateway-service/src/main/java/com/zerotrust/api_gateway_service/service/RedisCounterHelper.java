package com.zerotrust.api_gateway_service.service;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class RedisCounterHelper {
    private final ReactiveStringRedisTemplate redisTemplate;

    public RedisCounterHelper(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<Long> incrementWithExpiry(String key, Duration ttl) {
        return redisTemplate.opsForValue()
                .increment(key)
                .flatMap(count -> {
                    if (count != null && count == 1L) {
                        return redisTemplate.expire(key, ttl).thenReturn(count);
                    }
                    return Mono.just(count == null ? 0L : count);
                });
    }
}
