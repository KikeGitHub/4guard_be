package com.fourguard.wms.domain.ports.in;

import com.fourguard.wms.application.dto.request.auth.LoginRequest;
import com.fourguard.wms.application.dto.response.auth.AuthResponse;

public interface LoginUseCase {
    AuthResponse login(LoginRequest request);
}
