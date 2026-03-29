package com.zerotrust.auth_service.service;

import com.zerotrust.auth_service.dto.AuthEvent;
import com.zerotrust.common.messaging.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuthEventPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RetryTemplate retryTemplate;

    public AuthEventPublisher(KafkaTemplate<String, Object> kafkaTemplate, RetryTemplate retryTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.retryTemplate = retryTemplate;
    }

    public void publish(AuthEvent event) {
        try {
            retryTemplate.execute(ctx -> {
                kafkaTemplate.send(KafkaTopics.AUTH_EVENTS, event.usernameOrEmail(), event);
                return null;
            });
        } catch (Exception ex) {
            LOGGER.error("Failed to publish auth event after retries for {}", event.usernameOrEmail(), ex);
        }
    }
}
