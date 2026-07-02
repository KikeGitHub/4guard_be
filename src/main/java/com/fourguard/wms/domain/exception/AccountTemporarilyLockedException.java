package com.fourguard.wms.domain.exception;

/**
 * Thrown when a user account is temporarily locked due to repeated failed login attempts.
 * Maps to HTTP 423 (Locked).
 */
public class AccountTemporarilyLockedException extends RuntimeException {

    private final long minutesRemaining;

    public AccountTemporarilyLockedException(long minutesRemaining) {
        super("Cuenta bloqueada temporalmente. Intente de nuevo en " + minutesRemaining + " minuto(s).");
        this.minutesRemaining = minutesRemaining;
    }

    public long getMinutesRemaining() {
        return minutesRemaining;
    }
}
