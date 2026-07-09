package com.fourguard.wms.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Response DTO para un Rol.
 *
 * <p>Incluye el conjunto completo de permisos para evitar llamadas adicionales
 * al cliente. Se usa {@link PermissionResponse} embebido.</p>
 */
@Getter
@Builder
@Schema(description = "Representación de un Rol del sistema con sus permisos asignados")
public class RoleResponse {

    @Schema(description = "ID único del rol")
    private final UUID id;

    @Schema(description = "Nombre del rol", example = "WAREHOUSE_MANAGER")
    private final String name;

    @Schema(description = "Nivel jerárquico (1=más alto, 7=más bajo)", example = "3")
    private final Integer level;

    @Schema(description = "Indica si es un rol de sistema protegido", example = "false")
    private final Boolean isSystem;

    @Schema(description = "Permisos asignados al rol")
    private final Set<PermissionResponse> permissions;

    @Schema(description = "Versión para control de concurrencia optimista")
    private final Long version;

    @Schema(description = "Fecha y hora de creación")
    private final OffsetDateTime createdAt;

    @Schema(description = "Fecha y hora de última actualización")
    private final OffsetDateTime updatedAt;

    @Schema(description = "Usuario que creó el rol")
    private final String createdBy;

    @Schema(description = "Usuario que realizó la última actualización")
    private final String updatedBy;
}
