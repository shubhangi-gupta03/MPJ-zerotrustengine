package com.zerotrust.risk_scoring_service.service;

import com.zerotrust.common.dto.BehaviorEvent;
import com.zerotrust.common.dto.DeviceEvent;
import com.zerotrust.common.dto.RiskEvent;
import com.zerotrust.common.risk.RiskLevel;
import com.zerotrust.risk_scoring_service.dto.RiskDtos;
import com.zerotrust.risk_scoring_service.entity.RiskScore;
import com.zerotrust.risk_scoring_service.repository.RiskScoreRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class RiskScoringService {
    private final RiskScoreRepository repository;
    private final CompositeRiskEngine engine;
    private final RiskEventPublisher publisher;
    private final Map<String, Double> deviceSignals = new HashMap<>();
    private final Map<String, Double> behaviorSignals = new HashMap<>();

    public RiskScoringService(RiskScoreRepository repository, CompositeRiskEngine engine, RiskEventPublisher publisher) {
        this.repository = repository;
        this.engine = engine;
        this.publisher = publisher;
    }

    public RiskScore compute(RiskDtos.ComputeRequest request) {
        CompositeRiskEngine.ScoreResult result = engine.compute(
                request.deviceRisk() == null ? 0.3d : request.deviceRisk(),
                request.behaviorRisk() == null ? 0.3d : request.behaviorRisk(),
                request.contextualRisk() == null ? 0.2d : request.contextualRisk()
        );
        RiskScore score = new RiskScore();
        score.setUserId(request.userId());
        score.setSessionId(request.sessionId());
        score.setScore(result.score());
        score.setRiskTier(result.riskTier());
        score.setCalculatedAt(Instant.now());
        score.setActive(true);
        score.setContributions(result.contributions());
        RiskScore saved = repository.save(score);
        publish(saved);
        return saved;
    }

    public RiskScore score(String sessionId) {
        return repository.findTopBySessionIdOrderByCalculatedAtDesc(sessionId).orElse(null);
    }

    public List<RiskScore> history(String userId) {
        return repository.findByUserIdOrderByCalculatedAtDesc(userId);
    }

    public Map<String, Object> dashboardSummary() {
        List<RiskScore> all = repository.findAll();
        long critical = all.stream().filter(r -> r.getRiskTier() == RiskLevel.CRITICAL).count();
        long high = all.stream().filter(r -> r.getRiskTier() == RiskLevel.HIGH).count();
        return Map.of(
                "total", all.size(),
                "critical", critical,
                "high", high,
                "engine", engine.snapshot()
        );
    }

    public Map<String, Object> updateWeights(RiskDtos.WeightsUpdateRequest request) {
        engine.update(request.weights(), request.lowThreshold(), request.mediumThreshold(), request.highThreshold());
        return engine.snapshot();
    }

    @KafkaListener(topics = "device.events", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeDevice(DeviceEvent event) {
        deviceSignals.put(event.getSessionId(), 1.0d - event.getTrustScore());
        recomputeForSession(event.getUserId(), event.getSessionId());
    }

    @KafkaListener(topics = "behavior.anomalies", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBehavior(BehaviorEvent event) {
        behaviorSignals.put(event.getSessionId(), event.getAnomalyScore());
        recomputeForSession(event.getUserId(), event.getSessionId());
    }

    private void recomputeForSession(String userId, String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return;
        }
        if (repository.findBySessionIdAndActiveTrue(sessionId).isEmpty()) {
            return;
        }
        compute(new RiskDtos.ComputeRequest(
                userId,
                sessionId,
                deviceSignals.getOrDefault(sessionId, 0.3d),
                behaviorSignals.getOrDefault(sessionId, 0.2d),
                0.2d
        ));
    }

    private void publish(RiskScore score) {
        RiskEvent event = new RiskEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setUserId(score.getUserId());
        event.setSessionId(score.getSessionId());
        event.setRiskScore(score.getScore());
        event.setRiskLevel(score.getRiskTier());
        event.setStatus("UPDATED");
        event.setTimestamp(Instant.now());
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("contributions", score.getContributions());
        event.setMetadata(metadata);
        publisher.publishUpdate(event);
        if (score.getRiskTier() == RiskLevel.CRITICAL) {
            publisher.publishCriticalAlert(event);
        }
    }
}
