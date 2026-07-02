package com.fourguard.wms.domain.model;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing an in-app notification.
 * Persisted to {@code wms.notifications} via the infrastructure layer.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Notification {

    private UUID id;

    /** Organisation the notification belongs to. */
    private UUID organizationId;

    /** Target user. When set, only that user sees this notification. */
    private UUID recipientId;

    /**
     * Notification category.
     * Values: {@code ACCOUNT_TEMP_LOCKED}, {@code ACCOUNT_PERM_LOCKED}.
     */
    private String type;

    private String title;
    private String message;

    /** Whether the recipient has acknowledged this notification. */
    private Boolean isRead;

    /** Arbitrary key-value context (e.g. username, IP address). */
    private Map<String, Object> metadata;

    private OffsetDateTime createdAt;
}
