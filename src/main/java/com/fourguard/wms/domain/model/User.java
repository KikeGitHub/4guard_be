package com.fourguard.wms.domain.model;

import com.fourguard.wms.domain.enums.UserStatus;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private UUID id;
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Organization organization;
    private Branch branch;
    private Role role;
    private UserStatus status;
    private Boolean isEnabled;
    private Boolean changePasswordRequired;
    private OffsetDateTime lastLogin;

    // ── Login lockout ──────────────────────────────────────────────────────────
    /** Number of consecutive failed login attempts in the current window. */
    private Integer failedAttempts;
    /** Timestamp until which the account is temporarily locked. NULL = not locked. */
    private OffsetDateTime lockedUntil;
    /** When TRUE the account is permanently locked; requires admin intervention. */
    private Boolean permanentlyLocked;

    private Long version;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
