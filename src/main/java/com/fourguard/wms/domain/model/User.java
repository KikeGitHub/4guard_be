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
    private OffsetDateTime lastLogin;
    private Long version;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
