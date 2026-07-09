package com.fourguard.wms.application.mapper;

import com.fourguard.wms.application.dto.request.CreatePermissionRequest;
import com.fourguard.wms.application.dto.response.PermissionResponse;
import com.fourguard.wms.infrastructure.persistence.entity.PermissionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

/**
 * MapStruct mapper — PermissionEntity ↔ DTOs.
 *
 * <p>También es usado por {@link RoleMapper} para mapear el {@code Set<PermissionEntity>}
 * embebido en {@code RoleEntity}.</p>
 */
@Mapper(componentModel = "spring")
public interface PermissionMapper {

    // ── CreateRequest → Entity ────────────────────────────────────────────────

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "createdAt", ignore = true) // Asignado por @PrePersist en PermissionEntity
    PermissionEntity toEntity(CreatePermissionRequest request);

    // ── Entity → Response ─────────────────────────────────────────────────────

    PermissionResponse toResponse(PermissionEntity entity);

    List<PermissionResponse> toResponseList(List<PermissionEntity> entities);

    Set<PermissionResponse> toResponseSet(Set<PermissionEntity> entities);
}
