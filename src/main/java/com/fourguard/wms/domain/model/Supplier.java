package com.fourguard.wms.domain.model;

import com.fourguard.wms.domain.enums.SupplierScopeType;
import com.fourguard.wms.domain.enums.SupplierStatus;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Domain model for a Supplier — pure domain object, no JPA annotations. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Supplier {

    // --- Identity ---
    private UUID id;
    private Organization organization;
    /** Sequential supplier code per organization. Format: 'PRV-0001'. */
    private String code;

    // --- Fiscal Information ---
    private String legalName;
    private String commercialName;
    private String taxId;

    // --- Classification ---
    /** FK code → wms.cat_supplier_types (String kept for extensibility without redeploy). */
    private String supplierTypeCode;
    private Boolean isPreferred;

    // --- Operational Status ---
    private SupplierStatus status;
    /** Mandatory when status is INACTIVE or BLOCKED. */
    private String statusReason;
    private OffsetDateTime statusChangedAt;
    private String statusChangedBy;

    // --- 3PL Scope ---
    private SupplierScopeType scopeType;
    /** Required when scopeType = CLIENT. */
    private Client client;
    /** Required when scopeType = WAREHOUSE. Maps wms.branches. */
    private Branch branch;

    // --- Notes ---
    private String notes;

    // --- 1:1 Relations (loaded eagerly in service) ---
    private SupplierContact contact;
    private SupplierAddress address;
    private SupplierCommercialTerms commercialTerms;

    // --- Soft Delete ---
    private Boolean isActive;
    private Boolean isDeleted;
    private OffsetDateTime deletedAt;
    private String deletedBy;

    // --- Audit / Control ---
    private Long version;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
