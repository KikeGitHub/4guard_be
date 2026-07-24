package com.fourguard.wms.domain.ports.in;

import java.util.UUID;

/** Port of entry for an administrator to reset a user's password to a temporary one. */
public interface ResetUserPasswordUseCase {
    /**
     * Genera una contraseña temporal para el usuario identificado por su username o email.
     *
     * @param usernameOrEmail nombre de usuario o correo electrónico del usuario objetivo
     * @param adminUsername   nombre de usuario del administrador que ejecuta la acción
     * @return la contraseña temporal generada (en texto plano, para ser comunicada al usuario)
     */
    String resetToTemporaryPassword(String usernameOrEmail, String adminUsername);

    /**
     * Genera una contraseña temporal para el usuario identificado por su ID (UUID).
     *
     * @param userId        UUID del usuario objetivo
     * @param adminUsername nombre de usuario del administrador que ejecuta la acción
     * @return la contraseña temporal generada
     */
    String resetToTemporaryPasswordById(UUID userId, String adminUsername);
}
