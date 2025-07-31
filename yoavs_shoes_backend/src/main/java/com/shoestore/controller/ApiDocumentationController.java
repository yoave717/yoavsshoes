package com.shoestore.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller providing API documentation and examples
 */
@RestController
@RequestMapping("/api/docs")
@Tag(name = "Public", description = "Public API information and documentation")
public class ApiDocumentationController {

    /**
     * Get API information and available endpoints
     */
    @Operation(
            summary = "Get API information",
            description = "Returns basic information about the Shoe Store API, including version, available endpoints, and getting started guide."
    )
    @ApiResponse(
            responseCode = "200",
            description = "API information retrieved successfully",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "API information",
                            value = """
                    {
                      "apiName": "Shoe Store E-commerce API",
                      "version": "1.0.0",
                      "description": "REST API for shoe store e-commerce platform",
                      "baseUrl": "http://localhost:8080/api",
                      "documentationUrl": "http://localhost:8080/swagger-ui.html",
                      "endpoints": {
                        "authentication": "/api/auth",
                        "products": "/api/products",
                        "users": "/api/users",
                        "orders": "/api/orders",
                        "cart": "/api/cart",
                        "admin": "/api/admin"
                      },
                      "testAccounts": {
                        "admin": {
                          "email": "admin@shoestore.com",
                          "password": "admin123"
                        },
                        "user": {
                          "email": "user@example.com",
                          "password": "user123"
                        }
                      }
                    }
                    """
                    )
            )
    )
    @GetMapping(value = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> apiInfo = new HashMap<>();
        apiInfo.put("apiName", "Shoe Store E-commerce API");
        apiInfo.put("version", "1.0.0");
        apiInfo.put("description", "REST API for shoe store e-commerce platform");
        apiInfo.put("baseUrl", "http://localhost:8080/api");
        apiInfo.put("documentationUrl", "http://localhost:8080/swagger-ui.html");

        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("authentication", "/api/auth");
        endpoints.put("products", "/api/products");
        endpoints.put("users", "/api/users");
        endpoints.put("orders", "/api/orders");
        endpoints.put("cart", "/api/cart");
        endpoints.put("admin", "/api/admin");
        apiInfo.put("endpoints", endpoints);

        Map<String, Map<String, String>> testAccounts = new HashMap<>();
        Map<String, String> adminAccount = new HashMap<>();
        adminAccount.put("email", "admin@shoestore.com");
        adminAccount.put("password", "admin123");
        testAccounts.put("admin", adminAccount);

        Map<String, String> userAccount = new HashMap<>();
        userAccount.put("email", "user@example.com");
        userAccount.put("password", "user123");
        testAccounts.put("user", userAccount);
        apiInfo.put("testAccounts", testAccounts);

        return ResponseEntity.ok(apiInfo);
    }

    /**
     * Get getting started guide
     */
    @Operation(
            summary = "Get getting started guide",
            description = "Returns a step-by-step guide for getting started with the Shoe Store API."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Getting started guide retrieved successfully",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Getting started guide",
                            value = """
                    {
                      "title": "Getting Started with Shoe Store API",
                      "steps": [
                        {
                          "step": 1,
                          "title": "Authentication",
                          "description": "Login or register to get a JWT token",
                          "endpoint": "POST /api/auth/login",
                          "example": {
                            "email": "user@example.com",
                            "password": "user123"
                          }
                        },
                        {
                          "step": 2,
                          "title": "Browse Products",
                          "description": "Browse available shoe products",
                          "endpoint": "GET /api/products",
                          "authRequired": false
                        },
                        {
                          "step": 3,
                          "title": "Add to Cart",
                          "description": "Add products to your shopping cart",
                          "endpoint": "POST /api/cart/items",
                          "authRequired": true
                        },
                        {
                          "step": 4,
                          "title": "Place Order",
                          "description": "Place an order with items from your cart",
                          "endpoint": "POST /api/orders",
                          "authRequired": true
                        }
                      ]
                    }
                    """
                    )
            )
    )
    @GetMapping(value = "/getting-started", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getGettingStartedGuide() {
        Map<String, Object> guide = new HashMap<>();
        guide.put("title", "Getting Started with Shoe Store API");

        List<Map<String, Object>> steps = List.of(
                Map.of(
                        "step", 1,
                        "title", "Authentication",
                        "description", "Login or register to get a JWT token",
                        "endpoint", "POST /api/auth/login",
                        "example", Map.of(
                                "email", "user@example.com",
                                "password", "user123"
                        )
                ),
                Map.of(
                        "step", 2,
                        "title", "Browse Products",
                        "description", "Browse available shoe products",
                        "endpoint", "GET /api/products",
                        "authRequired", false
                ),
                Map.of(
                        "step", 3,
                        "title", "Add to Cart",
                        "description", "Add products to your shopping cart",
                        "endpoint", "POST /api/cart/items",
                        "authRequired", true
                ),
                Map.of(
                        "step", 4,
                        "title", "Place Order",
                        "description", "Place an order with items from your cart",
                        "endpoint", "POST /api/orders",
                        "authRequired", true
                )
        );

        guide.put("steps", steps);
        return ResponseEntity.ok(guide);
    }

    /**
     * Get error codes and descriptions
     */
    @Operation(
            summary = "Get error codes",
            description = "Returns information about API error codes and their meanings."
    )
    @GetMapping(value = "/error-codes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getErrorCodes() {
        Map<String, Object> errorInfo = new HashMap<>();
        errorInfo.put("title", "API Error Codes");

        Map<String, Map<String, String>> errorCodes = new HashMap<>();

        errorCodes.put("400", Map.of(
                "name", "Bad Request",
                "description", "Invalid request parameters or validation failed",
                "example", "Missing required field or invalid email format"
        ));

        errorCodes.put("401", Map.of(
                "name", "Unauthorized",
                "description", "Authentication required or invalid credentials",
                "example", "Missing JWT token or invalid login credentials"
        ));

        errorCodes.put("403", Map.of(
                "name", "Forbidden",
                "description", "Insufficient permissions to access the resource",
                "example", "Regular user trying to access admin endpoints"
        ));

        errorCodes.put("404", Map.of(
                "name", "Not Found",
                "description", "Requested resource does not exist",
                "example", "Product with specified ID not found"
        ));

        errorCodes.put("409", Map.of(
                "name", "Conflict",
                "description", "Resource conflict or insufficient stock",
                "example", "Email already exists or not enough inventory"
        ));

        errorCodes.put("500", Map.of(
                "name", "Internal Server Error",
                "description", "Unexpected server error occurred",
                "example", "Database connection failure or unexpected exception"
        ));

        errorInfo.put("errorCodes", errorCodes);

        Map<String, String> errorFormat = new HashMap<>();
        errorFormat.put("timestamp", "ISO 8601 timestamp when error occurred");
        errorFormat.put("status", "HTTP status code");
        errorFormat.put("error", "Error category/type");
        errorFormat.put("message", "Human-readable error message");
        errorFormat.put("path", "API endpoint where error occurred");
        errorFormat.put("details", "Additional error details (optional)");
        errorInfo.put("errorFormat", errorFormat);

        return ResponseEntity.ok(errorInfo);
    }

    /**
     * Get API status and health information
     */
    @Operation(
            summary = "Get API status",
            description = "Returns current API status and health information."
    )
    @ApiResponse(
            responseCode = "200",
            description = "API status retrieved successfully",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "API status",
                            value = """
                    {
                      "status": "UP",
                      "timestamp": "2024-01-01T12:00:00",
                      "uptime": "5 days, 3 hours, 15 minutes",
                      "version": "1.0.0",
                      "environment": "development",
                      "database": {
                        "status": "UP",
                        "url": "postgresql://localhost:5432/shoe_store_db"
                      },
                      "features": {
                        "authentication": "enabled",
                        "fileUploads": "enabled",
                        "emailNotifications": "disabled",
                        "caching": "enabled"
                      },
                      "statistics": {
                        "totalUsers": 245,
                        "totalProducts": 156,
                        "totalOrders": 1023,
                        "activeUsers": 42
                      }
                    }
                    """
                    )
            )
    )
    @GetMapping(value = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getApiStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", java.time.LocalDateTime.now());
        status.put("version", "1.0.0");
        status.put("environment", "development");

        Map<String, String> database = new HashMap<>();
        database.put("status", "UP");
        database.put("url", "postgresql://localhost:5432/shoe_store_db");
        status.put("database", database);

        Map<String, String> features = new HashMap<>();
        features.put("authentication", "enabled");
        features.put("fileUploads", "enabled");
        features.put("emailNotifications", "disabled");
        features.put("caching", "enabled");
        status.put("features", features);

        return ResponseEntity.ok(status);
    }

    /**
     * Get sample request/response examples
     */
    @Operation(
            summary = "Get API examples",
            description = "Returns sample request and response examples for common API operations."
    )
    @GetMapping(value = "/examples", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getApiExamples() {
        Map<String, Object> examples = new HashMap<>();
        examples.put("title", "API Request/Response Examples");

        Map<String, Object> authExamples = new HashMap<>();
        authExamples.put("loginRequest", Map.of(
                "email", "user@example.com",
                "password", "user123"
        ));
        authExamples.put("loginResponse", Map.of(
                "token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                "user", Map.of(
                        "id", 1,
                        "email", "user@example.com",
                        "firstName", "John",
                        "lastName", "Doe",
                        "isAdmin", false
                ),
                "expiresAt", "2024-12-01T12:00:00"
        ));

        Map<String, Object> productExamples = new HashMap<>();
        productExamples.put("productDetails", Map.of(
                "id", 1,
                "name", "Air Max 270",
                "brand", Map.of(
                        "id", 1,
                        "name", "Nike",
                        "logoUrl", "https://example.com/nike-logo.png"
                ),
                "category", Map.of(
                        "id", 1,
                        "name", "Running"
                ),
                "basePrice", 150.00,
                "description", "Comfortable running shoes with Air Max technology",
                "models", List.of(Map.of(
                        "id", 1,
                        "color", "Black/White",
                        "material", "Mesh/Synthetic",
                        "sku", "NIKE-AM270-BW-001",
                        "price", 150.00,
                        "imageUrl", "https://example.com/shoe-image.jpg",
                        "availableSizes", List.of("8", "8.5", "9", "9.5", "10")
                ))
        ));

        Map<String, Object> cartExamples = new HashMap<>();
        cartExamples.put("addToCartRequest", Map.of(
                "shoeModelId", 1,
                "size", "9",
                "quantity", 2
        ));
        cartExamples.put("cartResponse", Map.of(
                "items", List.of(Map.of(
                        "id", 1,
                        "shoeModel", Map.of(
                                "id", 1,
                                "name", "Air Max 270 Black/White",
                                "price", 150.00,
                                "imageUrl", "https://example.com/shoe-image.jpg"
                        ),
                        "size", "9",
                        "quantity", 2,
                        "totalPrice", 300.00
                )),
                "totalItems", 2,
                "totalPrice", 300.00
        ));

        Map<String, Object> orderExamples = new HashMap<>();
        orderExamples.put("createOrderRequest", Map.of(
                "shippingAddressId", 1,
                "paymentMethod", "CREDIT_CARD"
        ));
        orderExamples.put("orderResponse", Map.of(
                "id", 1,
                "orderNumber", "ORD-20240101-0001",
                "status", "PENDING",
                "totalAmount", 300.00,
                "orderDate", "2024-01-01T12:00:00",
                "items", List.of(Map.of(
                        "shoeModel", "Air Max 270 Black/White",
                        "size", "9",
                        "quantity", 2,
                        "unitPrice", 150.00,
                        "totalPrice", 300.00
                ))
        ));

        examples.put("authentication", authExamples);
        examples.put("products", productExamples);
        examples.put("cart", cartExamples);
        examples.put("orders", orderExamples);

        return ResponseEntity.ok(examples);
    }

    /**
     * Get API rate limiting information
     */
    @Operation(
            summary = "Get rate limiting info",
            description = "Returns information about API rate limiting and usage guidelines."
    )
    @GetMapping(value = "/rate-limits", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getRateLimitInfo() {
        Map<String, Object> rateLimitInfo = new HashMap<>();
        rateLimitInfo.put("title", "API Rate Limiting");
        rateLimitInfo.put("description", "Information about API usage limits and best practices");

        Map<String, Object> limits = new HashMap<>();
        limits.put("unauthenticated", Map.of(
                "requestsPerMinute", 30,
                "requestsPerHour", 500,
                "description", "Limits for public endpoints without authentication"
        ));
        limits.put("authenticated", Map.of(
                "requestsPerMinute", 100,
                "requestsPerHour", 2000,
                "description", "Limits for authenticated users"
        ));
        limits.put("admin", Map.of(
                "requestsPerMinute", 200,
                "requestsPerHour", 5000,
                "description", "Higher limits for admin users"
        ));

        List<String> bestPractices = List.of(
                "Implement exponential backoff for retries",
                "Cache responses when appropriate",
                "Use pagination for large data sets",
                "Monitor rate limit headers in responses",
                "Consider using webhooks for real-time updates instead of polling"
        );

        List<String> headers = List.of(
                "X-RateLimit-Limit: Maximum requests allowed",
                "X-RateLimit-Remaining: Requests remaining in current window",
                "X-RateLimit-Reset: Time when the rate limit resets",
                "Retry-After: Seconds to wait before retrying (when rate limited)"
        );

        rateLimitInfo.put("limits", limits);
        rateLimitInfo.put("bestPractices", bestPractices);
        rateLimitInfo.put("responseHeaders", headers);

        return ResponseEntity.ok(rateLimitInfo);
    }

    /**
     * Hidden endpoint - redirect to Swagger UI
     */
    @Hidden
    @GetMapping("")
    public ResponseEntity<Void> redirectToSwagger() {
        return ResponseEntity.status(302)
                .header("Location", "/swagger-ui.html")
                .build();
    }
}