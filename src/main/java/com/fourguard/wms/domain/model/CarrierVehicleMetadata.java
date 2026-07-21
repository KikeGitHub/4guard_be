package com.fourguard.wms.domain.model;

import com.fourguard.wms.domain.enums.VehicleOperationType;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CarrierVehicleMetadata {
    private UUID id;
    private UUID itemId;
    private Carrier carrier;
    private String vehiclePlates;
    private String driverName;
    private String driverLicense;
    private Integer sealCount;
    private VehicleOperationType operationType;
    private UUID registeredBy;
    private OffsetDateTime registeredAt;
    private String notes;
    private List<VehicleSeal> seals;
    private Long version;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
