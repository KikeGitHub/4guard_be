package com.fourguard.wms.infrastructure.persistence.repository;

import com.fourguard.wms.infrastructure.persistence.entity.VehicleSealEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VehicleSealJpaRepository extends JpaRepository<VehicleSealEntity, UUID> {
    List<VehicleSealEntity> findByMetadataId(UUID metadataId);
}
