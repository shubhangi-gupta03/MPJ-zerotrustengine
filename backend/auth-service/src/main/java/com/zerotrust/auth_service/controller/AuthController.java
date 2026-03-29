package com.zerotrust.auth_service.controller;

import com.zerotrust.auth_service.dto.*;
import com.zerotrust.auth_service.service.AuthService;
import com.zerotrust.auth_service.service.MfaService;
import com.zerotrust.common.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final MfaService mfaService;

    public AuthController(AuthService authService, MfaService mfaService) {
        this.authService = authService;
        this.mfaService = mfaService;
    }

    @PostMapping("/register")
    public ApiResponse<Object> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ApiResponse.created("User registered", null);
    }

    @PostMapping("/login")
    public ApiResponse<AuthTokensResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        AuthTokensResponse tokens = authService.login(request, clientIp(servletRequest), servletRequest.getHeader("User-Agent"));
        return ApiResponse.ok("Login processed", tokens);
    }

    @PostMapping("/logout")
    public ApiResponse<Object> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
        return ApiResponse.ok("Logged out", null);
    }

    @PostMapping("/refresh-token")
    public ApiResponse<AuthTokensResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.ok("Token refreshed", authService.refreshToken(request));
    }

    @PostMapping("/mfa/send")
    public ApiResponse<Object> sendMfa(@Valid @RequestBody MfaSendRequest request) {
        mfaService.sendOtp(request.usernameOrEmail());
        return ApiResponse.ok("OTP sent", null);
    }

    @PostMapping("/mfa/verify")
    public ApiResponse<AuthTokensResponse> verifyMfa(@Valid @RequestBody MfaVerifyRequest request, HttpServletRequest servletRequest) {
        return ApiResponse.ok("MFA verified", authService.verifyMfaAndIssueTokens(request, clientIp(servletRequest)));
    }

    @GetMapping("/session/status")
    public ApiResponse<SessionStatusResponse> sessionStatus(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return ApiResponse.ok("Session status", authService.sessionStatus(token));
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
