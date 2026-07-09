package com.fourguard.wms.infrastructure.persistence.repository;

import com.fourguard.wms.infrastructure.persistence.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PermissionJpaRepository extends JpaRepository<PermissionEntity, UUID> {
    Optional<PermissionEntity> findByName(String name);
    List<PermissionEntity> findByNameIn(List<String> names);
    boolean existsByName(String name);
    /** Bulk fetch by a set of IDs — used when assigning permissions to a role. */
    Set<PermissionEntity> findAllByIdIn(Set<UUID> ids);
}

