package com.shoestore.dto.auth;

import com.fasterxml.jackson.annotation.JsonView;
import com.shoestore.dto.user.UserDto;
import com.shoestore.dto.view.Views;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for authentication response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication response DTO")
public class AuthResponse {

    @JsonView(Views.Summary.class)
    @Schema(description = "JWT token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @JsonView(Views.Summary.class)
    @Schema(description = "Token type", example = "Bearer")
    private String tokenType;

    @JsonView(Views.Summary.class)
    @Schema(description = "Token expiration time")
    private LocalDateTime expiresAt;

    @JsonView(Views.Summary.class)
    @Schema(description = "User information")
    private UserDto user;

    @JsonView(Views.Summary.class)
    @Schema(description = "Response message", example = "Login successful")
    private String message;
}

