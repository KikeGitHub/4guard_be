package com.fourguard.wms.domain.exception;

/**
 * Exception thrown when business validation fails.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
