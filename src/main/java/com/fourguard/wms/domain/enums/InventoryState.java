package com.fourguard.wms.domain.enums;

import java.util.Arrays;

/**
 * Lifecycle state of an InventoryItem.
 * Stored in the database as an INTEGER (CHECK constraint: 10,20,30,40,50,60,70,80).
 */
public enum InventoryState {

    RECEIVED(10),
    IN_QUALITY(20),
    AVAILABLE(30),
    RESERVED(40),
    DISPATCHED(50),
    DAMAGED(60),
    EXPIRED(70),
    RETURNED(80);

    private final int dbValue;

    InventoryState(int dbValue) {
        this.dbValue = dbValue;
    }

    public int getDbValue() {
        return dbValue;
    }

    public static InventoryState fromDbValue(int value) {
        return Arrays.stream(values())
                .filter(s -> s.dbValue == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown InventoryState db value: " + value));
    }
}
