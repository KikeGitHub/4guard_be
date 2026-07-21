package com.fourguard.wms.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

/** Request DTO for updating carrier status with reason and observations. */
@Value
@Builder
public class UpdateCarrierStatusRequest {

    @NotNull(message = "El nuevo estado es requerido")
    @Schema(description = "Nuevo estado operativo (ACTIVE, INACTIVE, SUSPENDED)", example = "SUSPENDED")
    String status;

    @Schema(description = "Motivo de la suspensión o inactivación", example = "Retrasos recurrentes en entregas")
    String reason;

    @Schema(description = "Observaciones adicionales opcionales", example = "Reportado por el cliente FEMSA en rampa 4.")
    String observations;
}
