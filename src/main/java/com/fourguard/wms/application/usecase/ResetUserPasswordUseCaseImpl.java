package com.fourguard.wms.application.usecase;

import com.fourguard.wms.domain.exception.EntityNotFoundException;
import com.fourguard.wms.domain.ports.in.ResetUserPasswordUseCase;
import com.fourguard.wms.domain.ports.out.UserRepositoryPort;
import com.fourguard.wms.infrastructure.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResetUserPasswordUseCaseImpl implements ResetUserPasswordUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public String resetToTemporaryPassword(UUID userId, String adminUsername) {
        log.info("[AUTH] Admin '{}' requested temporary password reset for user ID: {}", adminUsername, userId);

        UserEntity userEntity = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        // Generate temporary password with complexity: 4G-<8 chars hex>-*
        String tempPassword = "4G-" + UUID.randomUUID().toString().substring(0, 8) + "*";
        
        userEntity.setPassword(passwordEncoder.encode(tempPassword));
        userEntity.setChangePasswordRequired(true);
        userEntity.setUpdatedAt(OffsetDateTime.now());
        userEntity.setUpdatedBy(adminUsername);

        userRepositoryPort.save(userEntity);

        log.info("[AUDIT] Temporary password generated successfully for user ID: {}", userId);
        return tempPassword;
    }
}
