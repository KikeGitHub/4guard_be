package com.fourguard.wms.domain.ports.out;

import com.fourguard.wms.infrastructure.persistence.entity.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port OUT — User repository contract.
 * Implemented by {@code UserPersistenceAdapter} in the infrastructure layer.
 */
public interface UserRepositoryPort {
    Optional<UserEntity> findById(UUID id);
    List<UserEntity>     findAll(); // Added for CRUD
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUsernameOrEmail(String identifier);
    List<UserEntity>     findByOrganizationId(UUID organizationId);
    UserEntity           save(UserEntity user);
    void                 deleteById(UUID id); // Added for CRUD
    boolean              existsByUsername(String username);
    boolean              existsByEmail(String email);
    List<UserEntity>     findAllById(List<UUID> ids);
}