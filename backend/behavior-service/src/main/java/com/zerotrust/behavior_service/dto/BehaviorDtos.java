package com.zerotrust.behavior_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

public class BehaviorDtos {
    public record IngestRequest(
            @NotNull String userId,
            String sessionId,
            int actionId,
            double velocity,
            double geoDistance,
            double interactionRate,
            Instant eventTime,
            @NotEmpty List<Double> features
    ) { }
}
