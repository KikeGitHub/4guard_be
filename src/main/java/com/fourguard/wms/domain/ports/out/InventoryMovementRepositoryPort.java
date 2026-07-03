package com.fourguard.wms.domain.ports.out;

import com.fourguard.wms.infrastructure.persistence.entity.InventoryMovementEntity;

import java.util.List;
import java.util.UUID;

/** Port OUT — InventoryMovement repository contract. */
public interface InventoryMovementRepositoryPort {
    InventoryMovementEntity   save(InventoryMovementEntity movement);
    List<InventoryMovementEntity> findByItemId(UUID itemId);
}
