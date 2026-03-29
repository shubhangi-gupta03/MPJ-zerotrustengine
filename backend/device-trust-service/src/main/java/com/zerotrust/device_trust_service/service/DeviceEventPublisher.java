package com.zerotrust.device_trust_service.service;

import com.zerotrust.common.dto.DeviceEvent;
import com.zerotrust.common.messaging.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

@Service
public class DeviceEventPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RetryTemplate retryTemplate;

    public DeviceEventPublisher(KafkaTemplate<String, Object> kafkaTemplate, RetryTemplate retryTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.retryTemplate = retryTemplate;
    }

    public void publish(DeviceEvent event) {
        try {
            retryTemplate.execute(ctx -> {
                kafkaTemplate.send(KafkaTopics.DEVICE_EVENTS, event.getUserId(), event);
                return null;
            });
        } catch (Exception ex) {
            LOGGER.error("Failed to publish device event for device {}", event.getDeviceId(), ex);
        }
    }
}
