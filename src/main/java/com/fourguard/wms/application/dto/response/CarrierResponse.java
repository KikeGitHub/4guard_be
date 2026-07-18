package com.fourguard.wms.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Response DTO for Carrier. */
@Getter
@Builder
public class CarrierResponse {
    private final UUID id;
    private final UUID organizationId;
    private final String organizationName;
    private final String name;
    private final String tradeName;
    private final String taxId;
    private final String contactName;
    private final String contactPhone;
    private final String contactEmail;
    private final String status;
    private final Long version;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;
}
