package com.shoestore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller for API status monitoring
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Health", description = "API health check endpoints")
public class HealthController {

    /**
     * Simple health check endpoint
     */
    @Operation(
            summary = "Health check",
            description = "Returns API health status and basic information"
    )
    @ApiResponse(
            responseCode = "200",
            description = "API is healthy",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Health status",
                            value = """
                    {
                      "status": "UP",
                      "timestamp": "2024-01-01T12:00:00",
                      "message": "Shoe Store API is running",
                      "version": "1.0.0"
                    }
                    """
                    )
            )
    )
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("message", "Shoe Store API is running");
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }

    /**
     * API version endpoint
     */
    @Operation(
            summary = "Get API version",
            description = "Returns current API version information"
    )
    @GetMapping("/version")
    public ResponseEntity<Map<String, Object>> version() {
        Map<String, Object> response = new HashMap<>();
        response.put("version", "1.0.0");
        response.put("name", "Shoe Store E-commerce API");
        response.put("description", "REST API for shoe store application");
        response.put("buildTime", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
}