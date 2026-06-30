package com.fourguard.wms.domain.ports.in;

import java.util.UUID;

/** Port of entry for an administrator to reset a user's password to a temporary one. */
public interface ResetUserPasswordUseCase {
    String resetToTemporaryPassword(UUID userId, String adminUsername);
}
