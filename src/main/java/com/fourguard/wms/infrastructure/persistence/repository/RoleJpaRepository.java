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

    /**
     * Returns true if at least one user is currently assigned to this role.
     * Uses Spring Data derived query — no manual JPQL needed.
     */
    @Query("SELECT COUNT(u) > 0 FROM UserEntity u WHERE u.role.id = :roleId")
    boolean existsUserWithRoleId(UUID roleId);
}

