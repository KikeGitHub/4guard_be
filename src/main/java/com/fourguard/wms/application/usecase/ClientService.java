package com.fourguard.wms.application.usecase;

import com.fourguard.wms.application.dto.request.CreateClientRequest;
import com.fourguard.wms.application.dto.request.UpdateClientRequest;
import com.fourguard.wms.application.dto.response.ClientResponse;
import com.fourguard.wms.application.mapper.ClientMapper;
import com.fourguard.wms.domain.exception.EntityNotFoundException;
import com.fourguard.wms.domain.ports.in.ClientUseCase;
import com.fourguard.wms.domain.ports.out.ClientRepositoryPort;
import com.fourguard.wms.domain.ports.out.OrganizationRepositoryPort;
import com.fourguard.wms.infrastructure.persistence.entity.ClientEntity;
import com.fourguard.wms.infrastructure.persistence.entity.OrganizationEntity;
import com.fourguard.wms.shared.audit.SecurityAuditHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService implements ClientUseCase {

    private final ClientRepositoryPort clientRepositoryPort;
    private final OrganizationRepositoryPort organizationRepositoryPort;
    private final ClientMapper clientMapper;
    private final SecurityAuditHelper securityAuditHelper;

    @Override
    @Transactional
    public ClientResponse createClient(CreateClientRequest request) {
        log.info("Creating client: {} under organization: {}", request.getName(), request.getOrganizationId());
        OrganizationEntity organization = organizationRepositoryPort.findById(request.getOrganizationId())
                .orElseThrow(() -> new EntityNotFoundException("Organización no encontrada con ID: " + request.getOrganizationId()));

        ClientEntity entity = clientMapper.toEntity(request);
        entity.setOrganization(organization);
        entity.setCreatedBy(securityAuditHelper.getCurrentUsername());
        ClientEntity saved = clientRepositoryPort.save(entity);
        return clientMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ClientResponse updateClient(UpdateClientRequest request) {
        log.info("Updating client with ID: {}", request.getId());
        ClientEntity existing = clientRepositoryPort.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + request.getId()));

        OrganizationEntity organization = organizationRepositoryPort.findById(request.getOrganizationId())
                .orElseThrow(() -> new EntityNotFoundException("Organización no encontrada con ID: " + request.getOrganizationId()));

        clientMapper.updateEntityFromDto(request, existing);
        existing.setOrganization(organization);
        existing.setUpdatedBy(securityAuditHelper.getCurrentUsername());
        ClientEntity saved = clientRepositoryPort.save(existing);
        return clientMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponse getClientById(UUID id) {
        log.debug("Fetching client with ID: {}", id);
        return clientRepositoryPort.findById(id)
                .map(clientMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponse> getClientsByOrganizationId(UUID organizationId) {
        log.debug("Fetching clients by organization ID: {}", organizationId);
        return clientRepositoryPort.findByOrganizationId(organizationId).stream()
                .map(clientMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponse> getAllClients() {
        log.debug("Fetching all clients");
        return clientRepositoryPort.findAll().stream()
                .map(clientMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteClient(UUID id) {
        log.info("Deleting client with ID: {}", id);
        if (!clientRepositoryPort.findById(id).isPresent()) {
            throw new EntityNotFoundException("Cliente no encontrado con ID: " + id);
        }
        clientRepositoryPort.deleteById(id);
    }
}
