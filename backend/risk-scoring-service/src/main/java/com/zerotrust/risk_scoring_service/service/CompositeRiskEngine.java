package com.zerotrust.risk_scoring_service.service;

import com.zerotrust.common.risk.RiskLevel;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CompositeRiskEngine {
    private final Map<String, Double> weights = new HashMap<>(Map.of(
            "device", 0.35d,
            "behavior", 0.40d,
            "context", 0.25d
    ));
    private double lowThreshold = 0.25d;
    private double mediumThreshold = 0.5d;
    private double highThreshold = 0.75d;

    public synchronized ScoreResult compute(double device, double behavior, double context) {
        double score = device * weights.get("device") + behavior * weights.get("behavior") + context * weights.get("context");
        RiskLevel level = score >= highThreshold ? RiskLevel.CRITICAL
                : (score >= mediumThreshold ? RiskLevel.HIGH : (score >= lowThreshold ? RiskLevel.MEDIUM : RiskLevel.LOW));
        return new ScoreResult(score, level, Map.of(
                "device", device * weights.get("device"),
                "behavior", behavior * weights.get("behavior"),
                "context", context * weights.get("context")
        ));
    }

    public synchronized void update(Map<String, Double> updatedWeights, Double low, Double medium, Double high) {
        if (updatedWeights != null) {
            weights.putAll(updatedWeights);
        }
        if (low != null) { lowThreshold = low; }
        if (medium != null) { mediumThreshold = medium; }
        if (high != null) { highThreshold = high; }
    }

    public synchronized Map<String, Object> snapshot() {
        return Map.of(
                "weights", new HashMap<>(weights),
                "thresholds", Map.of("low", lowThreshold, "medium", mediumThreshold, "high", highThreshold)
        );
    }

    public record ScoreResult(double score, RiskLevel riskTier, Map<String, Double> contributions) { }
}
