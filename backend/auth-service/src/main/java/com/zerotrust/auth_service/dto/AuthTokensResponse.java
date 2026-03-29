package com.zerotrust.auth_service.dto;

public record AuthTokensResponse(
        String accessToken,
        String refreshToken,
        long accessExpiresInSeconds,
        long refreshExpiresInSeconds,
        boolean mfaRequired
) {
}
