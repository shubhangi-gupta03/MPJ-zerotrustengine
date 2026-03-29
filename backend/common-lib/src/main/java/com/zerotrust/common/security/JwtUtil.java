package com.zerotrust.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

public final class JwtUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);

    private static final String ENV_PUBLIC_KEY_PATH = "JWT_PUBLIC_KEY_PATH";
    private static final String ENV_PUBLIC_KEY = "JWT_PUBLIC_KEY";
    private static final String PEM_HEADER = "-----BEGIN PUBLIC KEY-----";
    private static final String PEM_FOOTER = "-----END PUBLIC KEY-----";

    private final RSAPublicKey publicKey;

    public JwtUtil(RSAPublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public static JwtUtil fromEnvironment() {
        return new JwtUtil(loadPublicKeyFromEnvironment());
    }

    public Claims parseAndValidate(String jwtToken) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(jwtToken);
            return jws.getPayload();
        } catch (JwtException ex) {
            throw new IllegalArgumentException("JWT validation failed", ex);
        }
    }

    public String getSubject(String jwtToken) {
        return parseAndValidate(jwtToken).getSubject();
    }

    public String getStringClaim(String jwtToken, String claimName) {
        Object claim = parseAndValidate(jwtToken).get(claimName);
        return claim != null ? String.valueOf(claim) : null;
    }

    public Instant getIssuedAt(String jwtToken) {
        Date issuedAt = parseAndValidate(jwtToken).getIssuedAt();
        return issuedAt != null ? issuedAt.toInstant() : null;
    }

    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    private static RSAPublicKey loadPublicKeyFromEnvironment() {
        String keyPath = System.getenv(ENV_PUBLIC_KEY_PATH);
        String pem = null;

        if (keyPath != null && !keyPath.isBlank()) {
            try {
                pem = Files.readString(Path.of(keyPath)).trim();
                LOGGER.info("Loaded JWT public key from path in {}", ENV_PUBLIC_KEY_PATH);
            } catch (IOException ex) {
                throw new IllegalStateException("Failed to read JWT public key from path: " + keyPath, ex);
            }
        }

        if (pem == null || pem.isBlank()) {
            pem = System.getenv(ENV_PUBLIC_KEY);
            if (pem != null) {
                pem = pem.trim();
                LOGGER.info("Loaded JWT public key from {}", ENV_PUBLIC_KEY);
            }
        }

        if (pem == null || pem.isBlank()) {
            throw new IllegalStateException(
                    "JWT public key not configured. Set " + ENV_PUBLIC_KEY_PATH + " or " + ENV_PUBLIC_KEY
            );
        }

        return parseRsaPublicKey(pem);
    }

    private static RSAPublicKey parseRsaPublicKey(String pem) {
        String normalized = pem
                .replace(PEM_HEADER, "")
                .replace(PEM_FOOTER, "")
                .replaceAll("\\s", "");

        try {
            byte[] encoded = Base64.getDecoder().decode(normalized);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey key = keyFactory.generatePublic(new X509EncodedKeySpec(encoded));
            if (!(key instanceof RSAPublicKey rsaPublicKey)) {
                throw new IllegalArgumentException("Configured key is not an RSA public key");
            }
            return rsaPublicKey;
        } catch (IllegalArgumentException | GeneralSecurityException ex) {
            throw new IllegalStateException("Invalid RSA public key format", ex);
        }
    }
}
