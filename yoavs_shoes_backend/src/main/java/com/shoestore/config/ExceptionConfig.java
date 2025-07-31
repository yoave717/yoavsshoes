package com.shoestore.config;

import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Configuration for custom error handling
 */
@Configuration
public class ExceptionConfig {

    /**
     * Custom error attributes for Spring Boot's default error handling
     */
    @Bean
    public ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes() {
            @Override
            public Map<String, Object> getErrorAttributes(WebRequest webRequest,
                                                          org.springframework.boot.web.error.ErrorAttributeOptions options) {

                Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);

                // Add custom timestamp format
                errorAttributes.put("timestamp", LocalDateTime.now());

                // Remove unnecessary fields for production
                errorAttributes.remove("trace");
                errorAttributes.remove("exception");

                // Add request ID if available
                String requestId = webRequest.getHeader("X-Request-ID");
                if (requestId != null) {
                    errorAttributes.put("requestId", requestId);
                }

                return errorAttributes;
            }
        };
    }
}