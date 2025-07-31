package com.shoestore.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

/**
 * Utility class for logging operations and structured logging
 */
@Slf4j
public class LoggingUtil {

    // MDC keys for structured logging
    public static final String USER_ID_KEY = "userId";
    public static final String SESSION_ID_KEY = "sessionId";
    public static final String REQUEST_ID_KEY = "requestId";
    public static final String OPERATION_KEY = "operation";
    public static final String ENTITY_TYPE_KEY = "entityType";
    public static final String ENTITY_ID_KEY = "entityId";

    /**
     * Set user context for logging
     */
    public static void setUserContext(Long userId, String sessionId) {
        if (userId != null) {
            MDC.put(USER_ID_KEY, userId.toString());
        }
        if (sessionId != null) {
            MDC.put(SESSION_ID_KEY, sessionId);
        }
    }

    /**
     * Set request context for logging
     */
    public static void setRequestContext(String requestId) {
        if (requestId != null) {
            MDC.put(REQUEST_ID_KEY, requestId);
        }
    }

    /**
     * Set operation context for logging
     */
    public static void setOperationContext(String operation, String entityType, String entityId) {
        if (operation != null) {
            MDC.put(OPERATION_KEY, operation);
        }
        if (entityType != null) {
            MDC.put(ENTITY_TYPE_KEY, entityType);
        }
        if (entityId != null) {
            MDC.put(ENTITY_ID_KEY, entityId);
        }
    }

    /**
     * Clear all MDC context
     */
    public static void clearContext() {
        MDC.clear();
    }

    /**
     * Clear specific MDC keys
     */
    public static void clearUserContext() {
        MDC.remove(USER_ID_KEY);
        MDC.remove(SESSION_ID_KEY);
    }

    /**
     * Log user action with context
     */
    public static void logUserAction(Long userId, String action, String details) {
        setUserContext(userId, null);
        setOperationContext(action, null, null);
        log.info("User action: {} - {}", action, details);
        clearContext();
    }

    /**
     * Log entity operation with context
     */
    public static void logEntityOperation(String operation, String entityType, String entityId, String details) {
        setOperationContext(operation, entityType, entityId);
        log.info("Entity operation: {} on {} [{}] - {}", operation, entityType, entityId, details);
        clearContext();
    }

    /**
     * Log performance metrics
     */
    public static void logPerformance(String operation, long executionTimeMs) {
        setOperationContext(operation, null, null);
        if (executionTimeMs > 1000) {
            log.warn("Slow operation: {} took {}ms", operation, executionTimeMs);
        } else {
            log.debug("Operation: {} completed in {}ms", operation, executionTimeMs);
        }
        clearContext();
    }

    /**
     * Log security events
     */
    public static void logSecurityEvent(String event, String username, String details) {
        MDC.put("username", username);
        MDC.put("securityEvent", event);
        log.warn("Security event: {} for user {} - {}", event, username, details);
        MDC.remove("username");
        MDC.remove("securityEvent");
    }

    /**
     * Log business events
     */
    public static void logBusinessEvent(String event, String details) {
        MDC.put("businessEvent", event);
        log.info("Business event: {} - {}", event, details);
        MDC.remove("businessEvent");
    }

    /**
     * Log API request/response
     */
    public static void logApiCall(String method, String endpoint, int statusCode, long responseTimeMs) {
        MDC.put("httpMethod", method);
        MDC.put("endpoint", endpoint);
        MDC.put("statusCode", String.valueOf(statusCode));
        MDC.put("responseTime", String.valueOf(responseTimeMs));

        if (statusCode >= 400) {
            log.warn("API call: {} {} returned {} in {}ms", method, endpoint, statusCode, responseTimeMs);
        } else if (responseTimeMs > 2000) {
            log.warn("Slow API call: {} {} took {}ms", method, endpoint, responseTimeMs);
        } else {
            log.info("API call: {} {} returned {} in {}ms", method, endpoint, statusCode, responseTimeMs);
        }

        MDC.remove("httpMethod");
        MDC.remove("endpoint");
        MDC.remove("statusCode");
        MDC.remove("responseTime");
    }

    /**
     * Log database operations
     */
    public static void logDatabaseOperation(String operation, String table, long recordCount, long executionTimeMs) {
        MDC.put("dbOperation", operation);
        MDC.put("table", table);
        MDC.put("recordCount", String.valueOf(recordCount));

        if (executionTimeMs > 5000) {
            log.warn("Slow DB operation: {} on {} ({} records) took {}ms",
                    operation, table, recordCount, executionTimeMs);
        } else {
            log.debug("DB operation: {} on {} ({} records) completed in {}ms",
                    operation, table, recordCount, executionTimeMs);
        }

        MDC.remove("dbOperation");
        MDC.remove("table");
        MDC.remove("recordCount");
    }

    /**
     * Measure and log execution time
     */
    public static void measureAndLog(String operation, Runnable task) {
        long startTime = System.currentTimeMillis();
        try {
            task.run();
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            logPerformance(operation, executionTime);
        }
    }

    /**
     * Helper method to sanitize sensitive data in logs
     */
    public static String sanitize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // Mask email addresses
        if (input.contains("@")) {
            String[] parts = input.split("@");
            if (parts.length == 2) {
                String username = parts[0];
                String domain = parts[1];
                String maskedUsername = username.length() > 2 ?
                        username.substring(0, 2) + "***" : "***";
                return maskedUsername + "@" + domain;
            }
        }

        // Mask other sensitive data (assuming length > 4)
        if (input.length() > 4) {
            return input.substring(0, 2) + "***" + input.substring(input.length() - 2);
        }

        return "***";
    }
}