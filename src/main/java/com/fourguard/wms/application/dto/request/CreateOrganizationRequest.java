package com.fourguard.wms.application.dto.request;

import com.fourguard.wms.domain.enums.OrganizationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

/** Request DTO for creating an Organization. */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrganizationRequest {

    @NotBlank(message = "El nombre de la organización es requerido")
    @Size(max = 200, message = "El nombre no puede superar 200 caracteres")
    private String name;

    @NotBlank(message = "El código de la organización es requerido")
    @Size(max = 20, message = "El código no puede superar 20 caracteres")
    private String code;

    @Size(max = 20)
    private String taxId;

    @NotNull(message = "El tipo de organización es requerido")
    private OrganizationType type;

    private Map<String, Object> settings;
}
