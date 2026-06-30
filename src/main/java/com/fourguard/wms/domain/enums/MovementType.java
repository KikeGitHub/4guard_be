package com.fourguard.wms.domain.enums;

/** Type of inventory movement operation. */
public enum MovementType {
    ENTRY,
    EXIT,
    TRANSFER,
    ADJUSTMENT,
    RETURN,
    RELOCATION,
    QUARANTINE,
    RELEASE
}
