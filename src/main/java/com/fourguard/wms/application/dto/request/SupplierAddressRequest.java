package com.fourguard.wms.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

/** Sub-DTO for supplier address data (nested in Create/UpdateSupplierRequest). */
@Value
@Builder
public class SupplierAddressRequest {

    @NotBlank(message = "El estado/provincia es requerido")
    @Size(max = 80)
    @Schema(description = "Estado o provincia", example = "Nuevo León")
    String state;

    @Size(max = 80)
    @Schema(description = "Municipio (opcional)", example = "Apodaca")
    String municipality;

    @NotBlank(message = "La ciudad es requerida")
    @Size(max = 80)
    @Schema(description = "Ciudad", example = "Monterrey")
    String city;

    @Size(max = 80)
    @Schema(description = "País", example = "México")
    String country;

    @Size(max = 10)
    @Schema(description = "Código postal", example = "66600")
    String postalCode;

    @Size(max = 200)
    @Schema(description = "Calle o avenida", example = "Av. Industrias Alimentarias")
    String street;

    @Size(max = 20)
    @Schema(description = "Número exterior", example = "450")
    String exteriorNumber;

    @Size(max = 20)
    @Schema(description = "Número interior o piso", example = "Piso 8")
    String interiorNumber;
}
