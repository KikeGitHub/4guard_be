package com.fourguard.wms.infrastructure.persistence.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

/**
 * JPA entity for {@code wms.notifications}.
 * Stores in-app notifications sent to users (e.g. account lockout alerts).
 */
@Entity
@Table(name = "notifications", schema = "wms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(name = "organization_id", nullable = false, columnDefinition = "UUID")
    private UUID organizationId;

    /** Target user — may be {@code null} if the notification is for all admins. */
    @Column(name = "recipient_id", columnDefinition = "UUID")
    private UUID recipientId;

    /** Notification category: {@code ACCOUNT_TEMP_LOCKED}, {@code ACCOUNT_PERM_LOCKED}. */
    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onPrePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now(ZoneOffset.UTC);
        }
    }
}
