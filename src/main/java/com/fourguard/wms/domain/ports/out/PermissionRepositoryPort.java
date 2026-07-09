package com.fourguard.wms.domain.ports.out;

import com.fourguard.wms.infrastructure.persistence.entity.PermissionEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/** Port OUT — Permission repository contract. */
public interface PermissionRepositoryPort {
    Optional<PermissionEntity> findById(UUID id);
    Optional<PermissionEntity> findByName(String name);
    List<PermissionEntity>     findAll();
    /** Bulk fetch by IDs — used when assigning permissions to a role. */
    Set<PermissionEntity>      findAllByIds(Set<UUID> ids);
    PermissionEntity           save(PermissionEntity permission);
    boolean                    existsByName(String name);
    void                       deleteById(UUID id);
}
