package com.fourguard.wms.application.dto;

import com.fourguard.wms.domain.enums.UserStatus;
import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.UUID;

@Value
@Builder
public class UserResponse {
    UUID id;
    String username;
    String email;
    String firstName;
    String lastName;
    UUID organizationId;
    String organizationName; // Added for convenience
    UUID branchId;
    String branchName;       // Added for convenience
    UUID roleId;
    String roleName;         // Added for convenience
    UserStatus status;
    Boolean isEnabled;
    OffsetDateTime lastLogin;
    OffsetDateTime createdAt;
    String createdBy;
    OffsetDateTime updatedAt;
    String updatedBy;
}