package com.fourguard.wms.domain.exception;

/**
 * Thrown when a business operation conflicts with the current state
 * (e.g. duplicate unique code, location with active inventory).
 * Maps to HTTP 409 Conflict.
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
