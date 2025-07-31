package com.shoestore.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for password change
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Password change request")
public class ChangePasswordRequest {

    @Schema(description = "Current password", example = "OldPassword123!")
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @Schema(description = "New password", example = "NewPassword123!")
    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String newPassword;

    @Schema(description = "New password confirmation", example = "NewPassword123!")
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
}
