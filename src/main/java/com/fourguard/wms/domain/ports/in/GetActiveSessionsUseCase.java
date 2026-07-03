package com.fourguard.wms.domain.ports.in;

import com.fourguard.wms.application.dto.response.audit.ActiveSessionResponse;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

/**
 * Port IN — Use-case contract for retrieving active user sessions.
 */
public interface GetActiveSessionsUseCase {
    List<ActiveSessionResponse> getActiveSessions(UUID organizationId, UUID branchId, Principal principal);
}
