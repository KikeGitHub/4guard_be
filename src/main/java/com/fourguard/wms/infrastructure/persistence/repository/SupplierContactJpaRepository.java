package com.fourguard.wms.infrastructure.persistence.repository;

import com.fourguard.wms.infrastructure.persistence.entity.SupplierContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SupplierContactJpaRepository extends JpaRepository<SupplierContactEntity, UUID> {
    Optional<SupplierContactEntity> findBySupplierId(UUID supplierId);
}
