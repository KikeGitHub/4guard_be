package com.fourguard.wms.infrastructure.persistence.entity;

import com.fourguard.wms.domain.enums.CarrierStatus;
import com.fourguard.wms.shared.audit.BaseVersionedEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "carriers", schema = "wms",
        uniqueConstraints = @UniqueConstraint(name = "uk_carrier_org_name", columnNames = {"organization_id", "name"}))
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class CarrierEntity extends BaseVersionedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private OrganizationEntity organization;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "trade_name", length = 200)
    private String tradeName;

    @Column(name = "tax_id", length = 30)
    private String taxId;

    @Column(name = "contact_name", length = 150)
    private String contactName;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "contact_email", length = 255)
    private String contactEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private CarrierStatus status = CarrierStatus.ACTIVE;
}
