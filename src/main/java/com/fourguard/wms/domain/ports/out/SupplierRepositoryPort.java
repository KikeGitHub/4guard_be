package com.fourguard.wms.domain.ports.out;

import com.fourguard.wms.infrastructure.persistence.entity.SupplierEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Port OUT — Supplier repository contract. */
public interface SupplierRepositoryPort {
    Optional<SupplierEntity> findById(UUID id);
    Page<SupplierEntity>     findAll(Specification<SupplierEntity> spec, Pageable pageable);
    List<SupplierEntity>     findByOrganizationId(UUID organizationId);
    SupplierEntity           save(SupplierEntity entity);
    boolean                  existsByOrganizationIdAndCodeAndIsDeletedFalse(UUID organizationId, String code);
    boolean                  existsByOrganizationIdAndTaxIdAndIsDeletedFalse(UUID organizationId, String taxId);
    Optional<Integer>        findMaxCodeSequence(UUID organizationId);
}
