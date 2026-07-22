package com.fourguard.wms.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Full response DTO for a single Supplier (GET /api/suppliers/{id} and mutations).
 * Includes all nested 1:1 relations.
 * Note: branch_id from the BD is exposed as 'warehouseId' for FE compatibility.
 */
@Getter
@Builder
public class SupplierResponse {

    private final UUID id;
    private final UUID organizationId;
    private final String code;
    private final String legalName;
    private final String commercialName;
    private final String taxId;
    private final String type;
    private final Boolean preferred;
    private final String status;
    private final String statusReason;
    private final OffsetDateTime statusChangedAt;
    private final String statusChangedBy;
    private final String scopeType;
    private final UUID clientId;
    /** Mapped from branch_id in wms.suppliers. Named warehouseId for FE compatibility. */
    private final UUID warehouseId;
    private final String notes;
    private final Boolean active;
    private final Boolean deleted;
    private final Long version;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;
    private final String createdBy;
    private final String updatedBy;

    private final ContactResponse contact;
    private final AddressResponse address;
    private final CommercialTermsResponse commercialTerms;

    @Value
    @Builder
    public static class ContactResponse {
        UUID id;
        String fullName;
        String jobTitle;
        String email;
        String phone;
        String altPhone;
    }

    @Value
    @Builder
    public static class AddressResponse {
        UUID id;
        String country;
        String state;
        String municipality;
        String city;
        String postalCode;
        String street;
        String exteriorNumber;
        String interiorNumber;
    }

    @Value
    @Builder
    public static class CommercialTermsResponse {
        UUID id;
        Integer leadTimeDays;
        BigDecimal minimumOrderAmount;
        Integer creditDays;
        /** Exposed as 'currency' to match FE commercialTerms.currency field. */
        String currency;
        Boolean qualityInspectionRequired;
    }
}
