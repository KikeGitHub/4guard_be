package com.fourguard.wms.domain.ports.in;

import com.fourguard.wms.application.dto.UserResponse;

import java.util.List;
import java.util.UUID;

/**
 * Port IN — Retrieve User(s) Use Case.
 */
public interface GetUserUseCase {
    UserResponse getUserById(UUID id);
    List<UserResponse> getAllUsers();
}
