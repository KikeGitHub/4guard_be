package com.fourguard.wms.domain.model;

import com.fourguard.wms.domain.enums.LocationType;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Location {
    private UUID id;
    private Branch branch;
    private WarehouseSection section;
    private String zone;
    private String aisle;
    private String rack;
    private Integer level;
    private String position;
    private Integer coordX;
    private Integer coordY;
    private Integer coordZ;
    private LocationType type;
    private Integer capacityUnits;
    private Integer currentOccupancy;
    private Boolean isBlocked;
    private String blockReason;
    private Long version;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
