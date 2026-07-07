package com.fourguard.wms.application.usecase;

import com.fourguard.wms.application.dto.request.CreateBranchRequest;
import com.fourguard.wms.application.dto.request.UpdateBranchRequest;
import com.fourguard.wms.application.dto.response.BranchResponse;
import com.fourguard.wms.application.mapper.BranchMapper;
import com.fourguard.wms.domain.exception.EntityNotFoundException;
import com.fourguard.wms.domain.exception.ValidationException;
import com.fourguard.wms.domain.ports.in.CreateBranchUseCase;
import com.fourguard.wms.domain.ports.in.DeleteBranchUseCase;
import com.fourguard.wms.domain.ports.in.GetBranchUseCase;
import com.fourguard.wms.domain.ports.in.UpdateBranchUseCase;
import com.fourguard.wms.domain.ports.out.BranchRepositoryPort;
import com.fourguard.wms.domain.ports.out.OrganizationRepositoryPort;
import com.fourguard.wms.infrastructure.persistence.entity.BranchEntity;
import com.fourguard.wms.infrastructure.persistence.entity.OrganizationEntity;
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
public class BranchService implements CreateBranchUseCase, UpdateBranchUseCase, GetBranchUseCase, DeleteBranchUseCase {

    private final BranchRepositoryPort branchRepositoryPort;
    private final OrganizationRepositoryPort organizationRepositoryPort;
    private final BranchMapper branchMapper;

    @Override
    @Transactional
    public BranchResponse createBranch(CreateBranchRequest request) {
        log.info("Creating branch with code: {} under org: {}", request.getCode(), request.getOrganizationId());
        OrganizationEntity organization = organizationRepositoryPort.findById(request.getOrganizationId())
                .orElseThrow(() -> new EntityNotFoundException("Organización no encontrada con ID: " + request.getOrganizationId()));

        if (branchRepositoryPort.existsByOrganizationIdAndCode(request.getOrganizationId(), request.getCode())) {
            throw new ValidationException("El código de sucursal ya existe para esta organización: " + request.getCode());
        }

        BranchEntity entity = branchMapper.toEntity(request);
        entity.setOrganization(organization);
        entity.setCreatedBy("SYSTEM");
        BranchEntity saved = branchRepositoryPort.save(entity);
        return branchMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public BranchResponse updateBranch(UpdateBranchRequest request) {
        log.info("Updating branch with ID: {}", request.getId());
        BranchEntity existing = branchRepositoryPort.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("Sucursal no encontrada con ID: " + request.getId()));

        OrganizationEntity organization = organizationRepositoryPort.findById(request.getOrganizationId())
                .orElseThrow(() -> new EntityNotFoundException("Organización no encontrada con ID: " + request.getOrganizationId()));

        if (!existing.getCode().equals(request.getCode()) &&
                branchRepositoryPort.existsByOrganizationIdAndCode(request.getOrganizationId(), request.getCode())) {
            throw new ValidationException("El código de sucursal ya existe para esta organización: " + request.getCode());
        }

        branchMapper.updateEntityFromDto(request, existing);
        existing.setOrganization(organization);
        existing.setUpdatedBy("SYSTEM");
        BranchEntity saved = branchRepositoryPort.save(existing);
        return branchMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BranchResponse getBranchById(UUID id) {
        log.debug("Fetching branch with ID: {}", id);
        return branchRepositoryPort.findById(id)
                .map(branchMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Sucursal no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchResponse> getBranchesByOrganizationId(UUID organizationId) {
        log.debug("Fetching branches by org ID: {}", organizationId);
        return branchRepositoryPort.findByOrganizationId(organizationId).stream()
                .map(branchMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchResponse> getAllBranches() {
        log.debug("Fetching all branches");
        return branchRepositoryPort.findAll().stream()
                .map(branchMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteBranch(UUID id) {
        log.info("Deleting branch with ID: {}", id);
        if (!branchRepositoryPort.findById(id).isPresent()) {
            throw new EntityNotFoundException("Sucursal no encontrada con ID: " + id);
        }
        branchRepositoryPort.deleteById(id);
    }
}
