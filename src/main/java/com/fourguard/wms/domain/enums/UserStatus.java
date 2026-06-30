package com.fourguard.wms.domain.enums;

/** Lifecycle status of a User account. */
public enum UserStatus {
    /** Account is active and can log in. */
    ACTIVE,
    /** Account is inactive (soft-disabled). */
    INACTIVE,
    /** Account created but not yet activated. */
    PENDING,
    /** Account temporarily suspended. */
    SUSPENDED
}
