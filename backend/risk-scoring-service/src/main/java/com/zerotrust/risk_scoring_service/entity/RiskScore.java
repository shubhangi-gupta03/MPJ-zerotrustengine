package com.zerotrust.risk_scoring_service.entity;

import com.zerotrust.common.risk.RiskLevel;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Document(collection = "risk_scores")
public class RiskScore {
    @Id
    private String id;
    private String userId;
    private String sessionId;
    private double score;
    private RiskLevel riskTier;
    private Instant calculatedAt;
    private boolean active;
    private Map<String, Double> contributions = new HashMap<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    public RiskLevel getRiskTier() { return riskTier; }
    public void setRiskTier(RiskLevel riskTier) { this.riskTier = riskTier; }
    public Instant getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(Instant calculatedAt) { this.calculatedAt = calculatedAt; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Map<String, Double> getContributions() { return contributions; }
    public void setContributions(Map<String, Double> contributions) { this.contributions = contributions; }
}
