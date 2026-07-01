package com.fourguard.wms.domain.ports.in;

import com.fourguard.wms.application.dto.response.notification.NotificationResponse;

import java.util.List;
import java.util.UUID;

/**
 * Port IN — Use-case contract for in-app notifications.
 * Implemented by {@code NotificationUseCaseImpl} in the application layer.
 */
public interface NotificationUseCase {

    /**
     * Returns notifications addressed to the currently authenticated user.
     *
     * @param username   the authenticated user's username (from JWT principal)
     * @param unreadOnly when {@code true}, only returns unread notifications
     */
    List<NotificationResponse> getMyNotifications(String username, boolean unreadOnly);

    /**
     * Marks a specific notification as read. Only the owning recipient may perform this action.
     *
     * @param notificationId the notification to acknowledge
     * @param username       the authenticated user's username (ownership guard)
     */
    void markAsRead(UUID notificationId, String username);
}
