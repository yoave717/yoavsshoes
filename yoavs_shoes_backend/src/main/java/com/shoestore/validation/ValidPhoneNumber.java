package com.shoestore.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Pattern(regexp = "^(\\+\\d{1,2}\\s?)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$", 
         message = "Phone number must be a valid international format (10-15 digits, optional +)")
public @interface ValidPhoneNumber {
    String message() default "Phone number must be a valid international format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
