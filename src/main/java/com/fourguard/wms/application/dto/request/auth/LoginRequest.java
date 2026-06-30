package com.fourguard.wms.application.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "El identificador de usuario (username o email) es requerido")
    private String identifier;

    @NotBlank(message = "La contraseña es requerida")
    private String password;
}
