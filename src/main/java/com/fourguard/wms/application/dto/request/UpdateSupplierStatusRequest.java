package com.fourguard.wms.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

/**
 * Request DTO for changing a Supplier's operational status.
 * The 'reason' field is mandatory in the service layer when status is INACTIVE or BLOCKED.
 */
@Value
@Builder
public class UpdateSupplierStatusRequest {

    @NotBlank(message = "El estado es requerido")
    @Schema(description = "Nuevo estado del proveedor: ACTIVE | INACTIVE | BLOCKED", example = "INACTIVE")
    String status;

    @Schema(description = "Motivo del cambio de estado (obligatorio si status ≠ ACTIVE)", example = "Incumplimiento de SLA")
    String reason;
}
