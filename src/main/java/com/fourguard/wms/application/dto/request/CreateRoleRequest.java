package com.fourguard.wms.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

/**
 * Request DTO para crear un nuevo Rol.
 */
@Getter
@Builder
@Schema(description = "Payload para la creación de un nuevo rol")
public class CreateRoleRequest {

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    @Schema(description = "Nombre único del rol", example = "WAREHOUSE_MANAGER")
    private final String name;

    @NotNull(message = "El nivel del rol es obligatorio")
    @Min(value = 1, message = "El nivel mínimo es 1")
    @Max(value = 7, message = "El nivel máximo es 7")
    @Schema(description = "Nivel jerárquico del rol (1=más alto, 7=más bajo)", example = "3")
    private final Integer level;

    @Schema(description = "Indica si es un rol de sistema no eliminable", example = "false")
    private final Boolean isSystem;

    @Schema(description = "Conjunto de IDs de permisos a asignar al crear el rol")
    private final Set<UUID> permissionIds;
}
