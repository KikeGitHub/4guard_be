package com.fourguard.wms.infrastructure.persistence.repository;

import com.fourguard.wms.infrastructure.persistence.entity.SupplierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** JPA repository for wms.suppliers with dynamic filtering support. */
public interface SupplierJpaRepository extends
        JpaRepository<SupplierEntity, UUID>,
        JpaSpecificationExecutor<SupplierEntity> {

    List<SupplierEntity> findByOrganizationIdAndIsDeletedFalse(UUID organizationId);

    boolean existsByOrganizationIdAndCodeAndIsDeletedFalse(UUID organizationId, String code);

    boolean existsByOrganizationIdAndTaxIdAndIsDeletedFalse(UUID organizationId, String taxId);

    /**
     * Returns the maximum sequential number extracted from the code 'PRV-NNNN'
     * for a given organization, ignoring soft-deleted records.
     * Used by SupplierService to generate the next PRV code.
     */
    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(s.code, 5) AS int)), 0) " +
           "FROM SupplierEntity s " +
           "WHERE s.organization.id = :orgId AND s.isDeleted = false")
    Optional<Integer> findMaxCodeSequence(@Param("orgId") UUID orgId);
}
