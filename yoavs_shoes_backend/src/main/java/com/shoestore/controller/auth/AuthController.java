package com.shoestore.controller.auth;

import com.fasterxml.jackson.annotation.JsonView;
import com.shoestore.controller.base.BaseController;
import com.shoestore.dto.auth.*;
import com.shoestore.dto.user.UserDto;
import com.shoestore.dto.user.UserDto.CreateUserDto;
import com.shoestore.dto.view.Views;
import com.shoestore.entity.user.User;
import com.shoestore.service.auth.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for authentication endpoints
 */
@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication endpoints (login, register, logout)")
public class AuthController extends BaseController {

    private final AuthService authService;

    /**
     * Register a new user
     */
    @Operation(
            summary = "Register new user",
            description = """
            Register a new user account. Email must be unique and password must meet security requirements.
            
            **Password Requirements:**
            - At least 8 characters long
            - Must contain at least one letter and one number
            - Special characters are allowed
            
            **Response includes:**
            - JWT token for immediate authentication
            - User profile information
            - Token expiration time
            """,
            tags = {"Authentication"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    name = "Successful registration",
                                    value = """
                        {
                          "success": true,
                          "message": "Registration successful",
                          "data": {
                            "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                            "tokenType": "Bearer",
                            "expiresAt": "2024-12-01T12:00:00",
                            "user": {
                              "id": 1,
                              "email": "john.doe@example.com",
                              "firstName": "John",
                              "lastName": "Doe",
                              "fullName": "John Doe",
                              "isAdmin": false,
                              "createdAt": "2024-01-01T10:00:00",
                              "lastLogin": "2024-01-01T10:00:00"
                            },
                            "message": "Registration successful"
                          },
                          "timestamp": "2024-01-01T10:00:00"
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Validation error",
                                    value = """
                        {
                          "timestamp": "2024-01-01T12:00:00",
                          "status": 400,
                          "error": "Validation Failed",
                          "message": "Invalid input parameters",
                          "path": "/api/auth/register",
                          "details": {
                            "validationErrors": {
                              "email": "Invalid email format",
                              "password": "Password must be at least 8 characters long",
                              "confirmPassword": "Password confirmation does not match"
                            }
                          }
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already exists",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Email conflict",
                                    value = """
                        {
                          "timestamp": "2024-01-01T12:00:00",
                          "status": 409,
                          "error": "Resource Conflict",
                          "message": "Email john.doe@example.com is already in use",
                          "path": "/api/auth/register"
                        }
                        """
                            )
                    )
            )
    })
    @PostMapping("/register")
    @JsonView(Views.Summary.class)
    public ResponseEntity<StandardResponse<AuthResponse>> register(@Valid @RequestBody CreateUserDto request) {
        logAction("REGISTER_ATTEMPT", "User registration attempt with email: " + request.getEmail());

        AuthResponse response = authService.register(request);

        logAction("REGISTER_SUCCESS", "User registered successfully with ID: " + response.getUser().getId());

        return created(response, "Registration successful");
    }

    /**
     * Login user
     */
    @Operation(
            summary = "User login",
            description = """
            Authenticate user with email and password. Returns JWT token for subsequent API calls.
            
            **Security Features:**
            - Account locking after 5 failed attempts
            - Failed attempt tracking
            - Account status validation
            
            **Token Usage:**
            - Include token in Authorization header: `Bearer <token>`
            - Token expires in 24 hours by default
            - Use refresh mechanism before expiration
            """,
            tags = {"Authentication"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    name = "Successful login",
                                    value = """
                        {
                          "success": true,
                          "message": "Login successful",
                          "data": {
                            "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                            "tokenType": "Bearer",
                            "expiresAt": "2024-12-01T12:00:00",
                            "user": {
                              "id": 1,
                              "email": "john.doe@example.com",
                              "firstName": "John",
                              "lastName": "Doe",
                              "fullName": "John Doe",
                              "isAdmin": false,
                              "isActive": true,
                              "createdAt": "2024-01-01T10:00:00",
                              "lastLogin": "2024-01-01T14:30:00"
                            },
                            "message": "Login successful"
                          },
                          "timestamp": "2024-01-01T14:30:00"
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Invalid credentials",
                                    value = """
                        {
                          "timestamp": "2024-01-01T12:00:00",
                          "status": 401,
                          "error": "Unauthorized",
                          "message": "Invalid email or password",
                          "path": "/api/auth/login"
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "423",
                    description = "Account locked",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Account locked",
                                    value = """
                        {
                          "timestamp": "2024-01-01T12:00:00",
                          "status": 423,
                          "error": "Account Locked",
                          "message": "Account is locked. Please try again later.",
                          "path": "/api/auth/login"
                        }
                        """
                            )
                    )
            )
    })
    @PostMapping("/login")
    @JsonView(Views.Summary.class)
    public ResponseEntity<StandardResponse<AuthResponse>> login(@Valid @RequestBody LoginRequestDto request) {
        logAction("LOGIN_ATTEMPT", "User login attempt with email: " + request.getEmail());

        AuthResponse response = authService.login(request);

        logAction("LOGIN_SUCCESS", "User logged in successfully with ID: " + response.getUser().getId());

        return success(response, "Login successful");
    }

    /**
     * Logout user
     */
    @Operation(
            summary = "User logout",
            description = """
            Logout current user and invalidate JWT token.
            
            **Token Invalidation:**
            - Token is added to blacklist
            - Subsequent requests with this token will be rejected
            - User must login again to get new token
            """,
            tags = {"Authentication"},
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Logout successful",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Successful logout",
                                    value = """
                        {
                          "success": true,
                          "message": "Logout successful",
                          "data": {
                            "message": "Logout successful",
                            "timestamp": "2024-01-01T15:30:00"
                          },
                          "timestamp": "2024-01-01T15:30:00"
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Unauthorized",
                                    value = """
                        {
                          "timestamp": "2024-01-01T12:00:00",
                          "status": 401,
                          "error": "Unauthorized",
                          "message": "Authentication required to access this resource",
                          "path": "/api/auth/logout"
                        }
                        """
                            )
                    )
            )
    })
    @PostMapping("/logout")
    @JsonView(Views.Summary.class)
    public ResponseEntity<StandardResponse<LogoutResponse>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            logAction("LOGOUT_ATTEMPT", "User logout attempt");

            LogoutResponse response = authService.logout(token);

            logAction("LOGOUT_SUCCESS", "User logged out successfully");

            return success(response, "Logout successful");
        }

        return success(LogoutResponse.builder()
                .message("Logout successful")
                .timestamp(java.time.LocalDateTime.now())
                .build(), "Logout successful");
    }
    /**
     * Get current user info from token
     */
    @Operation(
            summary = "Get current user info",
            description = """
            Get information about the currently authenticated user based on JWT token.
            
            **Returns:**
            - User profile information
            - Account status
            - Login history
            - No sensitive information (passwords, etc.)
            """,
            tags = {"Authentication"},
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User information retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Current user info",
                                    value = """
                        {
                          "success": true,
                          "message": "User information retrieved successfully",
                          "data": {
                            "id": 1,
                            "email": "john.doe@example.com",
                            "firstName": "John",
                            "lastName": "Doe",
                            "fullName": "John Doe",
                            "isAdmin": false,
                            "isActive": true,
                            "createdAt": "2024-01-01T10:00:00",
                            "lastLogin": "2024-01-01T14:30:00"
                          },
                          "timestamp": "2024-01-01T16:15:00"
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Unauthorized",
                                    value = """
                        {
                          "timestamp": "2024-01-01T12:00:00",
                          "status": 401,
                          "error": "Unauthorized",
                          "message": "Authentication required to access this resource",
                          "path": "/api/auth/me"
                        }
                        """
                            )
                    )
            )
    })

    @GetMapping("/me")
    @JsonView(Views.Detailed.class)
    public ResponseEntity<StandardResponse<UserDto>> getCurrentUserInfo() {
        User currentUser = getCurrentUser();

        logAction("GET_CURRENT_USER", "Retrieved current user info for user ID: " + currentUser.getId());

        UserDto userDto = UserDto.builder()
                .id(currentUser.getId())
                .email(currentUser.getEmail())
                .firstName(currentUser.getFirstName())
                .lastName(currentUser.getLastName())
                .fullName(currentUser.getFullName())
                .phoneNumber(currentUser.getPhoneNumber())
                .isAdmin(currentUser.getIsAdmin())
                .createdAt(currentUser.getCreatedAt())
                .lastLogin(currentUser.getLastLogin())
                .updatedAt(currentUser.getUpdatedAt())
                .build();

        return success(userDto, "User information retrieved successfully");
    }

    /**
     * Verify JWT token validity
     */
    @Operation(
            summary = "Verify token validity",
            description = """
            Verify if the provided JWT token is valid and not expired.
            
            **Use Cases:**
            - Check token validity before making API calls
            - Frontend token validation
            - Session management
            
            **Returns:**
            - Token validity status
            - Expiration information
            - User basic info
            """,
            tags = {"Authentication"},
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token is valid",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Valid token",
                                    value = """
                        {
                          "success": true,
                          "message": "Token is valid",
                          "data": {
                            "valid": true,
                            "expiresAt": "2024-12-01T12:00:00",
                            "userId": 1,
                            "email": "john.doe@example.com"
                          },
                          "timestamp": "2024-01-01T16:30:00"
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token is invalid or expired",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Invalid token",
                                    value = """
                        {
                          "timestamp": "2024-01-01T12:00:00",
                          "status": 401,
                          "error": "Unauthorized",
                          "message": "Token has expired or is invalid",
                          "path": "/api/auth/verify"
                        }
                        """
                            )
                    )
            )
    })
    @GetMapping("/verify")
    @JsonView(Views.Summary.class)
    public ResponseEntity<StandardResponse<Object>> verifyToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            try {
                String email = getCurrentUser().getEmail();
                Long userId = getCurrentUserId();

                // Create verification response
                var verificationData = java.util.Map.of(
                        "valid", true,
                        "userId", userId,
                        "email", email
                );

                logAction("TOKEN_VERIFY", "Token verified successfully for user ID: " + userId);

                return success(verificationData, "Token is valid");

            } catch (Exception e) {
                log.warn("Token verification failed: {}", e.getMessage());
                throw new com.shoestore.exception.UnauthorizedException("Token has expired or is invalid");
            }
        }

        throw new com.shoestore.exception.UnauthorizedException("No valid token provided");
    }
}