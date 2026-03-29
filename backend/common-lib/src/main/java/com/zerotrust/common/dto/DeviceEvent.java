package com.zerotrust.common.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DeviceEvent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String eventId;
    private String deviceId;
    private String userId;
    private String sessionId;
    private String trustStatus;
    private double trustScore;
    private Instant timestamp;
    private Map<String, Object> metadata = new HashMap<>();

    public DeviceEvent() {
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
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

    public String getTrustStatus() {
        return trustStatus;
    }

    public void setTrustStatus(String trustStatus) {
        this.trustStatus = trustStatus;
    }

    public double getTrustScore() {
        return trustScore;
    }

    public void setTrustScore(double trustScore) {
        this.trustScore = trustScore;
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
        if (!(o instanceof DeviceEvent that)) {
            return false;
        }
        return Double.compare(that.trustScore, trustScore) == 0
                && Objects.equals(eventId, that.eventId)
                && Objects.equals(deviceId, that.deviceId)
                && Objects.equals(userId, that.userId)
                && Objects.equals(sessionId, that.sessionId)
                && Objects.equals(trustStatus, that.trustStatus)
                && Objects.equals(timestamp, that.timestamp)
                && Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, deviceId, userId, sessionId, trustStatus, trustScore, timestamp, metadata);
    }
}
