package com.shoestore.dto.user;

import com.fasterxml.jackson.annotation.JsonView;
import com.shoestore.dto.base.BaseCrudDto;
import com.shoestore.dto.view.Views;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Unified DTO for user address operations (create, update, response)
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserAddressDto extends BaseCrudDto {

    @JsonView(Views.Summary.class)
    private Long userId;

    @JsonView(Views.Summary.class)
    private String addressLine1;

    @JsonView(Views.Detailed.class)
    private String addressLine2;

    @JsonView(Views.Summary.class)
    private String city;

    @JsonView(Views.Summary.class)
    private String state;

    @JsonView(Views.Summary.class)
    private String postalCode;

    @JsonView(Views.Summary.class)
    private String country;

    @JsonView(Views.Summary.class)
    private Boolean isDefault;

    @JsonView(Views.Summary.class)
    private String label;

    @JsonView(Views.Detailed.class)
    private String firstName;

    @JsonView(Views.Detailed.class)
    private String lastName;

    @JsonView(Views.Admin.class)
    private String email;

    @JsonView(Views.Detailed.class)
    private String phoneNumber;

    @JsonView(Views.Detailed.class)
    private String deliveryInstructions;

    @JsonView(Views.Summary.class)
    private String formattedAddress;

    @JsonView(Views.Summary.class)
    private String shortFormattedAddress;

    @JsonView(Views.Summary.class)
    private String displayLabel;

    @JsonView(Views.Summary.class)
    private Boolean isComplete;


    /**
     * DTO for creating new addresses
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class CreateAddressDto extends BaseCrudDto.CreateDto {

        @NotBlank(message = "Address line 1 is required")
        @Size(min = 5, max = 255, message = "Address line 1 must be between 5 and 255 characters")
        private String addressLine1;

        @Size(max = 255, message = "Address line 2 cannot exceed 255 characters")
        private String addressLine2;

        @NotBlank(message = "City is required")
        @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
        private String city;

        @Size(max = 100, message = "State cannot exceed 100 characters")
        private String state;

        @NotBlank(message = "Postal code is required")
        @Size(min = 3, max = 20, message = "Postal code must be between 3 and 20 characters")
        private String postalCode;

        @NotBlank(message = "Country is required")
        @Size(min = 2, max = 100, message = "Country must be between 2 and 100 characters")
        private String country;

        private Boolean isDefault;

        @Size(max = 50, message = "Address label cannot exceed 50 characters")
        private String label;

        @NotBlank(message = "First name is required")
        @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
        private String firstName;

        @NotBlank(message = "Last name is required")
        @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
        private String lastName;

        @NotBlank(message = "Email is required")
        @Size(max = 100, message = "Email cannot exceed 100 characters")
        private String email;

        @NotBlank(message = "Phone number is required")
        @Size(max = 20, message = "Phone number cannot exceed 20 characters")
        private String phoneNumber;

        @Size(max = 500, message = "Delivery instructions cannot exceed 500 characters")
        private String deliveryInstructions;
    }

    /**
     * DTO for updating user addresses (user self-update)
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class UpdateAddressDto extends BaseCrudDto.UpdateDto {

        @Size(min = 5, max = 255, message = "Address line 1 must be between 5 and 255 characters")
        private String addressLine1;

        @Size(max = 255, message = "Address line 2 cannot exceed 255 characters")
        private String addressLine2;

        @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
        private String city;

        @Size(max = 100, message = "State cannot exceed 100 characters")
        private String state;

        @Size(min = 3, max = 20, message = "Postal code must be between 3 and 20 characters")
        private String postalCode;

        @Size(min = 2, max = 100, message = "Country must be between 2 and 100 characters")
        private String country;

        private Boolean isDefault;

        @Size(max = 50, message = "Address label cannot exceed 50 characters")
        private String label;

        @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
        private String firstName;

        @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
        private String lastName;

        @Size(max = 100, message = "Email cannot exceed 100 characters")
        private String email;

        @Size(max = 20, message = "Phone number cannot exceed 20 characters")
        private String phoneNumber;

        @Size(max = 500, message = "Delivery instructions cannot exceed 500 characters")
        private String deliveryInstructions;
    }

    /**
     * DTO for admin address updates (includes admin-only fields)
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class AdminUpdateAddressDto extends BaseCrudDto.AdminUpdateDto {

        private Long userId;

        @Size(min = 5, max = 255, message = "Address line 1 must be between 5 and 255 characters")
        private String addressLine1;

        @Size(max = 255, message = "Address line 2 cannot exceed 255 characters")
        private String addressLine2;

        @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
        private String city;

        @Size(max = 100, message = "State cannot exceed 100 characters")
        private String state;

        @Size(min = 3, max = 20, message = "Postal code must be between 3 and 20 characters")
        private String postalCode;

        @Size(min = 2, max = 100, message = "Country must be between 2 and 100 characters")
        private String country;

        private Boolean isDefault;

        @Size(max = 50, message = "Address label cannot exceed 50 characters")
        private String label;

        @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
        private String firstName;

        @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
        private String lastName;

        @Size(max = 100, message = "Email cannot exceed 100 characters")
        private String email;

        @Size(max = 20, message = "Phone number cannot exceed 20 characters")
        private String phoneNumber;

        @Size(max = 500, message = "Delivery instructions cannot exceed 500 characters")
        private String deliveryInstructions;

        private Boolean isComplete;
    }

    /**
     * DTO for creating order address information
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Order address information DTO")
    public static class CreateOrderAddressInfoDto {

    @JsonView(Views.Summary.class)
    @Schema(description = "Unique identifier for the order address", example = "1")
    @NotNull(message = "Address ID is required")
    private Long id;

    }
}