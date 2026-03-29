package com.zerotrust.auth_service.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class SessionBlacklistService {
    private final StringRedisTemplate redisTemplate;

    public SessionBlacklistService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void blacklist(String tokenId, Duration ttl) {
        redisTemplate.opsForValue().set("blacklist:" + tokenId, "1", ttl);
    }

    public boolean isBlacklisted(String tokenId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + tokenId));
    }
}
