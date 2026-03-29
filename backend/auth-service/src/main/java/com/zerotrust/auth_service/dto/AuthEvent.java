package com.zerotrust.auth_service.dto;

import java.time.Instant;

public record AuthEvent(
        String usernameOrEmail,
        String eventType,
        boolean success,
        double anomalyScore,
        String ipAddress,
        Instant timestamp
) {
}
