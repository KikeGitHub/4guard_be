package com.fourguard.wms.infrastructure.persistence.repository;

import com.fourguard.wms.infrastructure.persistence.entity.WarehouseSectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WarehouseSectionJpaRepository extends JpaRepository<WarehouseSectionEntity, UUID> {
    List<WarehouseSectionEntity> findByBranchId(UUID branchId);
    boolean existsByBranchIdAndCode(UUID branchId, String code);
}
