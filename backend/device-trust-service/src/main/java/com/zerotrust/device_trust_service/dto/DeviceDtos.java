package com.zerotrust.device_trust_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

public class DeviceDtos {
    public record FingerprintRequest(
            @NotNull String userId,
            @NotNull String deviceId,
            @NotEmpty List<Double> features,
            Map<String, Object> metadata
    ) { }

    public record TrustResult(String clazz, double confidence, double trustScore) { }
}
