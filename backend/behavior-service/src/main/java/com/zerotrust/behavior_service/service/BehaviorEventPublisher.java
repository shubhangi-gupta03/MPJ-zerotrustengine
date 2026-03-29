package com.zerotrust.behavior_service.service;

import com.zerotrust.common.dto.BehaviorEvent;
import com.zerotrust.common.messaging.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

@Service
public class BehaviorEventPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(BehaviorEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RetryTemplate retryTemplate;

    public BehaviorEventPublisher(KafkaTemplate<String, Object> kafkaTemplate, RetryTemplate retryTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.retryTemplate = retryTemplate;
    }

    public void publish(BehaviorEvent event) {
        try {
            retryTemplate.execute(ctx -> {
                kafkaTemplate.send(KafkaTopics.BEHAVIOR_ANOMALIES, event.getUserId(), event);
                return null;
            });
        } catch (Exception ex) {
            LOGGER.error("Failed to publish behavior anomaly for user {}", event.getUserId(), ex);
        }
    }
}
