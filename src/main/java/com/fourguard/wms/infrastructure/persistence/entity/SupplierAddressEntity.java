package com.fourguard.wms.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * JPA entity for wms.supplier_addresses.
 * 1:1 with SupplierEntity (optional — nullable).
 * Cascade from SupplierEntity — never persisted independently.
 */
@Entity
@Table(name = "supplier_addresses", schema = "wms")
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierAddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false, unique = true)
    private SupplierEntity supplier;

    @Column(name = "country", length = 80, nullable = false)
    @Builder.Default
    private String country = "México";

    @Column(name = "state", length = 80, nullable = false)
    private String state;

    @Column(name = "municipality", length = 80)
    private String municipality;

    @Column(name = "city", length = 80, nullable = false)
    private String city;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Column(name = "street", length = 200)
    private String street;

    @Column(name = "exterior_number", length = 20)
    private String exteriorNumber;

    @Column(name = "interior_number", length = 20)
    private String interiorNumber;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onPrePersist() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    protected void onPreUpdate() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }
}
