package com.fourguard.wms.application.usecase;

import com.fourguard.wms.application.dto.request.CreateOrganizationRequest;
import com.fourguard.wms.application.dto.request.UpdateOrganizationRequest;
import com.fourguard.wms.application.dto.response.OrganizationResponse;
import com.fourguard.wms.application.mapper.OrganizationMapper;
import com.fourguard.wms.domain.exception.EntityNotFoundException;
import com.fourguard.wms.domain.exception.ValidationException;
import com.fourguard.wms.domain.ports.in.CreateOrganizationUseCase;
import com.fourguard.wms.domain.ports.in.DeleteOrganizationUseCase;
import com.fourguard.wms.domain.ports.in.GetOrganizationUseCase;
import com.fourguard.wms.domain.ports.in.UpdateOrganizationUseCase;
import com.fourguard.wms.domain.ports.out.OrganizationRepositoryPort;
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
public class OrganizationService implements CreateOrganizationUseCase, UpdateOrganizationUseCase, GetOrganizationUseCase, DeleteOrganizationUseCase {

    private final OrganizationRepositoryPort organizationRepositoryPort;
    private final OrganizationMapper organizationMapper;
    private final SecurityAuditHelper securityAuditHelper;

    @Override
    @Transactional
    public OrganizationResponse createOrganization(CreateOrganizationRequest request) {
        log.info("Creating organization with code: {}", request.getCode());
        if (organizationRepositoryPort.existsByCode(request.getCode())) {
            throw new ValidationException("El código de organización ya existe: " + request.getCode());
        }

        OrganizationEntity entity = organizationMapper.toEntity(request);
        entity.setCreatedBy(securityAuditHelper.getCurrentUsername());
        OrganizationEntity saved = organizationRepositoryPort.save(entity);
        return organizationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public OrganizationResponse updateOrganization(UpdateOrganizationRequest request) {
        log.info("Updating organization with ID: {}", request.getId());
        OrganizationEntity existing = organizationRepositoryPort.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("Organización no encontrada con ID: " + request.getId()));

        organizationMapper.updateEntityFromDto(request, existing);
        existing.setUpdatedBy(securityAuditHelper.getCurrentUsername());
        OrganizationEntity saved = organizationRepositoryPort.save(existing);
        return organizationMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationResponse getOrganizationById(UUID id) {
        log.debug("Fetching organization with ID: {}", id);
        return organizationRepositoryPort.findById(id)
                .map(organizationMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Organización no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationResponse> getAllOrganizations() {
        log.debug("Fetching all organizations");
        return organizationRepositoryPort.findAll().stream()
                .map(organizationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteOrganization(UUID id) {
        log.info("Deleting organization with ID: {}", id);
        if (!organizationRepositoryPort.findById(id).isPresent()) {
            throw new EntityNotFoundException("Organización no encontrada con ID: " + id);
        }
        // TODO: check for dependent entities like branches, users, clients to avoid FK violations
        organizationRepositoryPort.deleteById(id);
    }
}
