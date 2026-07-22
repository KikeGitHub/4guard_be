package com.fourguard.wms.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/** Response DTO for a supplier audit log entry — mirrors CarrierAuditResponse structure. */
@Getter
@Builder
public class SupplierAuditResponse {

    private final UUID logId;
    /** Action label: SUPPLIER_CREATED | SUPPLIER_UPDATED | SUPPLIER_STATUS_UPDATED | SUPPLIER_ARCHIVED */
    private final String action;
    private final String username;
    private final OffsetDateTime createdAt;
    private final List<AuditDetailResponse> details;

    @Value
    @Builder
    public static class AuditDetailResponse {
        String fieldName;
        String oldValue;
        String newValue;
    }
}
