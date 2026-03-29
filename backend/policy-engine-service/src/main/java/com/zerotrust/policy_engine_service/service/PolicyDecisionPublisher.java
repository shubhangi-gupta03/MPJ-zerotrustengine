package com.zerotrust.policy_engine_service.service;

import com.zerotrust.common.messaging.KafkaTopics;
import com.zerotrust.policy_engine_service.entity.PolicyDecision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

@Service
public class PolicyDecisionPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyDecisionPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RetryTemplate retryTemplate;

    public PolicyDecisionPublisher(KafkaTemplate<String, Object> kafkaTemplate, RetryTemplate retryTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.retryTemplate = retryTemplate;
    }

    public void publish(PolicyDecision decision) {
        try {
            retryTemplate.execute(ctx -> {
                kafkaTemplate.send(KafkaTopics.POLICY_DECISIONS, decision.getUserId(), decision);
                return null;
            });
        } catch (Exception ex) {
            LOGGER.error("Failed to publish policy decision for user {}", decision.getUserId(), ex);
        }
    }
}
