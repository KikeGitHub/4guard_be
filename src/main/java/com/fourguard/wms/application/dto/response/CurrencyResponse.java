package com.fourguard.wms.application.dto.response;

import lombok.Builder;
import lombok.Getter;

/** Response DTO for a currency catalog entry (GET /api/suppliers/catalogs/currencies). */
@Getter
@Builder
public class CurrencyResponse {
    private final String code;
    private final String label;
    private final String symbol;
}
