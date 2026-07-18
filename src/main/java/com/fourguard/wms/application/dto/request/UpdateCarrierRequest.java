package com.fourguard.wms.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

/** Request DTO for updating a Carrier. */
@Value
@Builder
public class UpdateCarrierRequest {

    @NotNull(message = "El ID del transportista es requerido")
    @Schema(description = "UUID del transportista a actualizar", example = "c73f0907-9fa5-4bdf-87db-2eb5e7683938")
    UUID id;

    @NotNull(message = "El ID de la organización es requerido")
    @Schema(description = "UUID de la organización propietaria", example = "a53f0907-9fa5-4bdf-87db-2eb5e7683935")
    UUID organizationId;

    @Schema(description = "Nombre de la organización (solo referencial)", example = "4GUARD LOGISTICS CORP")
    String organizationName;

    @NotBlank(message = "El nombre del transportista es requerido")
    @Size(min = 2, max = 200, message = "El nombre debe tener entre 2 y 200 caracteres")
    @Schema(description = "Nombre o razón social del transportista", example = "Transportes del Norte S.A.")
    String name;

    @Size(max = 200, message = "El nombre comercial no puede superar 200 caracteres")
    @Schema(description = "Nombre comercial", example = "DHL Express")
    String tradeName;

    @Size(max = 30, message = "El tax ID / RFC no puede superar 30 caracteres")
    @Pattern(regexp = "^[A-Z0-9&\\-\\.\\s]*$", message = "RFC con formato inválido")
    @Schema(description = "RFC o Tax ID del transportista", example = "TNO991231AB0")
    String taxId;

    @Size(max = 150, message = "El nombre del contacto no puede superar 150 caracteres")
    @Schema(description = "Nombre del representante o contacto", example = "Juan Pérez")
    String contactName;

    @Size(max = 20, message = "El teléfono no puede superar 20 caracteres")
    @Pattern(regexp = "^\\+?[0-9\\s\\-()]{7,20}$", message = "Formato de teléfono inválido")
    @Schema(description = "Teléfono de contacto", example = "+52 55 1234 5678")
    String contactPhone;

    @Email(message = "Debe proporcionar un correo electrónico válido")
    @Size(max = 255, message = "El correo no puede superar 255 caracteres")
    @Schema(description = "Correo electrónico de contacto", example = "contacto@transportes.com")
    String contactEmail;

    @Schema(description = "Estado del transportista", example = "ACTIVE")
    String status;

    @Schema(description = "Versión del registro para optimistic locking. Si no se envía se respeta el valor actual en BD", example = "1")
    Long version;
}
