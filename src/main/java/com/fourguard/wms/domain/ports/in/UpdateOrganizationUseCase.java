package com.fourguard.wms.domain.ports.in;

import com.fourguard.wms.application.dto.request.UpdateOrganizationRequest;
import com.fourguard.wms.application.dto.response.OrganizationResponse;

/** Port IN — Update Organization Use Case. */
public interface UpdateOrganizationUseCase {
    OrganizationResponse updateOrganization(UpdateOrganizationRequest request);
}
