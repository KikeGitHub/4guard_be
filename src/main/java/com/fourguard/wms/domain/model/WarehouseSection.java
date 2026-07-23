package com.fourguard.wms.domain.model;

import com.fourguard.wms.domain.enums.WarehouseSectionStatus;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class WarehouseSection {
    private UUID id;
    private Branch branch;
    private String code;
    private String name;
    private WarehouseSectionStatus status;
    private Long version;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}

