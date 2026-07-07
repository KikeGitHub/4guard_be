package com.fourguard.wms.application.dto.request;

import com.fourguard.wms.domain.enums.OrganizationStatus;
import com.fourguard.wms.domain.enums.OrganizationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.util.Map;
import java.util.UUID;

/** Request DTO for updating an Organization. */
@Value
@Builder
public class UpdateOrganizationRequest {

    @NotNull(message = "El ID de la organización es requerido")
    UUID id;

    @NotBlank(message = "El nombre de la organización es requerido")
    @Size(max = 200, message = "El nombre no puede superar 200 caracteres")
    String name;

    @Size(max = 20)
    String taxId;

    @NotNull(message = "El tipo de organización es requerido")
    OrganizationType type;

    @NotNull(message = "El estatus de la organización es requerido")
    OrganizationStatus status;

    Map<String, Object> settings;
}
