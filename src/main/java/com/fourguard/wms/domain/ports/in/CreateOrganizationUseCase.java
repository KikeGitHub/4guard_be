package com.fourguard.wms.domain.ports.in;

import com.fourguard.wms.application.dto.request.CreateOrganizationRequest;
import com.fourguard.wms.application.dto.response.OrganizationResponse;

/** Port IN — Create Organization Use Case. */
public interface CreateOrganizationUseCase {
    OrganizationResponse createOrganization(CreateOrganizationRequest request);
}
