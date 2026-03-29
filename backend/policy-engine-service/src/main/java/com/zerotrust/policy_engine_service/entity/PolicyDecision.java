package com.zerotrust.policy_engine_service.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Document(collection = "policy_decisions")
public class PolicyDecision {
    @Id
    private String id;
    private String userId;
    private String role;
    private String resource;
    private String riskTier;
    private String action;
    private String matchedRuleId;
    private Instant decidedAt;
    private Map<String, Object> context = new HashMap<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    public String getRiskTier() { return riskTier; }
    public void setRiskTier(String riskTier) { this.riskTier = riskTier; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getMatchedRuleId() { return matchedRuleId; }
    public void setMatchedRuleId(String matchedRuleId) { this.matchedRuleId = matchedRuleId; }
    public Instant getDecidedAt() { return decidedAt; }
    public void setDecidedAt(Instant decidedAt) { this.decidedAt = decidedAt; }
    public Map<String, Object> getContext() { return context; }
    public void setContext(Map<String, Object> context) { this.context = context; }
}
