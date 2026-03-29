package com.zerotrust.policy_engine_service.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "policy_rules")
public class PolicyRule {
    @Id
    private String id;
    private String name;
    private int priority;
    private String userId;
    private String role;
    private String riskTier;
    private String resource;
    private Integer fromHour;
    private Integer toHour;
    private String action;
    private boolean enabled = true;
    private Instant updatedAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getRiskTier() { return riskTier; }
    public void setRiskTier(String riskTier) { this.riskTier = riskTier; }
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    public Integer getFromHour() { return fromHour; }
    public void setFromHour(Integer fromHour) { this.fromHour = fromHour; }
    public Integer getToHour() { return toHour; }
    public void setToHour(Integer toHour) { this.toHour = toHour; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
