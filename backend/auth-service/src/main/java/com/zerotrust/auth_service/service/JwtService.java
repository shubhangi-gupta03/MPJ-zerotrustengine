package com.zerotrust.auth_service.service;

import com.zerotrust.auth_service.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.zerotrust.auth_service.exception.AuthException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {
    private final JwtProperties properties;
    private final PrivateKey accessPrivateKey;
    private final PublicKey accessPublicKey;
    private final PrivateKey refreshPrivateKey;
    private final PublicKey refreshPublicKey;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        this.accessPrivateKey = loadPrivateKey(properties.getAccessPrivateKeyPath());
        this.accessPublicKey = loadPublicKey(properties.getAccessPublicKeyPath());
        this.refreshPrivateKey = loadPrivateKey(properties.getRefreshPrivateKeyPath());
        this.refreshPublicKey = loadPublicKey(properties.getRefreshPublicKeyPath());
    }

    public String issueAccessToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant expiry = now.plus(properties.getAccessTtlMinutes(), ChronoUnit.MINUTES);
        return Jwts.builder()
                .subject(subject)
                .issuer(properties.getIssuer())
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .claims(claims)
                .signWith(accessPrivateKey, Jwts.SIG.RS256)
                .compact();
    }

    public String issueRefreshToken(String subject) {
        Instant now = Instant.now();
        Instant expiry = now.plus(properties.getRefreshTtlDays(), ChronoUnit.DAYS);
        return Jwts.builder()
                .subject(subject)
                .issuer(properties.getIssuer())
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(refreshPrivateKey, Jwts.SIG.RS256)
                .compact();
    }

    public Claims parseAccessToken(String token) {
        return Jwts.parser().verifyWith((java.security.interfaces.RSAPublicKey) accessPublicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Claims parseRefreshToken(String token) {
        return Jwts.parser().verifyWith((java.security.interfaces.RSAPublicKey) refreshPublicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private PrivateKey loadPrivateKey(String path) {
        if (path == null || path.isBlank()) {
            throw new AuthException(HttpStatus.INTERNAL_SERVER_ERROR, "JWT private key path missing");
        }
        try {
            String pem = Files.readString(Path.of(path));
            byte[] bytes = Base64.getDecoder().decode(pem.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", ""));
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(bytes));
        } catch (IOException | GeneralSecurityException ex) {
            throw new AuthException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to load JWT private key");
        }
    }

    private PublicKey loadPublicKey(String path) {
        if (path == null || path.isBlank()) {
            throw new AuthException(HttpStatus.INTERNAL_SERVER_ERROR, "JWT public key path missing");
        }
        try {
            String pem = Files.readString(Path.of(path));
            byte[] bytes = Base64.getDecoder().decode(pem.replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", ""));
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytes));
        } catch (IOException | GeneralSecurityException ex) {
            throw new AuthException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to load JWT public key");
        }
    }
}
