package com.fourguard.wms.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * JPA entity for wms.supplier_commercial_terms.
 * 1:1 with SupplierEntity.
 * Cascade from SupplierEntity — never persisted independently.
 */
@Entity
@Table(name = "supplier_commercial_terms", schema = "wms")
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierCommercialTermsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false, unique = true)
    private SupplierEntity supplier;

    /**
     * Lead time in days.
     * Interpreted as "delivery time" for goods (is_service=false) or
     * "response time" for services (is_service=true) — logic driven by cat_supplier_types.is_service.
     */
    @Column(name = "lead_time_days", nullable = false)
    @Builder.Default
    private Integer leadTimeDays = 0;

    @Column(name = "minimum_order_amount", nullable = false, precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal minimumOrderAmount = BigDecimal.ZERO;

    @Column(name = "credit_days", nullable = false)
    @Builder.Default
    private Integer creditDays = 0;

    /** ISO 4217 currency code. FK → wms.cat_currencies.code. */
    @Column(name = "currency_code", columnDefinition = "CHAR(3)", nullable = false)
    @Builder.Default
    private String currencyCode = "MXN";

    @Column(name = "quality_inspection_required", nullable = false)
    @Builder.Default
    private Boolean qualityInspectionRequired = false;

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
