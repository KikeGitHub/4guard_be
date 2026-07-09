package com.fourguard.wms.application.dto.response;

import com.fourguard.wms.domain.enums.BranchStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Response DTO for Branch. */
@Getter
@Builder
public class BranchResponse {
    private final UUID id;
    private final UUID organizationId;
    private final String organizationName;
    private final String name;
    private final String code;
    private final String timezone;
    private final String addressLine1;
    private final BranchStatus status;
    private final Long version;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;
}
