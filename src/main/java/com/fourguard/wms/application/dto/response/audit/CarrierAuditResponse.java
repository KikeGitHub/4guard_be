package com.fourguard.wms.application.dto.response.audit;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/** Response DTO representing an audit log entry for a carrier. */
@Getter
@Builder
public class CarrierAuditResponse {
    private final UUID logId;
    private final String action;
    private final String username;
    private final OffsetDateTime createdAt;
    private final Map<String, Object> beforeState;
    private final Map<String, Object> afterState;
}
