package com.fourguard.wms.application.usecase;

import com.fourguard.wms.application.dto.request.auth.LoginRequest;
import com.fourguard.wms.application.dto.response.auth.AuthResponse;
import com.fourguard.wms.application.dto.response.auth.UserInfoResponse;
import com.fourguard.wms.application.mapper.UserMapper;
import com.fourguard.wms.domain.exception.InvalidCredentialsException;
import com.fourguard.wms.domain.ports.in.LoginUseCase;
import com.fourguard.wms.domain.ports.out.UserRepositoryPort;
import com.fourguard.wms.infrastructure.persistence.entity.UserEntity;
import com.fourguard.wms.infrastructure.security.jwt.JwtProperties;
import com.fourguard.wms.infrastructure.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginUseCaseImpl implements LoginUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("[AUTH] Attempting login for: {}", request.getIdentifier());

        // Find user by username or email
        UserEntity user = userRepositoryPort.findByUsernameOrEmail(request.getIdentifier())
                .orElseThrow(() -> new InvalidCredentialsException("Credenciales incorrectas"));

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("[AUTH] Password mismatch for user: {}", user.getUsername());
            throw new InvalidCredentialsException("Credenciales incorrectas");
        }

        // Check if account is active/enabled
        if (!user.getIsEnabled() || !"ACTIVE".equals(user.getStatus().name())) {
            log.warn("[AUTH] Account disabled or pending: {}", user.getUsername());
            throw new InvalidCredentialsException("La cuenta está desactivada o pendiente de activación");
        }

        // Update last login
        user.setLastLogin(OffsetDateTime.now(ZoneOffset.UTC));
        userRepositoryPort.save(user);

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Expiration timestamp
        LocalDateTime expiresAt = LocalDateTime.now().plusNanos(jwtProperties.getAccessTokenExpiration() * 1_000_000L);

        // Map user details to DTO
        UserInfoResponse userInfo = userMapper.toUserInfoResponse(user);

        log.info("[AUTH] Successful login for: {}", user.getUsername());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresAt(expiresAt)
                .user(userInfo)
                .build();
    }
}
