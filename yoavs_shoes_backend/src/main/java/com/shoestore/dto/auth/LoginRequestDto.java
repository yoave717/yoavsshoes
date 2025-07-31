package com.shoestore.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for user login
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User login request")
public class LoginRequestDto {

    @Schema(description = "User's email address", example = "john.doe@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "User's password", example = "SecurePass123!")
    @NotBlank(message = "Password is required")
    private String password;
}
