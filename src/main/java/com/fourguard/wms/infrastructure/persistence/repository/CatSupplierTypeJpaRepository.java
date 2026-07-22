package com.fourguard.wms.infrastructure.persistence.repository;

import com.fourguard.wms.infrastructure.persistence.entity.CatSupplierTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CatSupplierTypeJpaRepository extends JpaRepository<CatSupplierTypeEntity, String> {
    List<CatSupplierTypeEntity> findByActiveTrueOrderBySortOrderAsc();
}
