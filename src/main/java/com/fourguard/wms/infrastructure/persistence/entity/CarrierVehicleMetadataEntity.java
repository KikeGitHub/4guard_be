package com.fourguard.wms.infrastructure.persistence.entity;

import com.fourguard.wms.domain.enums.VehicleOperationType;
import com.fourguard.wms.shared.audit.BaseVersionedEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "carrier_vehicle_metadata", schema = "wms")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class CarrierVehicleMetadataEntity extends BaseVersionedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryItemEntity item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrier_id")
    private CarrierEntity carrier;

    @Column(name = "vehicle_plates", nullable = false, length = 20)
    private String vehiclePlates;

    @Column(name = "driver_name", length = 150)
    private String driverName;

    @Column(name = "driver_license", length = 30)
    private String driverLicense;

    @Column(name = "seal_count")
    @Builder.Default
    private Integer sealCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false, length = 20)
    @Builder.Default
    private VehicleOperationType operationType = VehicleOperationType.RECEIVING;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "registered_by", nullable = false)
    private UserEntity registeredBy;

    @Column(name = "registered_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime registeredAt = OffsetDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "metadata", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<VehicleSealEntity> seals = new ArrayList<>();
}
