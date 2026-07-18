package com.fourguard.wms.infrastructure.persistence.repository;

import com.fourguard.wms.infrastructure.persistence.entity.CarrierVehicleMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CarrierVehicleMetadataJpaRepository extends JpaRepository<CarrierVehicleMetadataEntity, UUID> {
    List<CarrierVehicleMetadataEntity> findByItemId(UUID itemId);
}
