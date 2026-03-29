package com.zerotrust.auth_service.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Map;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class AppConfig {

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(new SimpleRetryPolicy(3, Map.of(Exception.class, true)));
        return retryTemplate;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(org.springframework.data.redis.connection.RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }
}
