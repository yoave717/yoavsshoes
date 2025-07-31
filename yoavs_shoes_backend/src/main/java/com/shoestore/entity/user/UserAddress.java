package com.shoestore.entity.user;

import com.shoestore.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * User address entity for storing shipping and billing addresses
 */
@Entity
@Table(name = "user_addresses")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserAddress extends BaseEntity {

    /**
     * Reference to the user who owns this address
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    /**
     * First line of the address (street, house number)
     */
    @Column(name = "address_line_1", nullable = false, length = 255)
    @NotBlank(message = "Address line 1 is required")
    @Size(min = 5, max = 255, message = "Address line 1 must be between 5 and 255 characters")
    private String addressLine1;

    /**
     * Second line of the address (apartment, suite, etc.)
     */
    @Column(name = "address_line_2", length = 255)
    @Size(max = 255, message = "Address line 2 cannot exceed 255 characters")
    private String addressLine2;

    /**
     * City name
     */
    @Column(name = "city", nullable = false, length = 100)
    @NotBlank(message = "City is required")
    @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
    private String city;

    /**
     * State or province
     */
    @Column(name = "state", length = 100)
    @Size(max = 100, message = "State cannot exceed 100 characters")
    private String state;

    /**
     * Postal code or ZIP code
     */
    @Column(name = "postal_code", nullable = false, length = 20)
    @NotBlank(message = "Postal code is required")
    @Size(min = 3, max = 20, message = "Postal code must be between 3 and 20 characters")
    private String postalCode;

    /**
     * Country name
     */
    @Column(name = "country", nullable = false, length = 100)
    @NotBlank(message = "Country is required")
    @Size(min = 2, max = 100, message = "Country must be between 2 and 100 characters")
    @Builder.Default
    private String country = "Israel";

    /**
     * Flag indicating if this is the default address
     */
    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    /**
     * Address label/nickname (e.g., "Home", "Work", "Parents")
     */
    @Column(name = "label", length = 50)
    @Size(max = 50, message = "Address label cannot exceed 50 characters")
    private String label;

    /**
     * First name for this address
     */
    @Column(name = "first_name", nullable = false, length = 50)
    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    private String firstName;

    /**
     * Last name for this address
     */
    @Column(name = "last_name", nullable = false, length = 50)
    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    private String lastName;

    /**
     * Email address for this address
     */
    @Column(name = "email", nullable = false, length = 100)
    @NotBlank(message = "Email is required")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    /**
     * Phone number for this address
     */
    @Column(name = "phone_number", nullable = false, length = 20)
    @NotBlank(message = "Phone number is required")
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phoneNumber;

    /**
     * Special delivery instructions
     */
    @Column(name = "delivery_instructions", length = 500)
    @Size(max = 500, message = "Delivery instructions cannot exceed 500 characters")
    private String deliveryInstructions;

    // Utility methods

    /**
     * Get formatted address for display
     */
    public String getFormattedAddress() {
        StringBuilder address = new StringBuilder();
        address.append(addressLine1);

        if (addressLine2 != null && !addressLine2.trim().isEmpty()) {
            address.append(", ").append(addressLine2);
        }

        address.append(", ").append(city);

        if (state != null && !state.trim().isEmpty()) {
            address.append(", ").append(state);
        }

        address.append(" ").append(postalCode);
        address.append(", ").append(country);

        return address.toString();
    }

    /**
     * Get short formatted address (for display in lists)
     */
    public String getShortFormattedAddress() {
        return String.format("%s, %s %s", addressLine1, city, postalCode);
    }

    /**
     * Get display label (use custom label or return null if empty)
     */
    public String getDisplayLabel() {
        if (label != null && !label.trim().isEmpty()) {
            return label;
        }
        return null;
    }

    /**
     * Get full name for this address
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Validate address completeness
     */
    public boolean isComplete() {
        return addressLine1 != null && !addressLine1.trim().isEmpty() &&
                city != null && !city.trim().isEmpty() &&
                postalCode != null && !postalCode.trim().isEmpty() &&
                country != null && !country.trim().isEmpty();
    }

    /**
     * Pre-persist callback to set default values
     */
    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (isDefault == null) {
            isDefault = false;
        }
        if (country == null || country.trim().isEmpty()) {
            country = "Israel";
        }
    }

    /**
     * Pre-update callback to handle default address logic
     */
    @PreUpdate
    protected void onUpdate() {
        super.onUpdate();

        // If this address is being set as default, ensure no other address
        // for the same user is default (handled in service layer)
    }
}