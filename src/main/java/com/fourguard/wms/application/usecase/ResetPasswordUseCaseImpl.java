package com.fourguard.wms.application.usecase;

import com.fourguard.wms.application.dto.request.auth.ResetPasswordRequest;
import com.fourguard.wms.domain.exception.InvalidCredentialsException;
import com.fourguard.wms.domain.ports.in.ResetPasswordUseCase;
import com.fourguard.wms.domain.ports.out.UserRepositoryPort;
import com.fourguard.wms.infrastructure.persistence.entity.UserEntity;
import com.fourguard.wms.infrastructure.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResetPasswordUseCaseImpl implements ResetPasswordUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String token = request.getToken();

        try {
            // Extract email from reset token subject
            String email = jwtService.extractUsername(token);

            UserEntity user = userRepositoryPort.findByEmail(email)
                    .orElseThrow(() -> new InvalidCredentialsException("Usuario no encontrado"));

            // Verify expiration and token validity
            if (jwtService.isTokenExpired(token)) {
                throw new InvalidCredentialsException("El token ha expirado");
            }

            // Update user password
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepositoryPort.save(user);

            log.info("[AUTH] Password reset successfully for: {}", user.getUsername());

        } catch (Exception ex) {
            log.warn("[AUTH] Password reset failed: {}", ex.getMessage());
            throw new InvalidCredentialsException("Token de restablecimiento inválido o expirado");
        }
    }
}
