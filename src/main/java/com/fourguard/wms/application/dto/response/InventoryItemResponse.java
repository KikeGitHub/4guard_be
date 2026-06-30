package com.fourguard.wms.application.dto.response;

import com.fourguard.wms.domain.enums.InventoryState;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/** Response DTO for an inventory item. */
@Getter
@Builder
public class InventoryItemResponse {
    private final UUID          id;
    private final String        sscc;
    private final String        externalUa;
    private final InventoryState state;
    private final BigDecimal    quantity;
    private final String        batchNumber;
    private final LocalDate     manufacturingDate;
    private final LocalDate     expirationDate;
    private final String        sapFolio;
    private final String        quarantineReason;
    private final Map<String, Object> metadata;

    // Embedded references (IDs only — no nested objects to avoid circular serialization)
    private final UUID   organizationId;
    private final UUID   branchId;
    private final UUID   clientId;
    private final UUID   skuId;
    private final UUID   locationId;

    private final Long           version;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;
}
