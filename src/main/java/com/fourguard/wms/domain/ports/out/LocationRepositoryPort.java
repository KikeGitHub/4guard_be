package com.fourguard.wms.domain.ports.out;

import com.fourguard.wms.infrastructure.persistence.entity.LocationEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Port OUT — Location repository contract. */
public interface LocationRepositoryPort {
    Optional<LocationEntity> findById(UUID id);
    List<LocationEntity>     findByBranchId(UUID branchId);
    List<LocationEntity>     findAvailableByBranchId(UUID branchId);
    LocationEntity           save(LocationEntity location);
    void                     deleteById(UUID id);
}
