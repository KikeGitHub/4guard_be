package com.fourguard.wms.domain.enums;

/**
 * Operational status of a Supplier.
 * Aligned with CHECK constraint: wms.suppliers.status IN ('ACTIVE','INACTIVE','BLOCKED')
 */
public enum SupplierStatus {
    ACTIVE,
    INACTIVE,
    BLOCKED
}
