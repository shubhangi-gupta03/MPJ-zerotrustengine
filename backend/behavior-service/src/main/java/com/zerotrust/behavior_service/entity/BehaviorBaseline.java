package com.zerotrust.behavior_service.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "behavior_baselines")
public class BehaviorBaseline {
    @Id
    private String id;
    private String userId;
    private List<Double> centroid = new ArrayList<>();
    private int sampleSize;
    private Instant updatedAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public List<Double> getCentroid() { return centroid; }
    public void setCentroid(List<Double> centroid) { this.centroid = centroid; }
    public int getSampleSize() { return sampleSize; }
    public void setSampleSize(int sampleSize) { this.sampleSize = sampleSize; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
