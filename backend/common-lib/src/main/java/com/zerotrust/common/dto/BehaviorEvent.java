package com.zerotrust.common.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BehaviorEvent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String eventId;
    private String userId;
    private String sessionId;
    private String eventType;
    private double anomalyScore;
    private String status;
    private Instant timestamp;
    private Map<String, Object> metadata = new HashMap<>();

    public BehaviorEvent() {
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

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public double getAnomalyScore() {
        return anomalyScore;
    }

    public void setAnomalyScore(double anomalyScore) {
        this.anomalyScore = anomalyScore;
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
        if (!(o instanceof BehaviorEvent that)) {
            return false;
        }
        return Double.compare(that.anomalyScore, anomalyScore) == 0
                && Objects.equals(eventId, that.eventId)
                && Objects.equals(userId, that.userId)
                && Objects.equals(sessionId, that.sessionId)
                && Objects.equals(eventType, that.eventType)
                && Objects.equals(status, that.status)
                && Objects.equals(timestamp, that.timestamp)
                && Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, userId, sessionId, eventType, anomalyScore, status, timestamp, metadata);
    }
}
