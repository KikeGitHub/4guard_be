package com.fourguard.wms.infrastructure.persistence.adapter;

import com.fourguard.wms.domain.ports.out.InventoryMovementRepositoryPort;
import com.fourguard.wms.infrastructure.persistence.entity.InventoryMovementEntity;
import com.fourguard.wms.infrastructure.persistence.repository.InventoryMovementJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InventoryMovementPersistenceAdapter implements InventoryMovementRepositoryPort {
    private final InventoryMovementJpaRepository repository;

    @Override public InventoryMovementEntity       save(InventoryMovementEntity m) { return repository.save(m); }
    @Override public List<InventoryMovementEntity> findByItemId(UUID itemId)        { return repository.findByItemId(itemId); }
}
