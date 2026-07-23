package com.fourguard.wms.application.dto.request;

import com.fourguard.wms.domain.enums.WarehouseSectionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

/** Request DTO for updating warehouse section status via PATCH. */
@Value
@Builder
public class UpdateWarehouseSectionStatusRequest {

    @NotNull(message = "El campo 'status' es obligatorio")
    @Schema(description = "Nuevo estado de la sección (ACTIVE, INACTIVE)", example = "INACTIVE")
    WarehouseSectionStatus status;
}
