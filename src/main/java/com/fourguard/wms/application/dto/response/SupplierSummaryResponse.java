package com.fourguard.wms.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Reduced response DTO for the supplier list (GET /api/suppliers with pagination).
 * Excludes nested address and commercialTerms to keep the list payload light.
 * Full detail is available via GET /api/suppliers/{id}.
 */
@Getter
@Builder
public class SupplierSummaryResponse {

    private final UUID id;
    private final String code;
    private final String legalName;
    private final String commercialName;
    private final String taxId;
    private final String type;
    private final Boolean preferred;
    private final String status;
    private final String scopeType;
    private final UUID clientId;
    /** Mapped from branch_id. Named warehouseId for FE compatibility. */
    private final UUID warehouseId;
    private final Boolean active;
    private final OffsetDateTime updatedAt;

    /** Contact summary for list display (email + phone). */
    private final ContactSummary contact;

    @Builder
    public static class ContactSummary {
        public final String fullName;
        public final String email;
        public final String phone;
    }
}
