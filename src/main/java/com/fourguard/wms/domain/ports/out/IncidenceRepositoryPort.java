package com.fourguard.wms.domain.ports.out;

import com.fourguard.wms.infrastructure.persistence.entity.IncidenceEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Port OUT — Incidence repository contract. */
public interface IncidenceRepositoryPort {
    Optional<IncidenceEntity> findById(UUID id);
    Optional<IncidenceEntity> findByFolio(Integer folio);
    List<IncidenceEntity>     findByItemId(UUID itemId);
    IncidenceEntity           save(IncidenceEntity incidence);
}
