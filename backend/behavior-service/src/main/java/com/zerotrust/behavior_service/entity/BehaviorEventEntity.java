package com.zerotrust.behavior_service.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "behavior_events")
public class BehaviorEventEntity {
    @Id
    private String id;
    private String userId;
    private String sessionId;
    private int actionId;
    private double velocity;
    private double geoDistance;
    private double interactionRate;
    private Instant eventTime;
    private List<Double> features = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public int getActionId() { return actionId; }
    public void setActionId(int actionId) { this.actionId = actionId; }
    public double getVelocity() { return velocity; }
    public void setVelocity(double velocity) { this.velocity = velocity; }
    public double getGeoDistance() { return geoDistance; }
    public void setGeoDistance(double geoDistance) { this.geoDistance = geoDistance; }
    public double getInteractionRate() { return interactionRate; }
    public void setInteractionRate(double interactionRate) { this.interactionRate = interactionRate; }
    public Instant getEventTime() { return eventTime; }
    public void setEventTime(Instant eventTime) { this.eventTime = eventTime; }
    public List<Double> getFeatures() { return features; }
    public void setFeatures(List<Double> features) { this.features = features; }
}
