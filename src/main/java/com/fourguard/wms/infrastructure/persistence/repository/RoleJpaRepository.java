package com.fourguard.wms.infrastructure.persistence.repository;

import com.fourguard.wms.infrastructure.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleJpaRepository extends JpaRepository<RoleEntity, UUID> {
    Optional<RoleEntity> findByName(String name);

    /** Fetches role with permissions in a single JOIN query — avoids N+1. */
    @Query("SELECT r FROM RoleEntity r LEFT JOIN FETCH r.permissions WHERE r.id = :id")
    Optional<RoleEntity> findByIdWithPermissions(UUID id);
}
