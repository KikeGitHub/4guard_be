package com.fourguard.wms.presentation.advice;

import com.fourguard.wms.shared.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler — centralises all error responses.
 *
 * <p>Every error follows the unified response format:
 * <pre>
 * {
 *   "success"   : false,
 *   "message"   : "Descripción del error",
 *   "data"      : ["field: message", ...],  // only for validation errors
 *   "timestamp" : "2026-01-01T10:00:00"
 * }
 * </pre>
 *
 * <p>Security rules:
 * <ul>
 *   <li>Stack traces are NEVER exposed in responses</li>
 *   <li>Internal error details are logged server-side only</li>
 *   <li>Authentication errors return 401, authorization errors return 403</li>
 * </ul>
 *
 * <p>Domain-specific exceptions ({@code EntityNotFoundException},
 * {@code InvalidCredentialsException}, etc.) will be added in Phase 3.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ── Validation ────────────────────────────────────────────────────────────

    /**
     * Handles @Valid / @Validated constraint violations on request body.
     * Returns 400 with field-level error details.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<String>>> handleValidation(
            MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        log.warn("[VALIDATION] Request validation failed — {} error(s): {}", errors.size(), errors);

        return ResponseEntity.badRequest()
                .body(ApiResponse.<List<String>>builder()
                        .success(false)
                        .message("Error de validación en los datos de entrada")
                        .data(errors)
                        .build());
    }

    /**
     * Handles constraint violations on @PathVariable / @RequestParam.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<List<String>>> handleConstraintViolation(
            ConstraintViolationException ex) {

        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.toList());

        log.warn("[VALIDATION] Constraint violation: {}", errors);

        return ResponseEntity.badRequest()
                .body(ApiResponse.<List<String>>builder()
                        .success(false)
                        .message("Error de validación")
                        .data(errors)
                        .build());
    }

    // ── HTTP / Request ────────────────────────────────────────────────────────

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotReadable(
            HttpMessageNotReadableException ex) {
        log.warn("[REQUEST] Malformed JSON body: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("El cuerpo de la solicitud es inválido o está malformado"));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(
            MissingServletRequestParameterException ex) {
        log.warn("[REQUEST] Missing parameter '{}': {}", ex.getParameterName(), ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Parámetro requerido ausente: " + ex.getParameterName()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        log.warn("[REQUEST] Type mismatch for '{}': {}", ex.getName(), ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Tipo de dato inválido para el parámetro: " + ex.getName()));
    }

    // ── Security ──────────────────────────────────────────────────────────────

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthentication(AuthenticationException ex) {
        log.warn("[SECURITY] Authentication failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Autenticación requerida"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("[SECURITY] Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("No tiene permisos para realizar esta operación"));
    }

    // ── Fallback ──────────────────────────────────────────────────────────────

    /**
     * Catch-all for unhandled exceptions.
     * Logs the full stack trace server-side but returns a safe generic message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        log.error("[ERROR] Unhandled exception [{}]: {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error interno del servidor. Contacte al administrador."));
    }
}
