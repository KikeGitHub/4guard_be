package com.fourguard.wms.infrastructure.persistence.repository;

import com.fourguard.wms.infrastructure.persistence.entity.SupplierCommercialTermsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SupplierCommercialTermsJpaRepository extends JpaRepository<SupplierCommercialTermsEntity, UUID> {
    Optional<SupplierCommercialTermsEntity> findBySupplierId(UUID supplierId);
}
