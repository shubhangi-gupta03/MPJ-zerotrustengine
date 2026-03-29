package com.zerotrust.common.dto;

import com.zerotrust.common.risk.RiskLevel;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RiskEvent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String eventId;
    private String userId;
    private String sessionId;
    private double riskScore;
    private RiskLevel riskLevel;
    private String status;
    private Instant timestamp;
    private Map<String, Object> metadata = new HashMap<>();

    public RiskEvent() {
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(double riskScore) {
        this.riskScore = riskScore;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RiskEvent riskEvent)) {
            return false;
        }
        return Double.compare(riskEvent.riskScore, riskScore) == 0
                && Objects.equals(eventId, riskEvent.eventId)
                && Objects.equals(userId, riskEvent.userId)
                && Objects.equals(sessionId, riskEvent.sessionId)
                && riskLevel == riskEvent.riskLevel
                && Objects.equals(status, riskEvent.status)
                && Objects.equals(timestamp, riskEvent.timestamp)
                && Objects.equals(metadata, riskEvent.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, userId, sessionId, riskScore, riskLevel, status, timestamp, metadata);
    }
}
