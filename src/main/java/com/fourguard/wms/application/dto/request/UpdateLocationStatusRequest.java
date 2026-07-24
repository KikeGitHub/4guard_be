package com.fourguard.wms.application.dto.request;

import com.fourguard.wms.domain.enums.LocationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

/**
 * Request DTO for the FSM status-change endpoint:
 * {@code PATCH /api/v1/locations/{id}/status}
 *
 * <p>Business rules enforced in the service layer:
 * <ul>
 *   <li>{@code reason} is mandatory when {@code status = BLOCKED} or {@code MAINTENANCE}</li>
 *   <li>Only FSM-allowed transitions are accepted (HTTP 422 otherwise)</li>
 *   <li>Transition to {@code INACTIVE} requires {@code currentOccupancy = 0} (HTTP 409 otherwise)</li>
 * </ul>
 */
@Value
@Builder
public class UpdateLocationStatusRequest {

    @NotNull(message = "El campo 'status' es obligatorio")
    @Schema(
        description = "Nuevo estado FSM de la ubicación",
        example = "BLOCKED",
        allowableValues = {"ACTIVE", "BLOCKED", "MAINTENANCE", "INACTIVE"}
    )
    LocationStatus status;

    @Schema(
        description = "Motivo del cambio de estado. Obligatorio para BLOCKED y MAINTENANCE.",
        example = "Revisión de estructura metálica"
    )
    String reason;
}
