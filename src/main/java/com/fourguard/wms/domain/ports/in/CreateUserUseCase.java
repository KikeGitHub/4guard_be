package com.fourguard.wms.domain.ports.in;

import com.fourguard.wms.application.dto.UserCreateRequest;
import com.fourguard.wms.application.dto.UserResponse;

/**
 * Port IN — Create User Use Case.
 */
public interface CreateUserUseCase {
    UserResponse createUser(UserCreateRequest request);
}
