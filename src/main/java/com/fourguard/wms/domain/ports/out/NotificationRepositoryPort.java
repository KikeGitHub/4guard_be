package com.fourguard.wms.domain.ports.out;

import com.fourguard.wms.domain.model.Notification;

import java.util.List;
import java.util.UUID;

/**
 * Port OUT — Notification persistence contract.
 * Implemented by {@code NotificationPersistenceAdapter} in the infrastructure layer.
 */
public interface NotificationRepositoryPort {

    /** Persists a new notification and returns it with the generated id. */
    Notification save(Notification notification);

    /**
     * Returns all notifications for the given recipient, ordered by creation date (newest first).
     *
     * @param recipientId the target user's UUID
     * @param unreadOnly  when {@code true}, only returns unread notifications
     */
    List<Notification> findByRecipientId(UUID recipientId, boolean unreadOnly);

    /**
     * Marks a specific notification as read, scoped to the owning recipient.
     *
     * @param notificationId the notification to acknowledge
     * @param recipientId    must match the notification's recipient (ownership guard)
     */
    void markAsRead(UUID notificationId, UUID recipientId);
}
