package com.fourguard.wms.domain.ports.in;

import java.util.UUID;

/**
 * Port IN — Delete User Use Case.
 */
public interface DeleteUserUseCase {
    void deleteUser(UUID id);
}
