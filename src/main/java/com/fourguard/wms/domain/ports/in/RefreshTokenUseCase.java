package com.fourguard.wms.domain.ports.in;

import com.fourguard.wms.application.dto.request.auth.RefreshTokenRequest;
import com.fourguard.wms.application.dto.response.auth.AuthResponse;

public interface RefreshTokenUseCase {
    AuthResponse refresh(RefreshTokenRequest request);
}
