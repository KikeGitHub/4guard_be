package com.fourguard.wms.application.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordRequest {

    @NotBlank(message = "La nueva contraseña es requerida")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres de longitud")
    private String newPassword;
}
