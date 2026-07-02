package com.fourguard.wms.presentation.controller;

import com.fourguard.wms.application.dto.response.notification.NotificationResponse;
import com.fourguard.wms.domain.ports.in.NotificationUseCase;
import com.fourguard.wms.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for in-app notifications.
 *
 * <p>Each user can only read and acknowledge their own notifications.
 * The authenticated principal is derived from the JWT token.</p>
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "Gestión de notificaciones in-app para el usuario autenticado")
public class NotificationController {

    private final NotificationUseCase notificationUseCase;

    @GetMapping
    @Operation(
            summary = "Listar mis notificaciones",
            description = "Devuelve las notificaciones del usuario autenticado. " +
                          "Filtrar solo no leídas con ?unreadOnly=true")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notificaciones recuperadas con éxito"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications(
            @RequestParam(defaultValue = "false") boolean unreadOnly,
            Principal principal) {

        List<NotificationResponse> notifications =
                notificationUseCase.getMyNotifications(principal.getName(), unreadOnly);

        return ResponseEntity.ok(
                ApiResponse.ok("Notificaciones recuperadas con éxito", notifications));
    }

    @PatchMapping("/{id}/read")
    @Operation(
            summary = "Marcar notificación como leída",
            description = "Marca una notificación específica como leída. Solo el destinatario puede realizar esta acción.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notificación marcada como leída"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Notificación no encontrada o no pertenece al usuario")
    })
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable UUID id,
            Principal principal) {

        notificationUseCase.markAsRead(id, principal.getName());
        return ResponseEntity.ok(ApiResponse.ok("Notificación marcada como leída"));
    }
}
