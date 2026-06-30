package com.fourguard.wms.infrastructure.persistence.adapter;

import com.fourguard.wms.domain.ports.out.PermissionRepositoryPort;
import com.fourguard.wms.infrastructure.persistence.entity.PermissionEntity;
import com.fourguard.wms.infrastructure.persistence.repository.PermissionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PermissionPersistenceAdapter implements PermissionRepositoryPort {

    private final PermissionJpaRepository repository;

    @Override
    @Cacheable(value = "permissions", key = "#id", unless = "#result == null")
    public Optional<PermissionEntity> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    @Cacheable(value = "permissions", key = "#name", unless = "#result == null")
    public Optional<PermissionEntity> findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    @Cacheable(value = "permissions", key = "'all'")
    public List<PermissionEntity> findAll() {
        return repository.findAll();
    }

    @Override
    @CacheEvict(value = "permissions", allEntries = true)
    public PermissionEntity save(PermissionEntity p) {
        return repository.save(p);
    }

    @Override
    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }
}
