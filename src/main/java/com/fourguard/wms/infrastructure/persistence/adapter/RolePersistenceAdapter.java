package com.fourguard.wms.infrastructure.persistence.adapter;

import com.fourguard.wms.domain.ports.out.RoleRepositoryPort;
import com.fourguard.wms.infrastructure.persistence.entity.RoleEntity;
import com.fourguard.wms.infrastructure.persistence.repository.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RolePersistenceAdapter implements RoleRepositoryPort {

    private final RoleJpaRepository repository;

    @Override
    @Cacheable(value = "roles", key = "#id", unless = "#result == null")
    public Optional<RoleEntity> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    @Cacheable(value = "roles", key = "#name", unless = "#result == null")
    public Optional<RoleEntity> findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    @Cacheable(value = "roles", key = "'with-permissions-' + #id", unless = "#result == null")
    public Optional<RoleEntity> findByIdWithPermissions(UUID id) {
        return repository.findByIdWithPermissions(id);
    }

    @Override
    @Cacheable(value = "roles", key = "'all'")
    public List<RoleEntity> findAll() {
        return repository.findAll();
    }

    @Override
    @CacheEvict(value = "roles", allEntries = true)
    public RoleEntity save(RoleEntity role) {
        return repository.save(role);
    }

    @Override
    @CacheEvict(value = "roles", allEntries = true)
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsUserAssignedToRole(UUID roleId) {
        return repository.existsUserWithRoleId(roleId);
    }
}
