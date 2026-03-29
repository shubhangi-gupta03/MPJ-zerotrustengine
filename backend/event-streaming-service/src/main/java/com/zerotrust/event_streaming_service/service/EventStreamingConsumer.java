package com.zerotrust.event_streaming_service.service;

import com.zerotrust.event_streaming_service.model.AuditEvent;
import com.zerotrust.event_streaming_service.repo.AuditEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.Map;

@Service
public class EventStreamingConsumer {
    private static final Logger log = LoggerFactory.getLogger(EventStreamingConsumer.class);
    private final AuditEventRepository repository;
    private final SimpMessagingTemplate messagingTemplate;
    private final JavaMailSender mailSender;
    private final WebClient webClient;
    private final String fromEmail;
    private final String toEmail;
    private final String slackWebhook;

    public EventStreamingConsumer(
            AuditEventRepository repository,
            SimpMessagingTemplate messagingTemplate,
            JavaMailSender mailSender,
            @Value("${alerts.email.from}") String fromEmail,
            @Value("${alerts.email.to}") String toEmail,
            @Value("${alerts.slack.webhook}") String slackWebhook
    ) {
        this.repository = repository;
        this.messagingTemplate = messagingTemplate;
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
        this.toEmail = toEmail;
        this.slackWebhook = slackWebhook;
        this.webClient = WebClient.builder().build();
    }

    @KafkaListener(topics = {
            "auth.events", "device.events", "behavior.anomalies",
            "risk.updates", "policy.decisions", "system.alerts"
    }, groupId = "event-streaming-service")
    public void onEvent(String payload, org.apache.kafka.clients.consumer.ConsumerRecord<String, String> record) {
        String topic = record.topic();
        AuditEvent event = new AuditEvent();
        event.setType(topic);
        event.setPayload(payload);
        event.setTimestamp(Instant.now());
        event.setSeverity(topic.equals("system.alerts") ? "CRITICAL" : "INFO");
        repository.save(event);

        messagingTemplate.convertAndSend("/topic/alerts", Map.of(
                "topic", topic,
                "payload", payload,
                "timestamp", event.getTimestamp().toString(),
                "severity", event.getSeverity()
        ));

        if ("system.alerts".equals(topic)) {
            sendAlertEmail(payload);
            sendSlack(payload);
        }
    }

    private void sendAlertEmail(String payload) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(toEmail);
            msg.setSubject("ZeroTrust Critical Alert");
            msg.setText(payload);
            mailSender.send(msg);
        } catch (Exception ex) {
            log.error("Failed to send alert email", ex);
        }
    }

    private void sendSlack(String payload) {
        try {
            if (slackWebhook == null || slackWebhook.isBlank()) {
                return;
            }
            webClient.post()
                    .uri(slackWebhook)
                    .bodyValue(Map.of("text", "ZeroTrust alert: " + payload))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception ex) {
            log.error("Failed to send slack alert", ex);
        }
    }
}
