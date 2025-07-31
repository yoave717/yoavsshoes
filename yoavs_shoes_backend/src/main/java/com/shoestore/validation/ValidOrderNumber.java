package com.shoestore.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation annotation for 7-digit order numbers
 */
@Documented
@Constraint(validatedBy = OrderNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidOrderNumber {
    
    String message() default "Order number must be exactly 7 digits (1000000-9999999)";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
