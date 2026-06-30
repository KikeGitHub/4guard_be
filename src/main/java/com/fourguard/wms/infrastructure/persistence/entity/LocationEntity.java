package com.fourguard.wms.infrastructure.persistence.entity;

import com.fourguard.wms.domain.enums.LocationType;
import com.fourguard.wms.shared.audit.BaseVersionedEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "locations", schema = "wms")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class LocationEntity extends BaseVersionedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private BranchEntity branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private WarehouseSectionEntity section;

    @Column(nullable = false, length = 10)
    private String zone;

    @Column(length = 10)
    private String aisle;

    @Column(length = 10)
    private String rack;

    @Column(name = "level")
    private Integer level;

    @Column(length = 10)
    private String position;

    @Column(name = "coord_x")
    private Integer coordX;

    @Column(name = "coord_y")
    private Integer coordY;

    @Column(name = "coord_z")
    private Integer coordZ;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private LocationType type;

    @Column(name = "capacity_units")
    @Builder.Default
    private Integer capacityUnits = 1;

    @Column(name = "current_occupancy")
    @Builder.Default
    private Integer currentOccupancy = 0;

    @Column(name = "is_blocked")
    @Builder.Default
    private Boolean isBlocked = false;

    @Column(name = "block_reason", columnDefinition = "TEXT")
    private String blockReason;
}
