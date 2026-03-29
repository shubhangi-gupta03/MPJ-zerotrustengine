package com.zerotrust.common.auth;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public class UserPrincipal implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String userId;
    private String email;
    private String role;
    private boolean mfaVerified;
    private String sessionId;
    private Instant issuedAt;

    public UserPrincipal() {
    }

    public UserPrincipal(String userId, String email, String role, boolean mfaVerified, String sessionId, Instant issuedAt) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.mfaVerified = mfaVerified;
        this.sessionId = sessionId;
        this.issuedAt = issuedAt;
    }

    public static UserPrincipal of(
            String userId,
            String email,
            String role,
            boolean mfaVerified,
            String sessionId,
            Instant issuedAt
    ) {
        return new UserPrincipal(userId, email, role, mfaVerified, sessionId, issuedAt);
    }

    public boolean hasRole(String requiredRole) {
        return role != null && role.equalsIgnoreCase(requiredRole);
    }

    public boolean isSessionActive() {
        return sessionId != null && !sessionId.isBlank();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isMfaVerified() {
        return mfaVerified;
    }

    public void setMfaVerified(boolean mfaVerified) {
        this.mfaVerified = mfaVerified;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Instant issuedAt) {
        this.issuedAt = issuedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserPrincipal that)) {
            return false;
        }
        return mfaVerified == that.mfaVerified
                && Objects.equals(userId, that.userId)
                && Objects.equals(email, that.email)
                && Objects.equals(role, that.role)
                && Objects.equals(sessionId, that.sessionId)
                && Objects.equals(issuedAt, that.issuedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, email, role, mfaVerified, sessionId, issuedAt);
    }

    @Override
    public String toString() {
        return "UserPrincipal{"
                + "userId='" + userId + '\''
                + ", email='" + email + '\''
                + ", role='" + role + '\''
                + ", mfaVerified=" + mfaVerified
                + ", sessionId='" + sessionId + '\''
                + ", issuedAt=" + issuedAt
                + '}';
    }
}
