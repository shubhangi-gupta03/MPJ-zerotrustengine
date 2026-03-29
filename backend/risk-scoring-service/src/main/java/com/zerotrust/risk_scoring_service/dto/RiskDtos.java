package com.zerotrust.risk_scoring_service.dto;

import java.util.Map;

public class RiskDtos {
    public record ComputeRequest(String userId, String sessionId, Double deviceRisk, Double behaviorRisk, Double contextualRisk) { }
    public record WeightsUpdateRequest(Map<String, Double> weights, Double lowThreshold, Double mediumThreshold, Double highThreshold) { }
}
