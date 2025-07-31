package com.shoestore.entity.user;

import com.shoestore.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * User entity representing registered users in the system
 *
 * Includes both regular customers and admin users
 */
@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    /**
     * User's email address - used for authentication
     * Must be unique across the system
     */
    @Column(name = "email", nullable = false, unique = true, length = 255)
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    /**
     * Hashed password for authentication
     */
    @Column(name = "password_hash", nullable = false, length = 255)
    @NotBlank(message = "Password is required")
    @ToString.Exclude
    private String passwordHash;

    /**
     * User's first name
     */
    @Column(name = "first_name", nullable = false, length = 100)
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    private String firstName;

    /**
     * User's last name
     */
    @Column(name = "last_name", nullable = false, length = 100)
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    private String lastName;

    /**
     * Flag indicating if user has admin privileges
     */
    @Column(name = "is_admin", nullable = false)
    @Builder.Default
    private Boolean isAdmin = false;

    /**
     * Phone number (optional)
     */
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    /**
     * User's addresses - one-to-many relationship
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<UserAddress> addresses = new ArrayList<>();

    /**
     * Last login timestamp
     */
    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    /**
     * Failed login attempts counter (for security)
     */
    @Column(name = "failed_login_attempts")
    @Builder.Default
    private Integer failedLoginAttempts = 0;

    /**
     * Account locked timestamp (for security)
     */
    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;

    // Utility methods

    /**
     * Get user's full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Check if user has admin role
     */
    public boolean hasAdminRole() {
        return isAdmin != null && isAdmin;
    }

    /**
     * Check if account is locked
     */
    public boolean isAccountLocked() {
        return accountLockedUntil != null &&
                accountLockedUntil.isAfter(LocalDateTime.now());
    }

    /**
     * Get default address
     */
    public UserAddress getDefaultAddress() {
        return addresses.stream()
                .filter(UserAddress::getIsDefault)
                .findFirst()
                .orElse(null);
    }

    /**
     * Increment failed login attempts
     */
    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts = (this.failedLoginAttempts == null ? 0 : this.failedLoginAttempts) + 1;

        // Lock account after 5 failed attempts for 30 minutes
        if (this.failedLoginAttempts >= 5) {
            this.accountLockedUntil = LocalDateTime.now().plusMinutes(30);
        }
    }

    /**
     * Reset failed login attempts
     */
    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
        this.accountLockedUntil = null;
        this.lastLogin = java.time.LocalDateTime.now();
    }

    /**
     * Pre-persist callback to set default values
     */
    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (isAdmin == null) {
            isAdmin = false;
        }
        if (failedLoginAttempts == null) {
            failedLoginAttempts = 0;
        }
    }
}