package com.fourguard.wms.presentation.controller;

import com.fourguard.wms.application.dto.UserCreateRequest;
import com.fourguard.wms.application.dto.UserResponse;
import com.fourguard.wms.application.dto.UserUpdateRequest;
import com.fourguard.wms.domain.ports.in.CreateUserUseCase;
import com.fourguard.wms.domain.ports.in.DeleteUserUseCase;
import com.fourguard.wms.domain.ports.in.GetUserUseCase;
import com.fourguard.wms.domain.ports.in.UpdateUserUseCase;
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

/**
 * REST controller for User Management.
 * Context-path /api/v1 is prefixing this endpoint as configured in properties.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Endpoints para la gestión y administración de usuarios")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    @PostMapping
    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario en el sistema. Requiere permisos de administrador.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario creado con éxito"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada malformados o duplicados"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Permisos insuficientes")
    })
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse response = createUserUseCase.createUser(request);
        return ResponseEntity.ok(ApiResponse.ok("Usuario creado con éxito", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'WMS_READ')")
    @Operation(summary = "Obtener todos los usuarios", description = "Recupera la lista de todos los usuarios registrados.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de usuarios recuperada con éxito"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Permisos insuficientes")
    })
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> response = getUserUseCase.getAllUsers();
        return ResponseEntity.ok(ApiResponse.ok("Lista de usuarios recuperada con éxito", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'WMS_READ')")
    @Operation(summary = "Obtener usuario por ID", description = "Recupera los detalles de un usuario específico a partir de su UUID.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario encontrado con éxito"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Permisos insuficientes"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        UserResponse response = getUserUseCase.getUserById(id);
        return ResponseEntity.ok(ApiResponse.ok("Usuario encontrado con éxito", response));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente. Requiere permisos de administrador.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario actualizado con éxito"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o duplicados"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Permisos insuficientes"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@Valid @RequestBody UserUpdateRequest request) {
        UserResponse response = updateUserUseCase.updateUser(request);
        return ResponseEntity.ok(ApiResponse.ok("Usuario actualizado con éxito", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    @Operation(summary = "Eliminar usuario", description = "Elimina físicamente a un usuario del sistema por su ID. Requiere permisos de administrador.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario eliminado con éxito"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Permisos insuficientes"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id) {
        deleteUserUseCase.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.ok("Usuario eliminado con éxito"));
    }
}
