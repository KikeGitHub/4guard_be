package com.fourguard.wms.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

/** Request DTO for updating a Warehouse Section. */
@Value
@Builder
public class UpdateWarehouseSectionRequest {

    @NotNull(message = "El ID de la sección es requerido")
    UUID id;

    @NotNull(message = "El ID de la sucursal es requerido")
    UUID branchId;

    @NotBlank(message = "El código de la sección es requerido")
    @Size(max = 10, message = "El código no puede superar 10 caracteres")
    String code;

    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    String name;
}
