package com.fourguard.wms.domain.ports.in;

import com.fourguard.wms.application.dto.request.auth.LogoutRequest;

/** Port of entry for secure user logout. */
public interface LogoutUseCase {
    void logout(LogoutRequest request, String currentUsername);
}
