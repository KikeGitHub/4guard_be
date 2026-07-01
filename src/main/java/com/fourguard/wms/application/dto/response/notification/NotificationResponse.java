package com.fourguard.wms.application.dto.response.notification;

import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Read-only response DTO for an in-app notification.
 */
@Value
@Builder
public class NotificationResponse {
    UUID id;
    String type;
    String title;
    String message;
    Boolean isRead;
    Map<String, Object> metadata;
    OffsetDateTime createdAt;
}
