package com.fourguard.wms.domain.ports.in;

import com.fourguard.wms.application.dto.UserResponse;
import com.fourguard.wms.application.dto.UserUpdateRequest;

/**
 * Port IN — Update User Use Case.
 */
public interface UpdateUserUseCase {
    UserResponse updateUser(UserUpdateRequest request);
}
