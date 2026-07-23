package com.fourguard.wms.domain.exception;

/**
 * Thrown when an FSM state transition is not permitted by the business rules.
 * Maps to HTTP 422 Unprocessable Entity.
 */
public class InvalidFsmTransitionException extends RuntimeException {
    public InvalidFsmTransitionException(String message) {
        super(message);
    }
}
