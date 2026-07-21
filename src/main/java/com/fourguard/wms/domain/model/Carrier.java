package com.fourguard.wms.domain.model;

import com.fourguard.wms.domain.enums.CarrierStatus;
import com.fourguard.wms.domain.enums.CarrierType;
import com.fourguard.wms.domain.enums.ServiceType;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Carrier {
    private UUID id;
    private Organization organization;
    private String name;
    private String tradeName;
    private String taxId;
    private CarrierType carrierType;
    private CarrierStatus status;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private ServiceType serviceType;
    private String permitNumber;
    private String geographicCoverage;
    private String notes;
    private List<String> vehicleTypes;
    private List<UUID> preferredClientIds;
    private Long version;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
