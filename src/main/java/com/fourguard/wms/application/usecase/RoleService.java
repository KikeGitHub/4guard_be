package com.fourguard.wms.application.usecase;

import com.fourguard.wms.application.dto.request.CreateRoleRequest;
import com.fourguard.wms.application.dto.request.UpdateRoleRequest;
import com.fourguard.wms.application.dto.response.RoleResponse;
import com.fourguard.wms.application.mapper.RoleMapper;
import com.fourguard.wms.domain.exception.EntityNotFoundException;
import com.fourguard.wms.domain.exception.ValidationException;
import com.fourguard.wms.domain.ports.in.RoleUseCase;
import com.fourguard.wms.domain.ports.out.PermissionRepositoryPort;
import com.fourguard.wms.domain.ports.out.RoleRepositoryPort;
import com.fourguard.wms.infrastructure.persistence.entity.PermissionEntity;
import com.fourguard.wms.infrastructure.persistence.entity.RoleEntity;
import com.fourguard.wms.shared.audit.SecurityAuditHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Implementación del use case de Roles.
 *
 * <p>Sigue el mismo patrón que {@code BranchService}: trabaja directamente con
 * entidades JPA a través de los ports OUT, y usa el mapper para producir los DTOs
 * de respuesta. No genera un modelo de dominio intermedio.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService implements RoleUseCase {

    private final RoleRepositoryPort     roleRepositoryPort;
    private final PermissionRepositoryPort permissionRepositoryPort;
    private final RoleMapper             roleMapper;
    private final SecurityAuditHelper    securityAuditHelper;

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public RoleResponse createRole(CreateRoleRequest request) {
        log.info("Creating role with name: {}", request.getName());

        validateUniqueRoleName(request.getName(), null);

        RoleEntity entity = roleMapper.toEntity(request);
        entity.setCreatedBy(securityAuditHelper.getCurrentUsername());

        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            Set<PermissionEntity> permissions = resolvePermissions(request.getPermissionIds());
            entity.setPermissions(permissions);
        }

        RoleEntity saved = roleRepositoryPort.save(entity);
        log.info("Role created successfully with ID: {}", saved.getId());
        return roleMapper.toResponse(saved);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public RoleResponse updateRole(UpdateRoleRequest request) {
        log.info("Updating role with ID: {}", request.getId());

        RoleEntity existing = findRoleOrThrow(request.getId());
        validateUniqueRoleName(request.getName(), existing.getId());

        roleMapper.updateEntityFromDto(request, existing);
        existing.setUpdatedBy(securityAuditHelper.getCurrentUsername());

        if (request.getPermissionIds() != null) {
            Set<PermissionEntity> permissions = resolvePermissions(request.getPermissionIds());
            existing.getPermissions().clear();
            existing.getPermissions().addAll(permissions);
        }

        RoleEntity saved = roleRepositoryPort.save(existing);
        log.info("Role updated successfully with ID: {}", saved.getId());
        return roleMapper.toResponse(saved);
    }

    // ── GET BY ID ─────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(UUID id) {
        log.debug("Fetching role with ID: {}", id);
        // Usa findByIdWithPermissions para resolver permisos en un solo JOIN (anti-N+1)
        RoleEntity entity = roleRepositoryPort.findByIdWithPermissions(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con ID: " + id));
        return roleMapper.toResponse(entity);
    }

    // ── GET ALL ───────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        log.debug("Fetching all roles");
        return roleMapper.toResponseList(roleRepositoryPort.findAll());
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteRole(UUID id) {
        log.info("Deleting role with ID: {}", id);

        RoleEntity existing = findRoleOrThrow(id);

        if (Boolean.TRUE.equals(existing.getIsSystem())) {
            throw new ValidationException("No se puede eliminar un rol de sistema: " + existing.getName());
        }

        if (roleRepositoryPort.existsUserAssignedToRole(id)) {
            throw new ValidationException(
                "No se puede eliminar el rol '" + existing.getName() +
                "' porque tiene usuarios asignados. Reasigne los usuarios antes de eliminarlo."
            );
        }

        roleRepositoryPort.deleteById(id);
        log.info("Role deleted successfully with ID: {}", id);
    }

    // ── ASSIGN PERMISSIONS ────────────────────────────────────────────────────

    @Override
    @Transactional
    public RoleResponse assignPermissions(UUID roleId, Set<UUID> permissionIds) {
        log.info("Assigning {} permissions to role ID: {}", permissionIds.size(), roleId);

        RoleEntity existing = roleRepositoryPort.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con ID: " + roleId));

        Set<PermissionEntity> permissions = permissionIds.isEmpty()
                ? Collections.emptySet()
                : resolvePermissions(permissionIds);

        existing.getPermissions().clear();
        existing.getPermissions().addAll(permissions);
        existing.setUpdatedBy(securityAuditHelper.getCurrentUsername());

        RoleEntity saved = roleRepositoryPort.save(existing);
        log.info("Permissions assigned successfully to role ID: {}", roleId);
        return roleMapper.toResponse(saved);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private RoleEntity findRoleOrThrow(UUID id) {
        return roleRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con ID: " + id));
    }

    /**
     * Valida que el nombre del rol sea único en el sistema.
     *
     * @param name      el nombre a validar
     * @param excludeId ID del rol a excluir de la validación (para UPDATE), null en CREATE
     */
    private void validateUniqueRoleName(String name, UUID excludeId) {
        roleRepositoryPort.findByName(name).ifPresent(existing -> {
            if (!existing.getId().equals(excludeId)) {
                throw new ValidationException("Ya existe un rol con el nombre: " + name);
            }
        });
    }

    /**
     * Resuelve un conjunto de IDs de permisos a entidades JPA.
     * Lanza {@code ValidationException} si algún ID no existe.
     */
    private Set<PermissionEntity> resolvePermissions(Set<UUID> ids) {
        Set<PermissionEntity> found = permissionRepositoryPort.findAllByIds(ids);
        if (found.size() != ids.size()) {
            throw new ValidationException(
                "Uno o más IDs de permisos no son válidos. Verifique los IDs enviados."
            );
        }
        return found;
    }
}
