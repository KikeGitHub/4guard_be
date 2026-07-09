package com.fourguard.wms.application.mapper;

import com.fourguard.wms.application.dto.request.CreateRoleRequest;
import com.fourguard.wms.application.dto.request.UpdateRoleRequest;
import com.fourguard.wms.application.dto.response.RoleResponse;
import com.fourguard.wms.infrastructure.persistence.entity.RoleEntity;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper — RoleEntity ↔ DTOs.
 *
 * <p>Sigue exactamente el mismo patrón que {@link BranchMapper}:
 * mapea directo entre Entity y DTO sin pasar por el modelo de dominio,
 * ya que la capa de aplicación trabaja con entidades (patrón establecido en el proyecto).</p>
 */
@Mapper(componentModel = "spring", uses = {PermissionMapper.class})
public interface RoleMapper {

    // ── CreateRequest → Entity ────────────────────────────────────────────────

    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "permissions", ignore = true) // Resuelto en el service con PermissionRepositoryPort
    @Mapping(target = "isSystem",    expression = "java(request.getIsSystem() != null ? request.getIsSystem() : false)")
    @Mapping(target = "version",     ignore = true)
    @Mapping(target = "createdAt",   ignore = true)
    @Mapping(target = "updatedAt",   ignore = true)
    @Mapping(target = "createdBy",   ignore = true)
    @Mapping(target = "updatedBy",   ignore = true)
    RoleEntity toEntity(CreateRoleRequest request);

    // ── UpdateRequest → Entity (patch sobre entidad existente) ────────────────

    @Mapping(target = "id",          ignore = true) // El ID viene de la entidad existente
    @Mapping(target = "permissions", ignore = true) // Resuelto en el service
    @Mapping(target = "isSystem",    expression = "java(request.getIsSystem() != null ? request.getIsSystem() : entity.getIsSystem())")
    @Mapping(target = "version",     ignore = true)
    @Mapping(target = "createdAt",   ignore = true)
    @Mapping(target = "updatedAt",   ignore = true)
    @Mapping(target = "createdBy",   ignore = true)
    @Mapping(target = "updatedBy",   ignore = true)
    void updateEntityFromDto(UpdateRoleRequest request, @MappingTarget RoleEntity entity);

    // ── Entity → Response ─────────────────────────────────────────────────────

    RoleResponse toResponse(RoleEntity entity);

    List<RoleResponse> toResponseList(List<RoleEntity> entities);
}
