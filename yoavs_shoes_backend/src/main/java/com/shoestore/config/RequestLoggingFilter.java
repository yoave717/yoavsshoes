package com.shoestore.config;

import com.shoestore.util.LoggingUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter to log HTTP requests and responses with performance metrics
 */
@Component
@Order(1)
@Slf4j
public class RequestLoggingFilter implements Filter {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String USER_AGENT_HEADER = "User-Agent";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Generate or extract request ID
        String requestId = getOrGenerateRequestId(httpRequest);

        // Set request context
        LoggingUtil.setRequestContext(requestId);
        MDC.put("requestMethod", httpRequest.getMethod());
        MDC.put("requestUri", httpRequest.getRequestURI());
        MDC.put("remoteAddr", getClientIpAddress(httpRequest));

        // Add request ID to response headers
        httpResponse.setHeader(REQUEST_ID_HEADER, requestId);

        long startTime = System.currentTimeMillis();

        try {
            // Log incoming request
            logIncomingRequest(httpRequest);

            // Continue with the filter chain
            chain.doFilter(request, response);

        } finally {
            // Calculate response time
            long responseTime = System.currentTimeMillis() - startTime;

            // Log outgoing response
            logOutgoingResponse(httpRequest, httpResponse, responseTime);

            // Clear MDC context
            LoggingUtil.clearContext();
        }
    }

    /**
     * Log incoming HTTP request
     */
    private void logIncomingRequest(HttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String userAgent = request.getHeader(USER_AGENT_HEADER);

        StringBuilder logMessage = new StringBuilder();
        logMessage.append("Incoming request: ").append(method).append(" ").append(uri);

        if (queryString != null && !queryString.isEmpty()) {
            logMessage.append("?").append(queryString);
        }

        if (userAgent != null) {
            logMessage.append(" | User-Agent: ").append(userAgent);
        }

        // Skip logging for health checks and static resources
        if (shouldSkipLogging(uri)) {
            log.debug(logMessage.toString());
        } else {
            log.info(logMessage.toString());
        }
    }

    /**
     * Log outgoing HTTP response
     */
    private void logOutgoingResponse(HttpServletRequest request, HttpServletResponse response, long responseTime) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        int status = response.getStatus();

        // Log API call using utility
        LoggingUtil.logApiCall(method, uri, status, responseTime);

        // Additional logging for errors
        if (status >= 400) {
            log.warn("Request failed: {} {} returned status {} in {}ms",
                    method, uri, status, responseTime);
        }

        // Log slow requests
        if (responseTime > 2000 && !shouldSkipLogging(uri)) {
            log.warn("Slow request detected: {} {} took {}ms", method, uri, responseTime);
        }
    }

    /**
     * Get or generate request ID
     */
    private String getOrGenerateRequestId(HttpServletRequest request) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.trim().isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }
        return requestId;
    }

    /**
     * Get client IP address, considering proxy headers
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String headerName : headerNames) {
            String value = request.getHeader(headerName);
            if (value != null && !value.isEmpty() && !"unknown".equalsIgnoreCase(value)) {
                return value.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }

    /**
     * Determine if request should skip detailed logging
     */
    private boolean shouldSkipLogging(String uri) {
        return uri.startsWith("/actuator/") ||
                uri.startsWith("/swagger-ui/") ||
                uri.startsWith("/v3/api-docs/") ||
                uri.startsWith("/uploads/") ||
                uri.startsWith("/images/") ||
                uri.endsWith(".css") ||
                uri.endsWith(".js") ||
                uri.endsWith(".ico") ||
                uri.endsWith(".png") ||
                uri.endsWith(".jpg") ||
                uri.endsWith(".jpeg");
    }
}