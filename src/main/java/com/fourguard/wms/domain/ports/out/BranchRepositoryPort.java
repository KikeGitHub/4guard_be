package com.fourguard.wms.domain.ports.out;

import com.fourguard.wms.infrastructure.persistence.entity.BranchEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Port OUT — Branch repository contract. */
public interface BranchRepositoryPort {
    Optional<BranchEntity> findById(UUID id);
    List<BranchEntity>     findByOrganizationId(UUID organizationId);
    BranchEntity           save(BranchEntity branch);
    boolean                existsByOrganizationIdAndCode(UUID organizationId, String code);
}
