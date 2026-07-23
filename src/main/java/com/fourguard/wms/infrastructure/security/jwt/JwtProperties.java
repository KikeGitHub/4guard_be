package com.fourguard.wms.infrastructure.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT configuration properties bound from {@code security.jwt.*} in application.yml.
 *
 * <p>Centralises JWT settings so they can be injected wherever needed without
 * hard-coding values. The {@code secret} must be at least 256 bits (32 chars)
 * for the HS256 algorithm used by jjwt.</p>
 *
 * <p>Registered as a Spring component so it is picked up by component scanning.
 * {@code @ConfigurationPropertiesScan} on the main class also covers this bean.</p>
 */
@Component
@ConfigurationProperties(prefix = "security.jwt")
@Getter
@Setter
public class JwtProperties {

    /**
     * HMAC-SHA256 signing secret. Must be at least 256 bits.
     * In production, set via the {@code JWT_SECRET} environment variable.
     */
    private String secret;

    /**
     * Access token validity in milliseconds. Default: 1800000 (30 minutes).
     */
    private long accessTokenExpiration = 1_800_000L;

    /**
     * Refresh token validity in milliseconds. Default: 604800000 (7 days).
     */
    private long refreshTokenExpiration = 604_800_000L;
}

