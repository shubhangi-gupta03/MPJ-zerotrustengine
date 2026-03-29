package com.zerotrust.auth_service.service;

import com.zerotrust.auth_service.dto.*;
import com.zerotrust.auth_service.entity.LoginAttempt;
import com.zerotrust.auth_service.entity.RefreshToken;
import com.zerotrust.auth_service.entity.User;
import com.zerotrust.auth_service.exception.AuthException;
import com.zerotrust.auth_service.repository.LoginAttemptRepository;
import com.zerotrust.auth_service.repository.RefreshTokenRepository;
import com.zerotrust.auth_service.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
public class AuthService {
    private static final double MFA_SCORE_THRESHOLD = 0.6;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LoginAttemptRepository loginAttemptRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final LoginAnomalyScorer loginAnomalyScorer;
    private final SessionBlacklistService blacklistService;
    private final MfaService mfaService;
    private final AuthEventPublisher authEventPublisher;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            LoginAttemptRepository loginAttemptRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            LoginAnomalyScorer loginAnomalyScorer,
            SessionBlacklistService blacklistService,
            MfaService mfaService,
            AuthEventPublisher authEventPublisher
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.loginAttemptRepository = loginAttemptRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.loginAnomalyScorer = loginAnomalyScorer;
        this.blacklistService = blacklistService;
        this.mfaService = mfaService;
        this.authEventPublisher = authEventPublisher;
    }

    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new AuthException(HttpStatus.CONFLICT, "Username already exists");
        }
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new AuthException(HttpStatus.CONFLICT, "Email already exists");
        }
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);
    }

    @Transactional
    public AuthTokensResponse login(LoginRequest request, String ip, String userAgent) {
        User user = userRepository.findByUsernameOrEmail(request.usernameOrEmail(), request.usernameOrEmail())
                .orElseThrow(() -> new AuthException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        boolean passwordValid = passwordEncoder.matches(request.password(), user.getPasswordHash());
        float[] features = featureVector(ip, userAgent, request.usernameOrEmail());
        double score = loginAnomalyScorer.score(features);
        boolean mfaRequired = user.isMfaEnabled() || score > MFA_SCORE_THRESHOLD;

        saveAttempt(request.usernameOrEmail(), ip, userAgent, passwordValid, score, mfaRequired);
        authEventPublisher.publish(new AuthEvent(request.usernameOrEmail(), "LOGIN_ATTEMPT", passwordValid, score, ip, Instant.now()));

        if (!passwordValid) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        if (mfaRequired) {
            mfaService.sendOtp(request.usernameOrEmail());
            return new AuthTokensResponse(null, null, 0, 0, true);
        }
        return issueTokens(user);
    }

    @Transactional
    public AuthTokensResponse verifyMfaAndIssueTokens(MfaVerifyRequest request, String ip) {
        boolean verified = mfaService.verifyOtp(request.usernameOrEmail(), request.otp());
        if (!verified) {
            authEventPublisher.publish(new AuthEvent(request.usernameOrEmail(), "MFA_VERIFY", false, 0.0, ip, Instant.now()));
            throw new AuthException(HttpStatus.UNAUTHORIZED, "Invalid OTP");
        }
        User user = userRepository.findByUsernameOrEmail(request.usernameOrEmail(), request.usernameOrEmail())
                .orElseThrow(() -> new AuthException(HttpStatus.NOT_FOUND, "User not found"));
        authEventPublisher.publish(new AuthEvent(request.usernameOrEmail(), "MFA_VERIFY", true, 0.0, ip, Instant.now()));
        return issueTokens(user);
    }

    @Transactional
    public AuthTokensResponse refreshToken(RefreshTokenRequest request) {
        Claims claims = jwtService.parseRefreshToken(request.refreshToken());
        String tokenId = claims.getId();
        if (blacklistService.isBlacklisted(tokenId)) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "Refresh token blacklisted");
        }
        RefreshToken stored = refreshTokenRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new AuthException(HttpStatus.UNAUTHORIZED, "Refresh token not found"));
        if (stored.isRevoked() || stored.getExpiresAt().isBefore(Instant.now())) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "Refresh token expired or revoked");
        }
        User user = userRepository.findById(stored.getUserId())
                .orElseThrow(() -> new AuthException(HttpStatus.NOT_FOUND, "User not found"));
        return issueTokens(user);
    }

    @Transactional
    public void logout(LogoutRequest request) {
        Claims claims = jwtService.parseRefreshToken(request.refreshToken());
        String tokenId = claims.getId();
        RefreshToken stored = refreshTokenRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new AuthException(HttpStatus.UNAUTHORIZED, "Refresh token not found"));
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);
        Duration ttl = Duration.between(Instant.now(), stored.getExpiresAt());
        if (!ttl.isNegative()) {
            blacklistService.blacklist(tokenId, ttl);
        }
    }

    public SessionStatusResponse sessionStatus(String accessToken) {
        Claims claims = jwtService.parseAccessToken(accessToken);
        String jti = claims.getId();
        if (blacklistService.isBlacklisted(jti)) {
            return new SessionStatusResponse(false, "Token blacklisted");
        }
        return new SessionStatusResponse(true, "Session active");
    }

    private AuthTokensResponse issueTokens(User user) {
        String access = jwtService.issueAccessToken(user.getUsername(), Map.of("uid", user.getId()));
        String refresh = jwtService.issueRefreshToken(user.getUsername());
        Claims refreshClaims = jwtService.parseRefreshToken(refresh);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(user.getId());
        refreshToken.setTokenId(refreshClaims.getId());
        refreshToken.setTokenValue(refresh);
        refreshToken.setExpiresAt(refreshClaims.getExpiration().toInstant());
        refreshTokenRepository.save(refreshToken);

        long accessTtl = ChronoUnit.SECONDS.between(Instant.now(), jwtService.parseAccessToken(access).getExpiration().toInstant());
        long refreshTtl = ChronoUnit.SECONDS.between(Instant.now(), refreshClaims.getExpiration().toInstant());
        return new AuthTokensResponse(access, refresh, accessTtl, refreshTtl, false);
    }

    private void saveAttempt(String usernameOrEmail, String ip, String userAgent, boolean success, double score, boolean mfaRequired) {
        LoginAttempt attempt = new LoginAttempt();
        attempt.setUsernameOrEmail(usernameOrEmail);
        attempt.setIpAddress(ip);
        attempt.setUserAgent(userAgent == null ? "unknown" : userAgent);
        attempt.setSuccess(success);
        attempt.setAnomalyScore(score);
        attempt.setMfaRequired(mfaRequired);
        loginAttemptRepository.save(attempt);
    }

    private float[] featureVector(String ip, String userAgent, String usernameOrEmail) {
        int ipHash = Math.abs((ip == null ? "unknown" : ip).hashCode() % 1000);
        int uaHash = Math.abs((userAgent == null ? "unknown" : userAgent).hashCode() % 1000);
        int userHash = Math.abs(usernameOrEmail.hashCode() % 1000);
        int hour = Instant.now().atZone(java.time.ZoneOffset.UTC).getHour();
        return new float[]{
                ipHash / 1000f,
                uaHash / 1000f,
                userHash / 1000f,
                hour / 23f,
                (float) Math.random(),
                (float) Math.random()
        };
    }
}
