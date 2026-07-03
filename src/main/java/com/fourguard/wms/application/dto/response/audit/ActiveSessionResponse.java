package com.fourguard.wms.application.dto.response.audit;

import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Immutable response DTO representing an active user session.
 */
@Value
@Builder
public class ActiveSessionResponse {
    UUID userId;
    String username;
    String fullName;
    String email;
    UUID organizationId;
    String organizationName;
    UUID branchId;
    String branchName;
    OffsetDateTime lastLoginAt;
    String ipAddress;
    String userAgent;
}
