package com.fourguard.wms.domain.model;

import com.fourguard.wms.domain.enums.InventoryState;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class InventoryItem {
    private UUID id;
    private Organization organization;
    private Branch branch;
    private Client client;
    private String sscc;
    private String externalUa;
    private ProductSku sku;
    private Location location;
    private InventoryState state;
    private BigDecimal quantity;
    private String batchNumber;
    private LocalDate manufacturingDate;
    private LocalDate expirationDate;
    private String sapFolio;
    private String quarantineReason;
    private Map<String, Object> metadata;
    private Long version;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
