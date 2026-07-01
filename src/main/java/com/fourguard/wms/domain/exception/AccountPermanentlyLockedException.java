package com.fourguard.wms.domain.exception;

/**
 * Thrown when a user account is permanently locked after exhausting all
 * allowed login attempts following a temporary lockout.
 * Maps to HTTP 423 (Locked).
 */
public class AccountPermanentlyLockedException extends RuntimeException {

    public AccountPermanentlyLockedException() {
        super("Cuenta bloqueada definitivamente. Contacte al administrador para recuperar el acceso.");
    }
}
