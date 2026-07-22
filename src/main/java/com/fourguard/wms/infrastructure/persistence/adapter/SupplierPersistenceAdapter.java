package com.fourguard.wms.infrastructure.persistence.adapter;

import com.fourguard.wms.domain.ports.out.SupplierRepositoryPort;
import com.fourguard.wms.infrastructure.persistence.entity.SupplierEntity;
import com.fourguard.wms.infrastructure.persistence.repository.SupplierJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SupplierPersistenceAdapter implements SupplierRepositoryPort {

    private final SupplierJpaRepository repository;

    @Override public Optional<SupplierEntity> findById(UUID id)                                           { return repository.findById(id); }
    @Override public Page<SupplierEntity>     findAll(Specification<SupplierEntity> spec, Pageable p)    { return repository.findAll(spec, p); }
    @Override public List<SupplierEntity>     findByOrganizationId(UUID orgId)                           { return repository.findByOrganizationIdAndIsDeletedFalse(orgId); }
    @Override public SupplierEntity           save(SupplierEntity entity)                                 { return repository.save(entity); }
    @Override public boolean                  existsByOrganizationIdAndCodeAndIsDeletedFalse(UUID orgId, String code)  { return repository.existsByOrganizationIdAndCodeAndIsDeletedFalse(orgId, code); }
    @Override public boolean                  existsByOrganizationIdAndTaxIdAndIsDeletedFalse(UUID orgId, String taxId){ return repository.existsByOrganizationIdAndTaxIdAndIsDeletedFalse(orgId, taxId); }
    @Override public Optional<Integer>        findMaxCodeSequence(UUID orgId)                             { return repository.findMaxCodeSequence(orgId); }
}
