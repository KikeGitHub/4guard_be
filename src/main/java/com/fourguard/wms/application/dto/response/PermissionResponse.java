package com.fourguard.wms.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Response DTO para un Permiso del catálogo.
 */
@Getter
@Builder
@Schema(description = "Representación de un Permiso del sistema")
public class PermissionResponse {

    @Schema(description = "ID único del permiso")
    private final UUID id;

    @Schema(description = "Nombre único del permiso en formato ENTIDAD_ACCION", example = "INVENTORY_READ")
    private final String name;

    @Schema(description = "Descripción funcional del permiso")
    private final String description;

    @Schema(description = "Fecha y hora de creación en el catálogo")
    private final OffsetDateTime createdAt;
}
