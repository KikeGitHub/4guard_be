package com.fourguard.wms.shared.audit;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Helper centralizado para obtener el usuario autenticado del SecurityContext.
 *
 * <p>El JwtAuthenticationFilter ya inyecta el Authentication en el contexto
 * en cada request, por lo que siempre está disponible en cualquier Service.</p>
 */
@Component
public class SecurityAuditHelper {

    private static final String FALLBACK_USER = "SYSTEM";

    /**
     * Retorna el username del usuario actualmente autenticado.
     * Si por algún motivo no hay autenticación activa (e.g. jobs internos), retorna "SYSTEM".
     *
     * @return username del usuario logueado, o "SYSTEM" como fallback
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {
            return authentication.getName();
        }
        return FALLBACK_USER;
    }
}
