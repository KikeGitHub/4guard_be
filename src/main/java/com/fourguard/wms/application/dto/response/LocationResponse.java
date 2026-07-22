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

    // ── New FSM fields ──────────────────────────────────────────────────────
    /** Human-readable unique code, e.g. "ALMC-A-R1-N2". Null if not assigned. */
    private final String code;

    /** Descriptive name, e.g. "Pasillo A – Rack 1 – Nivel 2". */
    private final String name;

    /** FSM operational status: ACTIVE | BLOCKED | MAINTENANCE | INACTIVE. */
    private final String status;

    /** Reason for the current status. Present for BLOCKED and MAINTENANCE. */
    private final String statusReason;

    // ── Legacy fields kept for backwards-compatibility ──────────────────────
    /** Derived field: true when status == BLOCKED. */
    private final Boolean isBlocked;

    /** Derived field: equals statusReason when status == BLOCKED, null otherwise. */
    private final String blockReason;

    private final Long version;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;
}

