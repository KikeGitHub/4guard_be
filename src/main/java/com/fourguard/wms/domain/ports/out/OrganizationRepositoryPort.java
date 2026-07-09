package com.fourguard.wms.domain.ports.out;

import com.fourguard.wms.infrastructure.persistence.entity.OrganizationEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Port OUT — Organization repository contract. */
public interface OrganizationRepositoryPort {
    Optional<OrganizationEntity> findById(UUID id);
    Optional<OrganizationEntity> findByCode(String code);
    List<OrganizationEntity>     findAll();
    OrganizationEntity           save(OrganizationEntity organization);
    boolean                      existsByCode(String code);
    void                         deleteById(UUID id);
}
