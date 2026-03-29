package com.zerotrust.auth_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.jwt")
public class JwtProperties {
    private String issuer;
    private String accessPrivateKeyPath;
    private String accessPublicKeyPath;
    private String refreshPrivateKeyPath;
    private String refreshPublicKeyPath;
    private long accessTtlMinutes;
    private long refreshTtlDays;

    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }
    public String getAccessPrivateKeyPath() { return accessPrivateKeyPath; }
    public void setAccessPrivateKeyPath(String accessPrivateKeyPath) { this.accessPrivateKeyPath = accessPrivateKeyPath; }
    public String getAccessPublicKeyPath() { return accessPublicKeyPath; }
    public void setAccessPublicKeyPath(String accessPublicKeyPath) { this.accessPublicKeyPath = accessPublicKeyPath; }
    public String getRefreshPrivateKeyPath() { return refreshPrivateKeyPath; }
    public void setRefreshPrivateKeyPath(String refreshPrivateKeyPath) { this.refreshPrivateKeyPath = refreshPrivateKeyPath; }
    public String getRefreshPublicKeyPath() { return refreshPublicKeyPath; }
    public void setRefreshPublicKeyPath(String refreshPublicKeyPath) { this.refreshPublicKeyPath = refreshPublicKeyPath; }
    public long getAccessTtlMinutes() { return accessTtlMinutes; }
    public void setAccessTtlMinutes(long accessTtlMinutes) { this.accessTtlMinutes = accessTtlMinutes; }
    public long getRefreshTtlDays() { return refreshTtlDays; }
    public void setRefreshTtlDays(long refreshTtlDays) { this.refreshTtlDays = refreshTtlDays; }
}
