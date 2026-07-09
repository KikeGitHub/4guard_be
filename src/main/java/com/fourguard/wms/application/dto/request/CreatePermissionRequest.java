package com.fourguard.wms.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

/**
 * Request DTO para crear un nuevo Permiso en el catálogo.
 */
@Getter
@Builder
@Schema(description = "Payload para la creación de un nuevo permiso")
public class CreatePermissionRequest {

    @NotBlank(message = "El nombre del permiso es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Schema(
        description = "Nombre único del permiso en formato ENTIDAD_ACCION",
        example = "INVENTORY_READ"
    )
    private final String name;

    @Schema(description = "Descripción funcional del permiso", example = "Permite ver el inventario de artículos")
    private final String description;
}
