package com.fourguard.wms.infrastructure.persistence.repository;

import com.fourguard.wms.infrastructure.persistence.entity.InventoryMovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryMovementJpaRepository extends JpaRepository<InventoryMovementEntity, UUID> {
    List<InventoryMovementEntity> findByItemId(UUID itemId);
    List<InventoryMovementEntity> findByUserId(UUID userId);
}
