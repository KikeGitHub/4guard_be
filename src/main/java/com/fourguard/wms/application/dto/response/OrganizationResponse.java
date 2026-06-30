package com.fourguard.wms.application.dto.response;

import com.fourguard.wms.domain.enums.OrganizationStatus;
import com.fourguard.wms.domain.enums.OrganizationType;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/** Response DTO for Organization. Never exposes JPA entity internals. */
@Getter
@Builder
public class OrganizationResponse {
    private final UUID               id;
    private final String             name;
    private final String             code;
    private final String             taxId;
    private final OrganizationType   type;
    private final OrganizationStatus status;
    private final Map<String, Object> settings;
    private final Long               version;
    private final OffsetDateTime     createdAt;
    private final OffsetDateTime     updatedAt;
}
