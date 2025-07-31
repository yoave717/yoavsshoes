package com.shoestore.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.*;
import java.lang.reflect.Field;

@Documented
@Constraint(validatedBy = PasswordMatches.PasswordMatchesValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatches {
    String message() default "Password confirmation does not match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    String password();
    String confirmPassword();

    // Inner validator class to keep everything in one file
    class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

        private String passwordField;
        private String confirmPasswordField;

        @Override
        public void initialize(PasswordMatches constraintAnnotation) {
            this.passwordField = constraintAnnotation.password();
            this.confirmPasswordField = constraintAnnotation.confirmPassword();
        }

        @Override
        public boolean isValid(Object value, ConstraintValidatorContext context) {
            if (value == null) {
                return true;
            }

            try {
                Field passwordFieldObj = value.getClass().getDeclaredField(passwordField);
                Field confirmPasswordFieldObj = value.getClass().getDeclaredField(confirmPasswordField);
                
                passwordFieldObj.setAccessible(true);
                confirmPasswordFieldObj.setAccessible(true);
                
                String password = (String) passwordFieldObj.get(value);
                String confirmPassword = (String) confirmPasswordFieldObj.get(value);
                
                boolean matches = password != null && password.equals(confirmPassword);
                
                if (!matches) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                           .addPropertyNode(confirmPasswordField)
                           .addConstraintViolation();
                }
                
                return matches;
                
            } catch (Exception e) {
                return false;
            }
        }
    }
}
