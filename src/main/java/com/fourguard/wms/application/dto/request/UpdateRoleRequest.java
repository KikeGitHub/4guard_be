package com.fourguard.wms.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

/**
 * Request DTO para actualizar un Rol existente.
 */
@Getter
@Builder
@Schema(description = "Payload para actualizar un rol existente")
public class UpdateRoleRequest {

    @NotNull(message = "El ID del rol es obligatorio para la actualización")
    @Schema(description = "ID único del rol a actualizar", example = "550e8400-e29b-41d4-a716-446655440000")
    private final UUID id;

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    @Schema(description = "Nuevo nombre del rol", example = "WAREHOUSE_SUPERVISOR")
    private final String name;

    @NotNull(message = "El nivel del rol es obligatorio")
    @Min(value = 1, message = "El nivel mínimo es 1")
    @Max(value = 7, message = "El nivel máximo es 7")
    @Schema(description = "Nivel jerárquico del rol (1=más alto, 7=más bajo)", example = "3")
    private final Integer level;

    @Schema(description = "Indica si es un rol de sistema no eliminable", example = "false")
    private final Boolean isSystem;

    @Schema(description = "Conjunto de IDs de permisos. Si se provee, reemplaza los permisos actuales del rol.")
    private final Set<UUID> permissionIds;
}
