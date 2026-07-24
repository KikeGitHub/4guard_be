package com.fourguard.wms.application.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fourguard.wms.domain.enums.LocationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

/** Request DTO for updating a Location. Status changes must use PATCH /locations/{id}/status. */
@Value
@Builder
public class UpdateLocationRequest {

    @NotNull(message = "El ID de la ubicación es requerido")
    UUID id;

    @NotNull(message = "El ID de la sucursal es requerido")
    UUID branchId;

    UUID sectionId;

    @NotBlank(message = "La zona es requerida")
    @Size(max = 10, message = "La zona no puede superar 10 caracteres")
    String zone;

    @Size(max = 10, message = "El pasillo no puede superar 10 caracteres")
    String aisle;

    @Size(max = 10, message = "El rack no puede superar 10 caracteres")
    String rack;

    Integer level;

    @Size(max = 10, message = "La posición no puede superar 10 caracteres")
    String position;

    Integer coordX;
    Integer coordY;
    Integer coordZ;

    @NotNull(message = "El tipo de ubicación es requerido")
    LocationType type;

    Integer capacityUnits;

    Boolean isBlocked;
    String blockReason;

    /** Optional human-readable unique code. Returns HTTP 409 if already taken by another location. */
    @Size(max = 30, message = "El código no puede superar 30 caracteres")
    String code;

    /** Optional descriptive name. */
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    String name;

    /** Optional observations or notes. */
    @JsonAlias({"observations", "observaciones"})
    String notes;
}

