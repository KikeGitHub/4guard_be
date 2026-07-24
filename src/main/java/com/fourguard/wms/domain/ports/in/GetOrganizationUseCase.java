package com.fourguard.wms.domain.ports.in;

import com.fourguard.wms.application.dto.response.OrganizationResponse;
import com.fourguard.wms.application.dto.response.audit.OrganizationAuditResponse;

import java.util.List;
import java.util.UUID;

/** Port IN — Get Organization Use Case. */
public interface GetOrganizationUseCase {
    OrganizationResponse getOrganizationById(UUID id);
    List<OrganizationResponse> getAllOrganizations();
    List<OrganizationAuditResponse> getOrganizationAuditLogs(UUID id);
}
