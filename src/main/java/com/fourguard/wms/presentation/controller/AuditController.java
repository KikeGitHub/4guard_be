package com.fourguard.wms.presentation.controller;

import com.fourguard.wms.application.dto.response.audit.ActiveSessionResponse;
import com.fourguard.wms.domain.ports.in.GetActiveSessionsUseCase;
import com.fourguard.wms.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for active sessions audit querying.
 */
@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
@Tag(name = "Auditoría", description = "Endpoints para la consulta de bitácoras y sesiones activas")
public class AuditController {

    private final GetActiveSessionsUseCase getActiveSessionsUseCase;

    @GetMapping("/active-sessions")
    @PreAuthorize("hasAuthority('AUDIT_READ') or hasRole('OPERATIONS_MANAGER')")
    @Operation(
            summary = "Consultar sesiones activas",
            description = "Devuelve el listado de usuarios con sesión activa (logueados en las últimas 24 horas y sin logout posterior).")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sesiones activas obtenidas con éxito"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado o permisos insuficientes")
    })
    public ResponseEntity<ApiResponse<List<ActiveSessionResponse>>> getActiveSessions(
            @RequestParam(required = false) UUID organizationId,
            @RequestParam(required = false) UUID branchId,
            Principal principal) {

        List<ActiveSessionResponse> activeSessions =
                getActiveSessionsUseCase.getActiveSessions(organizationId, branchId, principal);

        return ResponseEntity.ok(
                ApiResponse.ok("Sesiones activas recuperadas con éxito", activeSessions));
    }
}
