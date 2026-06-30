package com.fourguard.wms.domain.ports.in;

import com.fourguard.wms.application.dto.request.auth.ForgotPasswordRequest;

public interface ForgotPasswordUseCase {
    void requestPasswordReset(ForgotPasswordRequest request);
}
