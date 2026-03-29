package com.zerotrust.risk_scoring_service.service;

import com.zerotrust.common.dto.RiskEvent;
import com.zerotrust.common.messaging.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

@Service
public class RiskEventPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(RiskEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RetryTemplate retryTemplate;

    public RiskEventPublisher(KafkaTemplate<String, Object> kafkaTemplate, RetryTemplate retryTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.retryTemplate = retryTemplate;
    }

    public void publishUpdate(RiskEvent event) {
        try {
            retryTemplate.execute(ctx -> {
                kafkaTemplate.send(KafkaTopics.RISK_UPDATES, event.getUserId(), event);
                return null;
            });
        } catch (Exception ex) {
            LOGGER.error("Failed to publish risk update for session {}", event.getSessionId(), ex);
        }
    }

    public void publishCriticalAlert(RiskEvent event) {
        try {
            retryTemplate.execute(ctx -> {
                kafkaTemplate.send(KafkaTopics.SYSTEM_ALERTS, event.getUserId(), event);
                return null;
            });
        } catch (Exception ex) {
            LOGGER.error("Failed to publish critical alert for session {}", event.getSessionId(), ex);
        }
    }
}
