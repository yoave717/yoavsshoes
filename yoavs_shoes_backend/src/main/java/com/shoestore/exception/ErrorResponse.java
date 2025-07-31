package com.shoestore.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standardized error response format for all API errors
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /**
     * Timestamp when the error occurred
     */
    private LocalDateTime timestamp;

    /**
     * HTTP status code
     */
    private int status;

    /**
     * Error type/category
     */
    private String error;

    /**
     * Human-readable error message
     */
    private String message;

    /**
     * Request path where error occurred
     */
    private String path;

    /**
     * Additional error details (optional)
     */
    private Map<String, Object> details;

    /**
     * Request ID for tracing (optional)
     */
    private String requestId;

    /**
     * Stack trace (only for development, not included in production)
     */
    private String trace;
}