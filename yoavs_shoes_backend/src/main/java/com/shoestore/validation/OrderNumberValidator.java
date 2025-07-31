package com.shoestore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for 7-digit order numbers
 */
public class OrderNumberValidator implements ConstraintValidator<ValidOrderNumber, String> {

    @Override
    public void initialize(ValidOrderNumber constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String orderNumber, ConstraintValidatorContext context) {
        if (orderNumber == null) {
            return true; // Let @NotNull handle null validation
        }
        
        // Must be exactly 7 digits
        if (orderNumber.length() != 7) {
            return false;
        }
        
        // Must be numeric
        try {
            int number = Integer.parseInt(orderNumber);
            // Must be in range 1000000-9999999
            return number >= 1000000 && number <= 9999999;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
