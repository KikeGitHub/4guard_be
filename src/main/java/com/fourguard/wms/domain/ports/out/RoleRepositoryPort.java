package com.fourguard.wms.domain.ports.out;

import com.fourguard.wms.infrastructure.persistence.entity.RoleEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Port OUT — Role repository contract. */
public interface RoleRepositoryPort {
    Optional<RoleEntity> findById(UUID id);
    Optional<RoleEntity> findByName(String name);
    Optional<RoleEntity> findByIdWithPermissions(UUID id);
    List<RoleEntity>     findAll();
    RoleEntity           save(RoleEntity role);
    void                 deleteById(UUID id);
    /** Returns true if any user currently has this role assigned. */
    boolean              existsUserAssignedToRole(UUID roleId);
}
