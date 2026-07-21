package com.fourguard.wms.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/** Response DTO for Carrier. */
@Getter
@Builder
public class CarrierResponse {
    private final UUID id;
    private final UUID organizationId;
    private final String organizationName;
    private final String name;
    private final String tradeName;
    private final String taxId;
    private final String carrierType;
    private final String status;
    private final String contactName;
    private final String contactPhone;
    private final String contactEmail;
    private final String serviceType;
    private final String permitNumber;
    private final String geographicCoverage;
    private final String notes;
    private final List<String> vehicleTypes;
    private final List<PreferredClientResponse> preferredClients;
    private final Long version;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    @Value
    @Builder
    public static class PreferredClientResponse {
        UUID id;
        String name;
    }
}
