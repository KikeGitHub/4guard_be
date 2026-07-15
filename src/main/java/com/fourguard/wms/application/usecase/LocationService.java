package com.fourguard.wms.application.usecase;

import com.fourguard.wms.application.dto.request.CreateLocationRequest;
import com.fourguard.wms.application.dto.request.UpdateLocationRequest;
import com.fourguard.wms.application.dto.response.LocationResponse;
import com.fourguard.wms.application.mapper.LocationMapper;
import com.fourguard.wms.domain.exception.EntityNotFoundException;
import com.fourguard.wms.domain.ports.in.LocationUseCase;
import com.fourguard.wms.domain.ports.out.BranchRepositoryPort;
import com.fourguard.wms.domain.ports.out.LocationRepositoryPort;
import com.fourguard.wms.domain.ports.out.WarehouseSectionRepositoryPort;
import com.fourguard.wms.infrastructure.persistence.entity.BranchEntity;
import com.fourguard.wms.infrastructure.persistence.entity.LocationEntity;
import com.fourguard.wms.infrastructure.persistence.entity.WarehouseSectionEntity;
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
public class LocationService implements LocationUseCase {

    private final LocationRepositoryPort locationRepositoryPort;
    private final BranchRepositoryPort branchRepositoryPort;
    private final WarehouseSectionRepositoryPort sectionRepositoryPort;
    private final LocationMapper locationMapper;
    private final SecurityAuditHelper securityAuditHelper;

    @Override
    @Transactional
    public LocationResponse createLocation(CreateLocationRequest request) {
        log.info("Creating location zone: {} type: {} under branch: {}", request.getZone(), request.getType(), request.getBranchId());
        BranchEntity branch = branchRepositoryPort.findById(request.getBranchId())
                .orElseThrow(() -> new EntityNotFoundException("Sucursal no encontrada con ID: " + request.getBranchId()));

        WarehouseSectionEntity section = null;
        if (request.getSectionId() != null) {
            section = sectionRepositoryPort.findById(request.getSectionId())
                    .orElseThrow(() -> new EntityNotFoundException("Sección no encontrada con ID: " + request.getSectionId()));
        }

        LocationEntity entity = locationMapper.toEntity(request);
        entity.setBranch(branch);
        entity.setSection(section);
        entity.setCurrentOccupancy(0);
        entity.setIsBlocked(false);
        entity.setCreatedBy(securityAuditHelper.getCurrentUsername());

        LocationEntity saved = locationRepositoryPort.save(entity);
        return locationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public LocationResponse updateLocation(UpdateLocationRequest request) {
        log.info("Updating location with ID: {}", request.getId());
        LocationEntity existing = locationRepositoryPort.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("Ubicación no encontrada con ID: " + request.getId()));

        BranchEntity branch = branchRepositoryPort.findById(request.getBranchId())
                .orElseThrow(() -> new EntityNotFoundException("Sucursal no encontrada con ID: " + request.getBranchId()));

        WarehouseSectionEntity section = null;
        if (request.getSectionId() != null) {
            section = sectionRepositoryPort.findById(request.getSectionId())
                    .orElseThrow(() -> new EntityNotFoundException("Sección no encontrada con ID: " + request.getSectionId()));
        }

        locationMapper.updateEntityFromDto(request, existing);
        existing.setBranch(branch);
        existing.setSection(section);
        
        if (request.getIsBlocked() != null) {
            existing.setIsBlocked(request.getIsBlocked());
            existing.setBlockReason(request.getIsBlocked() ? request.getBlockReason() : null);
        }
        
        existing.setUpdatedBy(securityAuditHelper.getCurrentUsername());
        LocationEntity saved = locationRepositoryPort.save(existing);
        return locationMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LocationResponse getLocationById(UUID id) {
        log.debug("Fetching location with ID: {}", id);
        return locationRepositoryPort.findById(id)
                .map(locationMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Ubicación no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationResponse> getLocationsByBranchId(UUID branchId) {
        log.debug("Fetching locations by branch ID: {}", branchId);
        return locationRepositoryPort.findByBranchId(branchId).stream()
                .map(locationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationResponse> getAvailableLocationsByBranchId(UUID branchId) {
        log.debug("Fetching available locations by branch ID: {}", branchId);
        return locationRepositoryPort.findAvailableByBranchId(branchId).stream()
                .map(locationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationResponse> getAllLocations() {
        log.debug("Fetching all locations");
        return locationRepositoryPort.findAll().stream()
                .map(locationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteLocation(UUID id) {
        log.info("Deleting location with ID: {}", id);
        if (!locationRepositoryPort.findById(id).isPresent()) {
            throw new EntityNotFoundException("Ubicación no encontrada con ID: " + id);
        }
        locationRepositoryPort.deleteById(id);
    }
}
