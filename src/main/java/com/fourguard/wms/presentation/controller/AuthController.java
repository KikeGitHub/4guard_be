package com.fourguard.wms.presentation.controller;

import com.fourguard.wms.application.dto.request.auth.LoginRequest;
import com.fourguard.wms.application.dto.request.auth.RefreshTokenRequest;
import com.fourguard.wms.application.dto.response.auth.AuthResponse;
import com.fourguard.wms.domain.ports.in.LoginUseCase;
import com.fourguard.wms.domain.ports.in.RefreshTokenUseCase;
import com.fourguard.wms.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for Authentication.
 * Context-path /api/v1 is prefixing this endpoint as configured in properties.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints públicos para el control de sesiones y tokens")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Verifica credenciales del usuario y devuelve tokens de acceso (JWT)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sesión iniciada con éxito"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada malformados"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Credenciales incorrectas o cuenta inactiva")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = loginUseCase.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Sesión iniciada con éxito", response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refrescar token", description = "Genera un nuevo token de acceso a partir de un refresh token válido")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tokens refrescados con éxito"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token de refresco inválido o expirado")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = refreshTokenUseCase.refresh(request);
        return ResponseEntity.ok(ApiResponse.ok("Tokens refrescados con éxito", response));
    }
}
