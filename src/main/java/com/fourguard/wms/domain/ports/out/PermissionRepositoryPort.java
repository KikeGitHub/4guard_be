package com.fourguard.wms.domain.ports.out;

import com.fourguard.wms.infrastructure.persistence.entity.PermissionEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Port OUT — Permission repository contract. */
public interface PermissionRepositoryPort {
    Optional<PermissionEntity> findById(UUID id);
    Optional<PermissionEntity> findByName(String name);
    List<PermissionEntity>     findAll();
    PermissionEntity           save(PermissionEntity permission);
    boolean                    existsByName(String name);
}
