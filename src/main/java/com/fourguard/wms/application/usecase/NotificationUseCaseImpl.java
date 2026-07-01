package com.fourguard.wms.application.usecase;

import com.fourguard.wms.application.dto.response.notification.NotificationResponse;
import com.fourguard.wms.domain.exception.EntityNotFoundException;
import com.fourguard.wms.domain.model.Notification;
import com.fourguard.wms.domain.ports.in.NotificationUseCase;
import com.fourguard.wms.domain.ports.out.NotificationRepositoryPort;
import com.fourguard.wms.domain.ports.out.UserRepositoryPort;
import com.fourguard.wms.infrastructure.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Use-case implementation for in-app notifications.
 * Lets authenticated users list and acknowledge their own notifications.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationUseCaseImpl implements NotificationUseCase {

    private final NotificationRepositoryPort notificationRepositoryPort;
    private final UserRepositoryPort         userRepositoryPort;

    @Override
    public List<NotificationResponse> getMyNotifications(String username, boolean unreadOnly) {
        UserEntity user = resolveUser(username);
        List<Notification> notifications =
                notificationRepositoryPort.findByRecipientId(user.getId(), unreadOnly);

        log.debug("[NOTIFICATION] Returning {} notification(s) for user: {}",
                  notifications.size(), username);

        return notifications.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(UUID notificationId, String username) {
        UserEntity user = resolveUser(username);
        notificationRepositoryPort.markAsRead(notificationId, user.getId());
        log.debug("[NOTIFICATION] Notification {} marked as read by user: {}",
                  notificationId, username);
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private UserEntity resolveUser(String username) {
        return userRepositoryPort.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuario no encontrado: " + username));
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType())
                .title(n.getTitle())
                .message(n.getMessage())
                .isRead(n.getIsRead())
                .metadata(n.getMetadata())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
