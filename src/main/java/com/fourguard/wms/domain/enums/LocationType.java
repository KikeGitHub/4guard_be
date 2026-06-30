package com.fourguard.wms.domain.enums;

/** Physical type of a warehouse Location. Maps to the SQL CHECK constraint. */
public enum LocationType {
    PALLET,
    BIN,
    SHELF,
    RAMP
}
