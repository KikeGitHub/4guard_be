package com.fourguard.wms.infrastructure.security.jwt;

import com.fourguard.wms.infrastructure.persistence.entity.PermissionEntity;
import com.fourguard.wms.infrastructure.persistence.entity.RoleEntity;
import com.fourguard.wms.infrastructure.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;

    @Mock
    private JwtProperties jwtProperties;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        // Enforce mock properties with lenient strictness to avoid Mockito strictness failure
        lenient().when(jwtProperties.getSecret()).thenReturn("4guard-super-secret-key-for-test-environments-minimum-256-bits-length");
        lenient().when(jwtProperties.getAccessTokenExpiration()).thenReturn(3600000L); // 1 hour
        lenient().when(jwtProperties.getRefreshTokenExpiration()).thenReturn(604800000L); // 7 days

        jwtService = new JwtService(jwtProperties);

        PermissionEntity p1 = PermissionEntity.builder().name("READ").build();
        RoleEntity role = RoleEntity.builder()
                .name("ADMIN")
                .permissions(Set.of(p1))
                .build();

        user = UserEntity.builder()
                .id(UUID.randomUUID())
                .username("enrique")
                .email("enrique@4guard.com")
                .role(role)
                .build();
    }

    @Test
    void whenGenerateAccessToken_thenExtractUsernameReturnsCorrectSubject() {
        // Act
        String token = jwtService.generateAccessToken(user);

        // Assert
        assertNotNull(token);
        String username = jwtService.extractUsername(token);
        assertEquals("enrique", username);
        assertFalse(jwtService.isTokenExpired(token));
        assertTrue(jwtService.isTokenValid(token, "enrique"));
    }

    @Test
    void whenTokenIsGenerated_thenClaimsCanBeExtracted() {
        // Act
        String token = jwtService.generateAccessToken(user);

        // Assert
        String emailClaim = jwtService.extractClaim(token, claims -> claims.get("email", String.class));
        String roleClaim = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
        
        assertEquals("enrique@4guard.com", emailClaim);
        assertEquals("ADMIN", roleClaim);
    }

    @Test
    void whenGenerateRefreshToken_thenExtractSubjectSuccessAndNoCustomClaimsPresent() {
        // Act
        String refreshToken = jwtService.generateRefreshToken(user);

        // Assert
        assertNotNull(refreshToken);
        assertEquals("enrique", jwtService.extractUsername(refreshToken));
        
        String emailClaim = jwtService.extractClaim(refreshToken, claims -> claims.get("email", String.class));
        assertNull(emailClaim); // Refresh token should be clean/empty claims
    }
}
