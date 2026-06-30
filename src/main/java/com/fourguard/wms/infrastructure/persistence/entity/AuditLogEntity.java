package com.fourguard.wms.infrastructure.persistence.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

/**
 * Audit Log JPA entity — WORM (Write Once, Read Many).
 *
 * <p>A PostgreSQL trigger ({@code trg_audit_logs_worm}) prevents any UPDATE or DELETE
 * on this table. The {@code @Immutable} annotation instructs Hibernate to never
 * attempt flushing changes to this entity, making it safe and consistent.</p>
 *
 * <p>Foreign key references (organization, branch, user) are stored as raw UUID
 * columns intentionally — loading full entity graphs on audit reads would be
 * unnecessarily expensive and is not required for audit purposes.</p>
 */
@Entity
@Immutable
@Table(name = "audit_logs", schema = "wms")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "log_id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID logId;

    @Column(name = "organization_id", nullable = false, columnDefinition = "UUID")
    private UUID organizationId;

    @Column(name = "branch_id", columnDefinition = "UUID")
    private UUID branchId;

    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    private UUID userId;

    @Column(nullable = false, length = 100)
    private String action;

    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;

    @Column(name = "entity_id", nullable = false, columnDefinition = "UUID")
    private UUID entityId;

    @Type(JsonType.class)
    @Column(name = "before_state", columnDefinition = "jsonb")
    private Map<String, Object> beforeState;

    @Type(JsonType.class)
    @Column(name = "after_state", columnDefinition = "jsonb")
    private Map<String, Object> afterState;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onPrePersist() {
        if (createdAt == null) createdAt = OffsetDateTime.now(ZoneOffset.UTC);
    }
}
