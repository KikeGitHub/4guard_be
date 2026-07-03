package com.fourguard.wms.domain.ports.out;

import com.fourguard.wms.infrastructure.persistence.entity.ClientEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Port OUT — Client repository contract. */
public interface ClientRepositoryPort {
    Optional<ClientEntity> findById(UUID id);
    List<ClientEntity>     findByOrganizationId(UUID organizationId);
    ClientEntity           save(ClientEntity client);
    void                   deleteById(UUID id);
}
