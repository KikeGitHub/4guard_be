package com.fourguard.wms.application.usecase;

import com.fourguard.wms.application.dto.request.CreatePermissionRequest;
import com.fourguard.wms.application.dto.response.PermissionResponse;
import com.fourguard.wms.application.mapper.PermissionMapper;
import com.fourguard.wms.domain.exception.EntityNotFoundException;
import com.fourguard.wms.domain.exception.ValidationException;
import com.fourguard.wms.domain.ports.in.PermissionUseCase;
import com.fourguard.wms.domain.ports.out.PermissionRepositoryPort;
import com.fourguard.wms.infrastructure.persistence.entity.PermissionEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implementación del use case de Permisos.
 *
 * <p>Los permisos son un catálogo semi-estático. No se expone operación de UPDATE
 * porque la tabla {@code wms.permissions} no tiene columnas de auditoría mutables
 * ({@code updated_at}, {@code version}, etc.).</p>
 *
 * <p>Sigue el mismo patrón que {@code BranchService}: trabaja con entidades JPA
 * directamente a través del port OUT.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionService implements PermissionUseCase {

    private final PermissionRepositoryPort permissionRepositoryPort;
    private final PermissionMapper         permissionMapper;

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public PermissionResponse createPermission(CreatePermissionRequest request) {
        log.info("Creating permission with name: {}", request.getName());

        if (permissionRepositoryPort.existsByName(request.getName())) {
            throw new ValidationException("Ya existe un permiso con el nombre: " + request.getName());
        }

        PermissionEntity entity = permissionMapper.toEntity(request);
        PermissionEntity saved  = permissionRepositoryPort.save(entity);

        log.info("Permission created successfully with ID: {}", saved.getId());
        return permissionMapper.toResponse(saved);
    }

    // ── GET BY ID ─────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public PermissionResponse getPermissionById(UUID id) {
        log.debug("Fetching permission with ID: {}", id);
        PermissionEntity entity = permissionRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Permiso no encontrado con ID: " + id));
        return permissionMapper.toResponse(entity);
    }

    // ── GET ALL ───────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getAllPermissions() {
        log.debug("Fetching all permissions");
        return permissionMapper.toResponseList(permissionRepositoryPort.findAll());
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deletePermission(UUID id) {
        log.info("Deleting permission with ID: {}", id);

        if (permissionRepositoryPort.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Permiso no encontrado con ID: " + id);
        }

        permissionRepositoryPort.deleteById(id);
        // Las filas en role_permissions se eliminan automáticamente por ON DELETE CASCADE en DB
        log.info("Permission deleted successfully with ID: {}", id);
    }
}
