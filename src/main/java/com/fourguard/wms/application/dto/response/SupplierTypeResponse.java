package com.fourguard.wms.application.dto.response;

import lombok.Builder;
import lombok.Getter;

/** Response DTO for a supplier type catalog entry (GET /api/suppliers/catalogs/types). */
@Getter
@Builder
public class SupplierTypeResponse {
    private final String code;
    private final String labelEs;
    private final String labelEn;
    private final Boolean isService;
    private final Integer sortOrder;
}
