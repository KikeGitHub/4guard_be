package com.fourguard.wms.shared.notification;

import com.fourguard.wms.domain.model.Notification;
import com.fourguard.wms.domain.ports.out.NotificationRepositoryPort;
import com.fourguard.wms.domain.ports.out.UserAdminQueryPort;
import com.fourguard.wms.infrastructure.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * Shared service responsible for creating and dispatching in-app notifications.
 *
 * <p>Triggered by {@code LoginLockService} when a lockout event occurs.
 * Each event generates two notifications: one for the affected user and one
 * for the primary administrator of the same organisation.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepositoryPort notificationRepositoryPort;
    private final UserAdminQueryPort         userAdminQueryPort;

    // ─── Public API ───────────────────────────────────────────────────────────

    /**
     * Sends a temporary-lockout notification to both the locked user and the org admin.
     *
     * @param user the user whose account was temporarily locked
     */
    public void notifyTemporaryLock(UserEntity user) {
        String userMsg = "Tu cuenta ha sido bloqueada temporalmente por 15 minutos " +
                         "debido a múltiples intentos fallidos de inicio de sesión.";
        String adminMsg = "La cuenta del usuario '" + user.getUsername() +
                          "' ha sido bloqueada temporalmente por 15 minutos " +
                          "tras 3 intentos fallidos de inicio de sesión.";

        createNotification(
                user.getOrganization().getId(), user.getId(),
                "ACCOUNT_TEMP_LOCKED",
                "Cuenta bloqueada temporalmente",
                userMsg,
                Map.of("username", user.getUsername(), "lockType", "TEMPORARY"));

        notifyAdmin(user, "ACCOUNT_TEMP_LOCKED",
                "⚠️ Alerta: Cuenta bloqueada temporalmente", adminMsg);

        log.warn("[LOCKOUT] Temporary lock notification sent for user: {}", user.getUsername());
    }

    /**
     * Sends a permanent-lockout notification to both the locked user and the org admin.
     *
     * @param user the user whose account was permanently locked
     */
    public void notifyPermanentLock(UserEntity user) {
        String userMsg = "Tu cuenta ha sido bloqueada definitivamente tras superar el " +
                         "número máximo de intentos fallidos. " +
                         "Contacta al administrador para recuperar el acceso.";
        String adminMsg = "La cuenta del usuario '" + user.getUsername() +
                          "' ha sido bloqueada definitivamente. " +
                          "Se requiere intervención del administrador para desbloquearla.";

        createNotification(
                user.getOrganization().getId(), user.getId(),
                "ACCOUNT_PERM_LOCKED",
                "Cuenta bloqueada definitivamente",
                userMsg,
                Map.of("username", user.getUsername(), "lockType", "PERMANENT"));

        notifyAdmin(user, "ACCOUNT_PERM_LOCKED",
                "🔒 Alerta crítica: Cuenta bloqueada definitivamente", adminMsg);

        log.warn("[LOCKOUT] Permanent lock notification sent for user: {}", user.getUsername());
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private void notifyAdmin(UserEntity lockedUser, String type, String title, String message) {
        userAdminQueryPort.findTopAdminByOrganizationId(lockedUser.getOrganization().getId())
                .ifPresentOrElse(
                        admin -> {
                            // Avoid duplicating the notification if the locked user IS the admin
                            if (!admin.getId().equals(lockedUser.getId())) {
                                createNotification(
                                        lockedUser.getOrganization().getId(), admin.getId(),
                                        type, title, message,
                                        Map.of("lockedUser", lockedUser.getUsername()));
                            }
                        },
                        () -> log.warn("[LOCKOUT] No active admin found for org: {}",
                                       lockedUser.getOrganization().getId())
                );
    }

    private void createNotification(UUID orgId, UUID recipientId, String type,
                                    String title, String message, Map<String, Object> metadata) {
        Notification notification = Notification.builder()
                .organizationId(orgId)
                .recipientId(recipientId)
                .type(type)
                .title(title)
                .message(message)
                .isRead(false)
                .metadata(metadata)
                .build();
        notificationRepositoryPort.save(notification);
    }
}
