package com.fourguard.wms.application.dto.response;

import com.fourguard.wms.domain.enums.LocationType;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Response DTO for Location. */
@Getter
@Builder
public class LocationResponse {
    private final UUID id;
    private final UUID branchId;
    private final String branchName;
    private final UUID sectionId;
    private final String sectionName;
    private final String zone;
    private final String aisle;
    private final String rack;
    private final Integer level;
    private final String position;
    private final Integer coordX;
    private final Integer coordY;
    private final Integer coordZ;
    private final LocationType type;
    private final Integer capacityUnits;
    private final Integer currentOccupancy;
    private final Boolean isBlocked;
    private final String blockReason;
    private final Long version;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;
}
