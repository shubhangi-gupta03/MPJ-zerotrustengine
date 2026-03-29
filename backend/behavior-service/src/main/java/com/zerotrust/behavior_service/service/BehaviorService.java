package com.zerotrust.behavior_service.service;

import com.zerotrust.behavior_service.dto.BehaviorDtos;
import com.zerotrust.behavior_service.entity.BehaviorAnomaly;
import com.zerotrust.behavior_service.entity.BehaviorBaseline;
import com.zerotrust.behavior_service.entity.BehaviorEventEntity;
import com.zerotrust.behavior_service.repository.BehaviorAnomalyRepository;
import com.zerotrust.behavior_service.repository.BehaviorBaselineRepository;
import com.zerotrust.behavior_service.repository.BehaviorEventRepository;
import com.zerotrust.common.dto.BehaviorEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BehaviorService {
    private final BehaviorEventRepository eventRepository;
    private final BehaviorBaselineRepository baselineRepository;
    private final BehaviorAnomalyRepository anomalyRepository;
    private final IsolationForestDetector isolationForestDetector;
    private final LSTMPatternDetector lstmPatternDetector;
    private final BehaviorEventPublisher publisher;
    private final Map<String, Deque<Integer>> actionWindow = new HashMap<>();

    public BehaviorService(BehaviorEventRepository eventRepository,
                           BehaviorBaselineRepository baselineRepository,
                           BehaviorAnomalyRepository anomalyRepository,
                           IsolationForestDetector isolationForestDetector,
                           LSTMPatternDetector lstmPatternDetector,
                           BehaviorEventPublisher publisher) {
        this.eventRepository = eventRepository;
        this.baselineRepository = baselineRepository;
        this.anomalyRepository = anomalyRepository;
        this.isolationForestDetector = isolationForestDetector;
        this.lstmPatternDetector = lstmPatternDetector;
        this.publisher = publisher;
    }

    public BehaviorEventEntity ingest(BehaviorDtos.IngestRequest request) {
        BehaviorEventEntity entity = new BehaviorEventEntity();
        entity.setUserId(request.userId());
        entity.setSessionId(request.sessionId());
        entity.setActionId(request.actionId());
        entity.setVelocity(request.velocity());
        entity.setGeoDistance(request.geoDistance());
        entity.setInteractionRate(request.interactionRate());
        entity.setEventTime(request.eventTime() == null ? Instant.now() : request.eventTime());
        entity.setFeatures(request.features());
        BehaviorEventEntity saved = eventRepository.save(entity);

        boolean layer1 = layer1Flag(saved);
        double layer2Score = isolationForestDetector.score(saved.getFeatures());
        boolean layer2 = layer2Score > 0.75d;
        double layer3Score = layer3(saved.getUserId(), saved.getActionId());
        boolean layer3 = layer3Score > 0.8d;

        if (layer1 || layer2 || layer3) {
            BehaviorAnomaly anomaly = new BehaviorAnomaly();
            anomaly.setUserId(saved.getUserId());
            anomaly.setSessionId(saved.getSessionId());
            anomaly.setAnomalyType(layer3 ? "LSTM_PATTERN" : (layer2 ? "ISOLATION_FOREST" : "BASELINE_DEVIATION"));
            anomaly.setScore(Math.max(layer3Score, layer2Score));
            anomaly.setDetectedAt(Instant.now());
            anomaly.getMetadata().put("layer1", layer1);
            anomaly.getMetadata().put("layer2", layer2Score);
            anomaly.getMetadata().put("layer3", layer3Score);
            anomalyRepository.save(anomaly);

            BehaviorEvent event = new BehaviorEvent();
            event.setEventId(UUID.randomUUID().toString());
            event.setUserId(saved.getUserId());
            event.setSessionId(saved.getSessionId());
            event.setEventType("ANOMALY");
            event.setAnomalyScore(anomaly.getScore());
            event.setStatus("FLAGGED");
            event.setTimestamp(Instant.now());
            event.setMetadata(anomaly.getMetadata());
            publisher.publish(event);
        }
        return saved;
    }

    public List<BehaviorEventEntity> profile(String userId) { return eventRepository.findByUserId(userId); }
    public List<BehaviorAnomaly> anomalies(String userId) { return anomalyRepository.findByUserId(userId); }
    public List<BehaviorBaseline> baselines() { return baselineRepository.findAll(); }

    @Scheduled(fixedDelay = 86400000L)
    public void scheduledBaselineRebuild() { rebuildBaselines(); }

    public List<BehaviorBaseline> rebuildBaselines() {
        Instant cutoff = Instant.now().minus(30, ChronoUnit.DAYS);
        Map<String, List<BehaviorEventEntity>> grouped = eventRepository.findAll().stream()
                .filter(e -> e.getEventTime() != null && e.getEventTime().isAfter(cutoff))
                .collect(Collectors.groupingBy(BehaviorEventEntity::getUserId));
        List<BehaviorBaseline> output = new ArrayList<>();
        for (Map.Entry<String, List<BehaviorEventEntity>> entry : grouped.entrySet()) {
            List<Double> centroid = deterministicCentroid(entry.getValue());
            BehaviorBaseline baseline = baselineRepository.findByUserId(entry.getKey()).orElseGet(BehaviorBaseline::new);
            baseline.setUserId(entry.getKey());
            baseline.setCentroid(centroid);
            baseline.setSampleSize(entry.getValue().size());
            baseline.setUpdatedAt(Instant.now());
            output.add(baselineRepository.save(baseline));
        }
        return output;
    }

    private boolean layer1Flag(BehaviorEventEntity event) {
        return baselineRepository.findByUserId(event.getUserId()).map(b -> {
            List<Double> centroid = b.getCentroid();
            if (centroid.isEmpty() || event.getFeatures().isEmpty()) {
                return false;
            }
            double d = 0.0;
            int n = Math.min(centroid.size(), event.getFeatures().size());
            for (int i = 0; i < n; i++) {
                d += Math.abs(centroid.get(i) - event.getFeatures().get(i));
            }
            return (d / n) > 0.5d;
        }).orElse(false);
    }

    private double layer3(String userId, int actionId) {
        Deque<Integer> queue = actionWindow.computeIfAbsent(userId, k -> new ArrayDeque<>());
        if (queue.size() == 20) {
            queue.removeFirst();
        }
        queue.addLast(actionId);
        if (queue.size() < 20) {
            return 0.0d;
        }
        return lstmPatternDetector.score(new ArrayList<>(queue));
    }

    private List<Double> deterministicCentroid(List<BehaviorEventEntity> events) {
        int featureLength = events.stream().map(e -> e.getFeatures().size()).max(Integer::compareTo).orElse(0);
        List<Double> centroid = new ArrayList<>(Collections.nCopies(featureLength, 0.0d));
        for (BehaviorEventEntity event : events.stream().sorted(Comparator.comparing(BehaviorEventEntity::getEventTime)).toList()) {
            for (int i = 0; i < event.getFeatures().size(); i++) {
                centroid.set(i, centroid.get(i) + event.getFeatures().get(i));
            }
        }
        for (int i = 0; i < centroid.size(); i++) {
            centroid.set(i, centroid.get(i) / events.size());
        }
        return centroid;
    }
}
