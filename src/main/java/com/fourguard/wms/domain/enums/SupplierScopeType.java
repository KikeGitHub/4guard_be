package com.fourguard.wms.domain.enums;

/**
 * Scope type for 3PL visibility of a Supplier.
 * Aligned with CHECK constraint: wms.suppliers.scope_type IN ('GLOBAL','CLIENT','WAREHOUSE')
 * <ul>
 *   <li>GLOBAL    — visible to the entire organization</li>
 *   <li>CLIENT    — visible only for a specific client owner (client_id required)</li>
 *   <li>WAREHOUSE — visible only for a specific branch/warehouse (branch_id required)</li>
 * </ul>
 */
public enum SupplierScopeType {
    GLOBAL,
    CLIENT,
    WAREHOUSE
}
