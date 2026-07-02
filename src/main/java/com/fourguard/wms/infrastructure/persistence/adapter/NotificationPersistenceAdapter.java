package com.fourguard.wms.infrastructure.persistence.adapter;

import com.fourguard.wms.domain.model.Notification;
import com.fourguard.wms.domain.ports.out.NotificationRepositoryPort;
import com.fourguard.wms.infrastructure.persistence.entity.NotificationEntity;
import com.fourguard.wms.infrastructure.persistence.repository.NotificationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Infrastructure adapter — implements {@link NotificationRepositoryPort} using JPA.
 */
@Component
@RequiredArgsConstructor
public class NotificationPersistenceAdapter implements NotificationRepositoryPort {

    private final NotificationJpaRepository repository;

    @Override
    public Notification save(Notification notification) {
        NotificationEntity entity = toEntity(notification);
        NotificationEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<Notification> findByRecipientId(UUID recipientId, boolean unreadOnly) {
        List<NotificationEntity> entities = unreadOnly
                ? repository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(recipientId)
                : repository.findByRecipientIdOrderByCreatedAtDesc(recipientId);
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(UUID notificationId, UUID recipientId) {
        repository.markAsRead(notificationId, recipientId);
    }

    // ── Mapping helpers ────────────────────────────────────────────────────────

    private NotificationEntity toEntity(Notification n) {
        return NotificationEntity.builder()
                .organizationId(n.getOrganizationId())
                .recipientId(n.getRecipientId())
                .type(n.getType())
                .title(n.getTitle())
                .message(n.getMessage())
                .isRead(n.getIsRead() != null ? n.getIsRead() : false)
                .metadata(n.getMetadata())
                .build();
    }

    private Notification toDomain(NotificationEntity e) {
        return Notification.builder()
                .id(e.getId())
                .organizationId(e.getOrganizationId())
                .recipientId(e.getRecipientId())
                .type(e.getType())
                .title(e.getTitle())
                .message(e.getMessage())
                .isRead(e.getIsRead())
                .metadata(e.getMetadata())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
