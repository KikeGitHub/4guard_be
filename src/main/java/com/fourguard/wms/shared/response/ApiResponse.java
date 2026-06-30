package com.fourguard.wms.shared.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Unified API response wrapper for all endpoints.
 *
 * <p>Every response from the 4GUARD WMS API follows this structure:
 * <pre>
 * {
 *   "success"  : true | false,
 *   "message"  : "Human-readable description",
 *   "data"     : { ... },        // null is omitted
 *   "timestamp": "2026-01-01T10:00:00"
 * }
 * </pre>
 *
 * <p>Use the static factory methods for consistency:
 * <ul>
 *   <li>{@link #ok(String, Object)} — 2xx success with data</li>
 *   <li>{@link #ok(String)} — 2xx success without data (e.g., DELETE)</li>
 *   <li>{@link #error(String)} — 4xx/5xx failure</li>
 * </ul>
 *
 * @param <T> the type of the {@code data} payload
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /** Indicates whether the operation completed successfully. */
    private final boolean success;

    /** Human-readable message describing the result. */
    private final String message;

    /** Response payload — omitted from JSON when null. */
    private final T data;

    /** Timestamp of the response. */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime timestamp;

    // ─── Factory methods ──────────────────────────────────────────────────────

    /**
     * Creates a successful response with a data payload.
     *
     * @param message descriptive message
     * @param data    response payload
     * @param <T>     payload type
     * @return {@code ApiResponse} with {@code success=true}
     */
    public static <T> ApiResponse<T> ok(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a successful response without a data payload (e.g., for DELETE).
     *
     * @param message descriptive message
     * @param <T>     unused payload type
     * @return {@code ApiResponse} with {@code success=true}
     */
    public static <T> ApiResponse<T> ok(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a failure response.
     *
     * @param message error description
     * @return {@code ApiResponse} with {@code success=false}
     */
    public static ApiResponse<Void> error(String message) {
        return ApiResponse.<Void>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
