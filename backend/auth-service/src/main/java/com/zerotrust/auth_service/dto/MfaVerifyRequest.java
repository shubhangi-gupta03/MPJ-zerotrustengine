package com.zerotrust.auth_service.dto;

import jakarta.validation.constraints.NotBlank;

public record MfaVerifyRequest(
        @NotBlank String usernameOrEmail,
        @NotBlank String otp
) {
}
