package com.fourguard.wms.domain.enums;

import java.util.EnumSet;
import java.util.Set;

/**
 * FSM states for a warehouse {@code Location}.
 *
 * <pre>
 * ACTIVE      → BLOCKED, MAINTENANCE, INACTIVE
 * BLOCKED     → ACTIVE
 * MAINTENANCE → ACTIVE
 * INACTIVE    → ACTIVE
 * </pre>
 *
 * All other transitions are invalid and must be rejected with HTTP 422.
 */
public enum LocationStatus {

    /** Fully operational, available for storage. */
    ACTIVE,

    /** Temporarily blocked — requires a reason. */
    BLOCKED,

    /** Under physical maintenance — requires a reason. */
    MAINTENANCE,

    /** Permanently deactivated — only allowed when currentOccupancy = 0. */
    INACTIVE;

    /**
     * Returns the set of states that this state is allowed to transition INTO.
     *
     * @return allowed target states (may be empty)
     */
    public Set<LocationStatus> allowedTransitions() {
        return switch (this) {
            case ACTIVE      -> EnumSet.of(BLOCKED, MAINTENANCE, INACTIVE);
            case BLOCKED     -> EnumSet.of(ACTIVE);
            case MAINTENANCE -> EnumSet.of(ACTIVE);
            case INACTIVE    -> EnumSet.of(ACTIVE);
        };
    }

    /**
     * Validates that a transition from {@code this} state to {@code target} is permitted.
     *
     * @param target desired next state
     * @return {@code true} if the transition is allowed
     */
    public boolean canTransitionTo(LocationStatus target) {
        return allowedTransitions().contains(target);
    }

    /**
     * Returns {@code true} when a reason/motivo is mandatory for the given target state.
     * Specifically: {@code BLOCKED} and {@code MAINTENANCE} always require a reason.
     */
    public static boolean requiresReason(LocationStatus target) {
        return target == BLOCKED || target == MAINTENANCE;
    }
}
