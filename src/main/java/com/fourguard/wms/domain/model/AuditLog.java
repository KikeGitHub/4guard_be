package com.fourguard.wms.domain.model;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AuditLog {
    private UUID logId;
    private UUID organizationId;
    private UUID branchId;
    private UUID userId;
    private String action;
    private String entityType;
    private UUID entityId;
    private Map<String, Object> beforeState;
    private Map<String, Object> afterState;
    private String ipAddress;
    private String userAgent;
    private OffsetDateTime createdAt;
}
