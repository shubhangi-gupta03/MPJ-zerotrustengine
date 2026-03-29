package com.zerotrust.auth_service.dto;

public record SessionStatusResponse(boolean active, String reason) {
}
