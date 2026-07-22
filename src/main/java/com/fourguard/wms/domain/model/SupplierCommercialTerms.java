package com.fourguard.wms.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Domain model for the commercial terms of a Supplier (1:1). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SupplierCommercialTerms {
    private UUID id;
    /** Lead time in days (delivery time for goods / response time for services). */
    private Integer leadTimeDays;
    private BigDecimal minimumOrderAmount;
    private Integer creditDays;
    /** ISO 4217 currency code (e.g. 'MXN', 'USD', 'EUR'). FK → wms.cat_currencies. */
    private String currencyCode;
    private Boolean qualityInspectionRequired;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
