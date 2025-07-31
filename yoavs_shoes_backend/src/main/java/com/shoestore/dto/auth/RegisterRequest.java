package com.shoestore.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for user registration
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User registration request")
public class RegisterRequest {

    @Schema(description = "User's email address", example = "john.doe@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @Schema(description = "User's password", example = "SecurePass123!")
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String password;

    @Schema(description = "Password confirmation", example = "SecurePass123!")
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    @Schema(description = "User's first name", example = "John")
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    private String lastName;

    @Schema(description = "User's phone number", example = "+972-50-1234567")
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phoneNumber;
}

