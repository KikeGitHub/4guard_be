package com.fourguard.wms.domain.ports.out;

import com.fourguard.wms.infrastructure.persistence.entity.WarehouseSectionEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Port OUT — WarehouseSection repository contract. */
public interface WarehouseSectionRepositoryPort {
    Optional<WarehouseSectionEntity> findById(UUID id);
    List<WarehouseSectionEntity>     findByBranchId(UUID branchId);
    WarehouseSectionEntity           save(WarehouseSectionEntity section);
    void                             deleteById(UUID id);
}
