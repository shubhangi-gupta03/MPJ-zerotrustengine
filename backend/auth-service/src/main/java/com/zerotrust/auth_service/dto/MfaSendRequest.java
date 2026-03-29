package com.zerotrust.auth_service.dto;

import jakarta.validation.constraints.NotBlank;

public record MfaSendRequest(@NotBlank String usernameOrEmail) {
}
