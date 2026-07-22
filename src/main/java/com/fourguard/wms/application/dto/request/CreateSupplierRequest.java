package com.fourguard.wms.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

/** Request DTO for creating a Supplier. Mirrors supplier.model.ts from the FE. */
@Value
@Builder
public class CreateSupplierRequest {

    @NotNull(message = "El ID de la organización es requerido")
    @Schema(description = "UUID de la organización propietaria", example = "a53f0907-9fa5-4bdf-87db-2eb5e7683935")
    UUID organizationId;

    @NotBlank(message = "La razón social fiscal es requerida")
    @Size(min = 3, max = 250, message = "La razón social debe tener entre 3 y 250 caracteres")
    @Schema(description = "Razón social fiscal del proveedor", example = "Empaques Nacionales del Norte S.A. de C.V.")
    String legalName;

    @Size(max = 150, message = "El nombre comercial no puede superar 150 caracteres")
    @Schema(description = "Nombre comercial o marca del proveedor", example = "EmpaquesNorte")
    String commercialName;

    @NotBlank(message = "El RFC / Tax ID es requerido")
    @Size(min = 3, max = 20, message = "El RFC debe tener entre 3 y 20 caracteres")
    @Schema(description = "RFC (MX) o Tax ID del proveedor", example = "ENN980415HG8")
    String taxId;

    @NotBlank(message = "El tipo de proveedor es requerido")
    @Schema(description = "Código de tipo (FK → cat_supplier_types.code)", example = "PACKAGING")
    String type;

    @Schema(description = "¿Es proveedor preferente?", example = "true")
    Boolean preferred;

    @NotBlank(message = "El scope es requerido")
    @Schema(description = "Alcance 3PL: GLOBAL | CLIENT | WAREHOUSE", example = "GLOBAL")
    String scopeType;

    @Schema(description = "UUID o Código del cliente (requerido si scopeType=CLIENT)")
    String clientId;

    @Schema(description = "Nombre del cliente (opcional, usado para auto-creación en pruebas/demos)")
    String clientName;

    /** Maps to branch_id in BD. Named warehouseId for FE compatibility. */
    @Schema(description = "UUID o Código del branch/almacén (requerido si scopeType=WAREHOUSE). Alias de branchId.", example = "uuid")
    String warehouseId;

    @Schema(description = "Nombre del almacén/branch (opcional, usado para auto-creación en pruebas/demos)")
    String warehouseName;

    @Size(max = 2000, message = "Las notas no pueden superar 2000 caracteres")
    @Schema(description = "Notas operativas del proveedor")
    String notes;

    @NotNull(message = "El contacto principal es requerido")
    @Valid
    @Schema(description = "Datos del contacto principal del proveedor")
    SupplierContactRequest contact;

    @Valid
    @Schema(description = "Dirección fiscal u operativa del proveedor (opcional)")
    SupplierAddressRequest address;

    @Valid
    @Schema(description = "Condiciones comerciales del proveedor")
    SupplierCommercialTermsRequest commercialTerms;
}
