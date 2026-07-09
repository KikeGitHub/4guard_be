package com.fourguard.wms.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

/** Request DTO for creating a Client. */
@Value
@Builder
public class CreateClientRequest {

    @NotNull(message = "El ID de la organización es requerido")
    UUID organizationId;

    @NotBlank(message = "El nombre del cliente es requerido")
    @Size(max = 200, message = "El nombre no puede superar 200 caracteres")
    String name;

    @Size(max = 50, message = "El ID externo no puede superar 50 caracteres")
    String externalId;

    String status;
}
