package com.fourguard.wms.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

/**
 * Encapsulates all query parameters for GET /api/suppliers.
 * Built from @RequestParam in the controller and passed to SupplierSpecification.
 */
@Value
@Builder
public class SupplierFilterRequest {

    @Schema(description = "Búsqueda libre: legal_name, commercial_name, tax_id, code, email, ciudad", example = "EmpaquesNorte")
    String search;

    @Schema(description = "Filtrar por estado: ACTIVE | INACTIVE | BLOCKED")
    String status;

    @Schema(description = "Filtrar por tipo (FK code de cat_supplier_types)", example = "PACKAGING")
    String type;

    @Schema(description = "Filtrar por scope: GLOBAL | CLIENT | WAREHOUSE")
    String scopeType;

    @Schema(description = "UUID de la organización (requerido internamente en el servicio)")
    UUID organizationId;

    @Schema(description = "UUID del cliente para filtrar por scope=CLIENT")
    UUID clientId;

    /** Maps to branch_id in the DB. Named warehouseId for FE compatibility. */
    @Schema(description = "UUID del branch/almacén para filtrar por scope=WAREHOUSE (alias de branchId)")
    UUID warehouseId;

    @Schema(description = "Si true, retorna solo proveedores preferentes", example = "true")
    Boolean preferredOnly;
}
