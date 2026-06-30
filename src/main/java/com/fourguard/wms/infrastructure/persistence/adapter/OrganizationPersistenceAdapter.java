package com.fourguard.wms.infrastructure.persistence.adapter;

import com.fourguard.wms.domain.ports.out.OrganizationRepositoryPort;
import com.fourguard.wms.infrastructure.persistence.entity.OrganizationEntity;
import com.fourguard.wms.infrastructure.persistence.repository.OrganizationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrganizationPersistenceAdapter implements OrganizationRepositoryPort {

    private final OrganizationJpaRepository repository;

    @Override
    @Cacheable(value = "catalogues", key = "'org-' + #id", unless = "#result == null")
    public Optional<OrganizationEntity> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    @Cacheable(value = "catalogues", key = "'org-' + #code", unless = "#result == null")
    public Optional<OrganizationEntity> findByCode(String code) {
        return repository.findByCode(code);
    }

    @Override
    @Cacheable(value = "catalogues", key = "'org-all'")
    public List<OrganizationEntity> findAll() {
        return repository.findAll();
    }

    @Override
    @CacheEvict(value = "catalogues", allEntries = true)
    public OrganizationEntity save(OrganizationEntity o) {
        return repository.save(o);
    }

    @Override
    public boolean existsByCode(String code) {
        return repository.existsByCode(code);
    }
}
