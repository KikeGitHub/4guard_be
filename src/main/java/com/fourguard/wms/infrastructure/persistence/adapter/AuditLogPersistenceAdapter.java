package com.fourguard.wms.infrastructure.persistence.adapter;

import com.fourguard.wms.domain.ports.out.AuditLogRepositoryPort;
import com.fourguard.wms.infrastructure.persistence.entity.AuditLogEntity;
import com.fourguard.wms.infrastructure.persistence.repository.AuditLogJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuditLogPersistenceAdapter implements AuditLogRepositoryPort {

    private final AuditLogJpaRepository repository;

    @Override
    public AuditLogEntity log(AuditLogEntity entry) {
        // Only INSERT — no update or delete (WORM enforced by DB trigger)
        return repository.save(entry);
    }

    @Override
    public List<AuditLogEntity> findByEntityTypeAndEntityId(String type, UUID id) {
        return repository.findByEntityTypeAndEntityId(type, id);
    }

    @Override
    public List<AuditLogEntity> findByUserId(UUID userId) {
        return repository.findByUserId(userId);
    }
}
