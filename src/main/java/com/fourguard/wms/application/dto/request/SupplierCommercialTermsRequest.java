package com.fourguard.wms.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

/** Sub-DTO for supplier commercial terms (nested in Create/UpdateSupplierRequest). */
@Value
@Builder
public class SupplierCommercialTermsRequest {

    @Min(value = 0, message = "El tiempo de entrega no puede ser negativo")
    @Schema(description = "Tiempo de entrega/respuesta en días", example = "3")
    Integer leadTimeDays;

    @DecimalMin(value = "0.00", message = "El monto mínimo de orden no puede ser negativo")
    @Schema(description = "Monto mínimo de orden", example = "15000.00")
    BigDecimal minimumOrderAmount;

    @Min(value = 0, message = "Los días de crédito no pueden ser negativos")
    @Schema(description = "Días de crédito", example = "30")
    Integer creditDays;

    /** ISO 4217 currency code. FK → wms.cat_currencies. */
    @Size(min = 3, max = 3, message = "El código de moneda debe ser de 3 caracteres (ISO 4217)")
    @Schema(description = "Código de moneda ISO 4217", example = "MXN")
    String currency;

    @Schema(description = "¿Se requiere inspección de calidad en recepción?", example = "true")
    Boolean qualityInspectionRequired;
}
