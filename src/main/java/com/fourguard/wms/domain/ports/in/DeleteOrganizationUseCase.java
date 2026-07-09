package com.fourguard.wms.domain.ports.in;

import java.util.UUID;

/** Port IN — Delete Organization Use Case. */
public interface DeleteOrganizationUseCase {
    void deleteOrganization(UUID id);
}
