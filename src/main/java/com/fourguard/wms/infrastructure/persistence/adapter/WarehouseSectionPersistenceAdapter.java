package com.fourguard.wms.infrastructure.persistence.adapter;

import com.fourguard.wms.domain.ports.out.WarehouseSectionRepositoryPort;
import com.fourguard.wms.infrastructure.persistence.entity.WarehouseSectionEntity;
import com.fourguard.wms.infrastructure.persistence.repository.WarehouseSectionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WarehouseSectionPersistenceAdapter implements WarehouseSectionRepositoryPort {
    private final WarehouseSectionJpaRepository repository;

    @Override public Optional<WarehouseSectionEntity> findById(UUID id)         { return repository.findById(id); }
    @Override public List<WarehouseSectionEntity>     findByBranchId(UUID bid)  { return repository.findByBranchId(bid); }
    @Override public WarehouseSectionEntity           save(WarehouseSectionEntity s) { return repository.save(s); }
    @Override public void                             deleteById(UUID id)        { repository.deleteById(id); }
}
