package com.fourguard.wms.application.dto.request;

import com.fourguard.wms.domain.enums.BranchStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

/** Request DTO for updating a Branch. */
@Value
@Builder
public class UpdateBranchRequest {

    @NotNull(message = "El ID de la sucursal es requerido")
    UUID id;

    @NotNull(message = "El ID de la organización es requerido")
    UUID organizationId;

    @NotBlank(message = "El nombre de la sucursal es requerido")
    @Size(max = 200, message = "El nombre no puede superar 200 caracteres")
    String name;

    @NotBlank(message = "El código de la sucursal es requerido")
    @Size(max = 20, message = "El código no puede superar 20 caracteres")
    String code;

    @Size(max = 50)
    String timezone;

    String addressLine1;

    @NotNull(message = "El estatus es requerido")
    BranchStatus status;
}
