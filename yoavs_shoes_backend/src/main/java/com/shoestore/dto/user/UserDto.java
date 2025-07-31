package com.shoestore.dto.user;

import com.fasterxml.jackson.annotation.JsonView;
import com.shoestore.dto.base.BaseCrudDto;
import com.shoestore.dto.view.Views;
import com.shoestore.validation.ValidPhoneNumber;
import com.shoestore.validation.ValidPassword;
import com.shoestore.validation.PasswordMatches;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * DTO for user information with different view levels
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserDto extends BaseCrudDto {

    @JsonView(Views.Summary.class)
    private String email;

    @JsonView(Views.Summary.class)
    private String firstName;

    @JsonView(Views.Summary.class)
    private String lastName;

    @JsonView(Views.Summary.class)
    private String fullName;

    @JsonView(Views.Detailed.class)
    private String phoneNumber;

    @JsonView(Views.Summary.class)
    private Boolean isAdmin;

    @JsonView(Views.Detailed.class)
    private LocalDateTime lastLogin;

    @JsonView(Views.Admin.class)
    private Integer failedLoginAttempts;

    @JsonView(Views.Admin.class)
    private Boolean accountLocked;

    @JsonView(Views.Admin.class)
    private LocalDateTime accountLockedUntil;

    /**
     * DTO for creating new users
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "User creation DTO")
    @PasswordMatches(password = "password", confirmPassword = "confirmPassword")
    public static class CreateUserDto extends BaseCrudDto.CreateDto {

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Schema(description = "Email address", example = "john.doe@example.com", required = true)
        private String email;

        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        @Schema(description = "First name", example = "John", required = true)
        private String firstName;

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        @Schema(description = "Last name", example = "Doe", required = true)
        private String lastName;

        @Size(max = 20, message = "Phone number cannot exceed 20 characters")
        @ValidPhoneNumber
        @Schema(description = "Phone number", example = "+1234567890")
        private String phoneNumber;

        @NotBlank(message = "Password is required")
        @ValidPassword
        @Schema(description = "Password", example = "SecurePassword123!", required = true)
        private String password;

        @NotBlank(message = "Password confirmation is required")
        @Schema(description = "Confirm password", example = "SecurePassword123!", required = true)
        private String confirmPassword;
    }

    /**
     * DTO for updating user profile (user self-update)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "User profile update DTO")
    public static class UpdateUserDto extends BaseCrudDto.UpdateDto {

        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        @Schema(description = "First name", example = "John")
        private String firstName;

        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        @Schema(description = "Last name", example = "Doe")
        private String lastName;

        @Size(max = 20, message = "Phone number cannot exceed 20 characters")
        @ValidPhoneNumber
        @Schema(description = "Phone number", example = "+1234567890")
        private String phoneNumber;
    }

    /**
     * DTO for admin user updates (includes admin-only fields)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Admin user update DTO")
    public static class AdminUpdateUserDto extends BaseCrudDto.AdminUpdateDto {

        @Email(message = "Email must be valid")
        @Schema(description = "Email address", example = "john.doe@example.com")
        private String email;

        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        @Schema(description = "First name", example = "John")
        private String firstName;

        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        @Schema(description = "Last name", example = "Doe")
        private String lastName;

        @Size(max = 20, message = "Phone number cannot exceed 20 characters")
        @ValidPhoneNumber
        @Schema(description = "Phone number", example = "+1234567890")
        private String phoneNumber;

        @Schema(description = "Is admin user", example = "false")
        private Boolean isAdmin;

        @Schema(description = "Is active user", example = "true")
        private Boolean isActive;

        @Schema(description = "Reset failed login attempts", example = "0")
        private Integer failedLoginAttempts;

        @Schema(description = "Account locked until date")
        private LocalDateTime accountLockedUntil;
    }

    /**
     * DTO for password change operations
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Password change DTO")
    @PasswordMatches(password = "newPassword", confirmPassword = "confirmPassword")
    public static class ChangePasswordDto {

        @NotBlank(message = "Current password is required")
        @Schema(description = "Current password", required = true)
        private String currentPassword;

        @NotBlank(message = "New password is required")
        @ValidPassword
        @Schema(description = "New password", example = "NewSecurePassword123!", required = true)
        private String newPassword;

        @NotBlank(message = "Password confirmation is required")
        @Schema(description = "Confirm new password", example = "NewSecurePassword123!", required = true)
        private String confirmPassword;
    }
}
