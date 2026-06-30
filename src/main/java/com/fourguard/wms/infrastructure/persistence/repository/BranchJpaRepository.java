package com.fourguard.wms.infrastructure.persistence.repository;

import com.fourguard.wms.infrastructure.persistence.entity.BranchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BranchJpaRepository extends JpaRepository<BranchEntity, UUID> {
    List<BranchEntity> findByOrganizationId(UUID organizationId);
    Optional<BranchEntity> findByOrganizationIdAndCode(UUID organizationId, String code);
    boolean existsByOrganizationIdAndCode(UUID organizationId, String code);
}
