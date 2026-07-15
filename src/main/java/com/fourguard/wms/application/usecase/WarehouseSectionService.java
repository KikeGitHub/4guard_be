package com.fourguard.wms.application.usecase;

import com.fourguard.wms.application.dto.request.CreateWarehouseSectionRequest;
import com.fourguard.wms.application.dto.request.UpdateWarehouseSectionRequest;
import com.fourguard.wms.application.dto.response.WarehouseSectionResponse;
import com.fourguard.wms.application.mapper.WarehouseSectionMapper;
import com.fourguard.wms.domain.exception.EntityNotFoundException;
import com.fourguard.wms.domain.ports.in.WarehouseSectionUseCase;
import com.fourguard.wms.domain.ports.out.BranchRepositoryPort;
import com.fourguard.wms.domain.ports.out.WarehouseSectionRepositoryPort;
import com.fourguard.wms.infrastructure.persistence.entity.BranchEntity;
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
public class WarehouseSectionService implements WarehouseSectionUseCase {

    private final WarehouseSectionRepositoryPort sectionRepositoryPort;
    private final BranchRepositoryPort branchRepositoryPort;
    private final WarehouseSectionMapper sectionMapper;
    private final SecurityAuditHelper securityAuditHelper;

    @Override
    @Transactional
    public WarehouseSectionResponse createWarehouseSection(CreateWarehouseSectionRequest request) {
        log.info("Creating warehouse section with code: {} for branch: {}", request.getCode(), request.getBranchId());
        BranchEntity branch = branchRepositoryPort.findById(request.getBranchId())
                .orElseThrow(() -> new EntityNotFoundException("Sucursal no encontrada con ID: " + request.getBranchId()));

        WarehouseSectionEntity entity = sectionMapper.toEntity(request);
        entity.setBranch(branch);
        entity.setCreatedBy(securityAuditHelper.getCurrentUsername());
        WarehouseSectionEntity saved = sectionRepositoryPort.save(entity);
        return sectionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public WarehouseSectionResponse updateWarehouseSection(UpdateWarehouseSectionRequest request) {
        log.info("Updating warehouse section with ID: {}", request.getId());
        WarehouseSectionEntity existing = sectionRepositoryPort.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("Sección no encontrada con ID: " + request.getId()));

        BranchEntity branch = branchRepositoryPort.findById(request.getBranchId())
                .orElseThrow(() -> new EntityNotFoundException("Sucursal no encontrada con ID: " + request.getBranchId()));

        sectionMapper.updateEntityFromDto(request, existing);
        existing.setBranch(branch);
        existing.setUpdatedBy(securityAuditHelper.getCurrentUsername());
        WarehouseSectionEntity saved = sectionRepositoryPort.save(existing);
        return sectionMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseSectionResponse getWarehouseSectionById(UUID id) {
        log.debug("Fetching warehouse section with ID: {}", id);
        return sectionRepositoryPort.findById(id)
                .map(sectionMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Sección no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseSectionResponse> getWarehouseSectionsByBranchId(UUID branchId) {
        log.debug("Fetching warehouse sections by branch ID: {}", branchId);
        return sectionRepositoryPort.findByBranchId(branchId).stream()
                .map(sectionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseSectionResponse> getAllWarehouseSections() {
        log.debug("Fetching all warehouse sections");
        return sectionRepositoryPort.findAll().stream()
                .map(sectionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteWarehouseSection(UUID id) {
        log.info("Deleting warehouse section with ID: {}", id);
        if (!sectionRepositoryPort.findById(id).isPresent()) {
            throw new EntityNotFoundException("Sección no encontrada con ID: " + id);
        }
        sectionRepositoryPort.deleteById(id);
    }
}
