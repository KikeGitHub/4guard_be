package com.fourguard.wms.infrastructure.persistence.repository;

import com.fourguard.wms.infrastructure.persistence.entity.SupplierAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SupplierAddressJpaRepository extends JpaRepository<SupplierAddressEntity, UUID> {
    Optional<SupplierAddressEntity> findBySupplierId(UUID supplierId);
}
