package com.fourguard.wms.domain.model;

import com.fourguard.wms.domain.enums.LocationStatus;
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

    /** Human-readable unique code, e.g. "ALMC-A-R1-N2". May be null if not yet assigned. */
    private String code;

    /** Descriptive name, e.g. "Pasillo A – Rack 1 – Nivel 2". */
    private String name;

    /** FSM operational status. */
    private LocationStatus status;

    /** Reason for the last status change. Required when status = BLOCKED or MAINTENANCE. */
    private String statusReason;

    /** Observations / notes for the location. */
    private String notes;

    // ── Legacy fields kept for backwards-compatibility ──────────────────────
    /** Derived from status: true only when status == BLOCKED. */
    private Boolean isBlocked;

    /** Derived from statusReason when status == BLOCKED. */
    private String blockReason;

    private Long version;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
