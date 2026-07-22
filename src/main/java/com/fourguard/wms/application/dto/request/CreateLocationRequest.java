package com.fourguard.wms.application.dto.request;

import com.fourguard.wms.domain.enums.LocationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

/** Request DTO for creating a Location. */
@Value
@Builder
public class CreateLocationRequest {

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

    /** Optional human-readable unique code (e.g. "ALMC-A-R1-N2"). Generated or left null if absent. */
    @Size(max = 30, message = "El código no puede superar 30 caracteres")
    String code;

    /** Optional descriptive name (e.g. "Pasillo A – Rack 1 – Nivel 2"). */
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    String name;
}

