package com.fourguard.wms.application.usecase;

import com.fourguard.wms.application.dto.request.auth.ForgotPasswordRequest;
import com.fourguard.wms.domain.ports.in.ForgotPasswordUseCase;
import com.fourguard.wms.domain.ports.out.UserRepositoryPort;
import com.fourguard.wms.infrastructure.persistence.entity.UserEntity;
import com.fourguard.wms.infrastructure.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForgotPasswordUseCaseImpl implements ForgotPasswordUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final JwtService jwtService;

    @Override
    public void requestPasswordReset(ForgotPasswordRequest request) {
        log.info("[AUTH] Forgot password request for: {}", request.getEmail());

        // Standard security practice: Do not throw error if email does not exist,
        // to prevent email enumeration attacks. Just log and return.
        UserEntity user = userRepositoryPort.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            log.warn("[AUTH] Forgot password email not found: {}", request.getEmail());
            return;
        }

        // Generate one-time reset token (15 min validity)
        String resetToken = jwtService.generateResetPasswordToken(user.getEmail());

        // In a real system, send this token via email (e.g. SMTP or AWS SES)
        // For development, print to console/log
        log.info("[SMTP MOCK] Enviando correo de restablecimiento a: {}. Token: {}", user.getEmail(), resetToken);
    }
}
