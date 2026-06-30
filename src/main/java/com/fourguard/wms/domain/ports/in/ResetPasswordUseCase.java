package com.fourguard.wms.domain.ports.in;

import com.fourguard.wms.application.dto.request.auth.ResetPasswordRequest;

public interface ResetPasswordUseCase {
    void resetPassword(ResetPasswordRequest request);
}
