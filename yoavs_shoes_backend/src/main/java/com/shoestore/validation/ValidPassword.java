package com.shoestore.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$", 
         message = "Password must be at least 8 characters long and contain at least one letter and one number")
public @interface ValidPassword {
    String message() default "Password must be at least 8 characters long and contain at least one letter and one number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
