package com.fourguard.wms.application.usecase;

import com.fourguard.wms.application.dto.request.CreateCarrierRequest;
import com.fourguard.wms.application.dto.request.UpdateCarrierRequest;
import com.fourguard.wms.application.dto.response.CarrierResponse;
import com.fourguard.wms.application.mapper.CarrierMapper;
import com.fourguard.wms.domain.enums.CarrierStatus;
import com.fourguard.wms.domain.exception.EntityNotFoundException;
import com.fourguard.wms.domain.exception.ValidationException;
import com.fourguard.wms.domain.ports.in.CarrierUseCase;
import com.fourguard.wms.domain.ports.out.CarrierRepositoryPort;
import com.fourguard.wms.domain.ports.out.OrganizationRepositoryPort;
import com.fourguard.wms.domain.ports.out.UserRepositoryPort;
import com.fourguard.wms.infrastructure.persistence.entity.CarrierEntity;
import com.fourguard.wms.infrastructure.persistence.entity.OrganizationEntity;
import com.fourguard.wms.infrastructure.persistence.entity.UserEntity;
import com.fourguard.wms.shared.audit.AuditService;
import com.fourguard.wms.shared.audit.SecurityAuditHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarrierService implements CarrierUseCase {

    private final CarrierRepositoryPort carrierRepositoryPort;
    private final OrganizationRepositoryPort organizationRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final CarrierMapper carrierMapper;
    private final SecurityAuditHelper securityAuditHelper;
    private final AuditService auditService;

    @Override
    @Transactional
    public CarrierResponse createCarrier(CreateCarrierRequest request) {
        log.info("Creating carrier: {} under organization: {}", request.getName(), request.getOrganizationId());

        OrganizationEntity organization = organizationRepositoryPort.findById(request.getOrganizationId())
                .orElseThrow(() -> new EntityNotFoundException("Organización no encontrada con ID: " + request.getOrganizationId()));

        // Validar unicidad de nombre en la misma organización
        // Nota: Podemos usar una consulta en port o repository para verificar.
        // Dado que añadimos la restricción UNIQUE en base de datos, un check rápido previene excepciones de BD.
        List<CarrierEntity> existingCarriers = carrierRepositoryPort.findByOrganizationId(request.getOrganizationId());
        boolean nameExists = existingCarriers.stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(request.getName()));
        if (nameExists) {
            throw new ValidationException("Ya existe un transportista registrado con el nombre '" + request.getName() + "' en esta organización.");
        }

        CarrierEntity entity = carrierMapper.toEntity(request);
        entity.setOrganization(organization);
        
        String currentUser = securityAuditHelper.getCurrentUsername();
        entity.setCreatedBy(currentUser);
        entity.setUpdatedBy(currentUser);

        CarrierEntity saved = carrierRepositoryPort.save(entity);

        // Registro de auditoría obligatoria
        logAuditChange(currentUser, "CARRIER_CREATED", saved.getId(), null, saved);

        return carrierMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CarrierResponse updateCarrier(UpdateCarrierRequest request) {
        log.info("Updating carrier with ID: {}", request.getId());

        CarrierEntity existing = carrierRepositoryPort.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("Transportista no encontrado con ID: " + request.getId()));

        OrganizationEntity organization = organizationRepositoryPort.findById(request.getOrganizationId())
                .orElseThrow(() -> new EntityNotFoundException("Organización no encontrada con ID: " + request.getOrganizationId()));

        // Validar unicidad del nombre si es que cambia
        if (!existing.getName().equalsIgnoreCase(request.getName())) {
            List<CarrierEntity> existingCarriers = carrierRepositoryPort.findByOrganizationId(request.getOrganizationId());
            boolean nameExists = existingCarriers.stream()
                    .anyMatch(c -> c.getName().equalsIgnoreCase(request.getName()));
            if (nameExists) {
                throw new ValidationException("Ya existe otro transportista registrado con el nombre '" + request.getName() + "' en esta organización.");
            }
        }

        CarrierEntity originalSnapshot = existing.toBuilder().build();

        carrierMapper.updateEntityFromDto(request, existing);
        existing.setOrganization(organization);
        
        String currentUser = securityAuditHelper.getCurrentUsername();
        existing.setUpdatedBy(currentUser);

        CarrierEntity saved = carrierRepositoryPort.save(existing);

        // Registro de auditoría obligatoria
        logAuditChange(currentUser, "CARRIER_UPDATED", saved.getId(), originalSnapshot, saved);

        return carrierMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CarrierResponse getCarrierById(UUID id) {
        log.debug("Fetching carrier with ID: {}", id);
        return carrierRepositoryPort.findById(id)
                .map(carrierMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Transportista no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarrierResponse> getCarriersByOrganizationId(UUID organizationId) {
        log.debug("Fetching carriers by organization ID: {}", organizationId);
        return carrierRepositoryPort.findByOrganizationId(organizationId).stream()
                .map(carrierMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarrierResponse> getAllCarriers() {
        log.debug("Fetching all carriers");
        return carrierRepositoryPort.findAll().stream()
                .map(carrierMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCarrier(UUID id) {
        log.info("Deleting carrier with ID: {}", id);
        CarrierEntity existing = carrierRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transportista no encontrado con ID: " + id));

        String currentUser = securityAuditHelper.getCurrentUsername();
        
        // Log delete audit before actually deleting
        logAuditChange(currentUser, "CARRIER_DELETED", id, existing, null);

        carrierRepositoryPort.deleteById(id);
    }

    private void logAuditChange(String username, String action, UUID entityId, CarrierEntity before, CarrierEntity after) {
        try {
            UserEntity actor = userRepositoryPort.findByUsername(username).orElse(null);
            if (actor != null) {
                Map<String, Object> beforeState = buildAuditState(before);
                Map<String, Object> afterState = buildAuditState(after);
                auditService.log(actor, action, "CARRIER", entityId, beforeState, afterState);
            }
        } catch (Exception e) {
            log.error("Failed to persist audit log for carrier operation", e);
        }
    }

    private Map<String, Object> buildAuditState(CarrierEntity entity) {
        if (entity == null) return null;
        Map<String, Object> state = new HashMap<>();
        state.put("id", entity.getId() != null ? entity.getId().toString() : null);
        state.put("name", entity.getName());
        state.put("tradeName", entity.getTradeName());
        state.put("taxId", entity.getTaxId());
        state.put("contactName", entity.getContactName());
        state.put("contactPhone", entity.getContactPhone());
        state.put("contactEmail", entity.getContactEmail());
        state.put("status", entity.getStatus() != null ? entity.getStatus().name() : null);
        state.put("organizationId", entity.getOrganization() != null ? entity.getOrganization().getId().toString() : null);
        return state;
    }
}
