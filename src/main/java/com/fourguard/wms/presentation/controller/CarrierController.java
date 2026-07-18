package com.fourguard.wms.presentation.controller;

import com.fourguard.wms.application.dto.request.CreateCarrierRequest;
import com.fourguard.wms.application.dto.request.UpdateCarrierRequest;
import com.fourguard.wms.application.dto.response.CarrierResponse;
import com.fourguard.wms.domain.ports.in.CarrierUseCase;
import com.fourguard.wms.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/** REST controller for Carrier Management. */
@RestController
@RequestMapping("/carriers")
@RequiredArgsConstructor
@Tag(name = "Transportistas", description = "Endpoints para la gestión y administración de líneas de transporte (Carriers)")
public class CarrierController {

    private final CarrierUseCase carrierUseCase;

    @PostMapping
    @PreAuthorize("hasAuthority('CARRIERS_CREATE') or hasRole('OPERATIONS_MANAGER')")
    @Operation(summary = "Crear transportista", description = "Registra un nuevo transportista en el WMS.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transportista creado con éxito"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Permisos insuficientes")
    })
    public ResponseEntity<ApiResponse<CarrierResponse>> createCarrier(@Valid @RequestBody CreateCarrierRequest request) {
        CarrierResponse response = carrierUseCase.createCarrier(request);
        return ResponseEntity.ok(ApiResponse.ok("Transportista creado con éxito", response));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('CARRIERS_UPDATE') or hasRole('OPERATIONS_MANAGER')")
    @Operation(summary = "Actualizar transportista", description = "Actualiza los datos de un transportista existente.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transportista actualizado con éxito"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Permisos insuficientes"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Transportista no encontrado")
    })
    public ResponseEntity<ApiResponse<CarrierResponse>> updateCarrier(@Valid @RequestBody UpdateCarrierRequest request) {
        CarrierResponse response = carrierUseCase.updateCarrier(request);
        return ResponseEntity.ok(ApiResponse.ok("Transportista actualizado con éxito", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CARRIERS_READ') or hasRole('OPERATIONS_MANAGER')")
    @Operation(summary = "Obtener transportista por ID", description = "Recupera los detalles de un transportista específico por su UUID.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transportista encontrado con éxito"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Permisos insuficientes"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Transportista no encontrado")
    })
    public ResponseEntity<ApiResponse<CarrierResponse>> getCarrierById(@PathVariable UUID id) {
        CarrierResponse response = carrierUseCase.getCarrierById(id);
        return ResponseEntity.ok(ApiResponse.ok("Transportista encontrado con éxito", response));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CARRIERS_READ') or hasRole('OPERATIONS_MANAGER')")
    @Operation(summary = "Obtener transportistas", description = "Recupera la lista de transportistas, opcionalmente filtrando por organización.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de transportistas recuperada con éxito"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Permisos insuficientes")
    })
    public ResponseEntity<ApiResponse<List<CarrierResponse>>> getCarriers(@RequestParam(required = false) UUID organizationId) {
        List<CarrierResponse> response;
        if (organizationId != null) {
            response = carrierUseCase.getCarriersByOrganizationId(organizationId);
        } else {
            response = carrierUseCase.getAllCarriers();
        }
        return ResponseEntity.ok(ApiResponse.ok("Lista de transportistas recuperada con éxito", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CARRIERS_DELETE') or hasRole('OPERATIONS_MANAGER')")
    @Operation(summary = "Eliminar transportista", description = "Elimina físicamente un transportista del sistema por su ID.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transportista eliminado con éxito"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Permisos insuficientes"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Transportista no encontrado")
    })
    public ResponseEntity<ApiResponse<Void>> deleteCarrier(@PathVariable UUID id) {
        carrierUseCase.deleteCarrier(id);
        return ResponseEntity.ok(ApiResponse.ok("Transportista eliminado con éxito"));
    }
}
