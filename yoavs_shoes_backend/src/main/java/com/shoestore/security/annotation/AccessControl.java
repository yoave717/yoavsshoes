package com.shoestore.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for declarative access control on controller methods
 * 
 * This annotation can be applied to controller methods to automatically
 * validate user access based on entity ownership, admin roles, or public access.
 * 
 * @author System
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessControl {

    /**
     * The access level required for this operation
     */
    AccessLevel level() default AccessLevel.AUTHENTICATED;

    /**
     * The parameter name that contains the entity ID (default: "id")
     * This is used to extract the entity ID from method parameters
     */
    String entityIdParam() default "id";

    /**
     * The entity type class for access validation
     * If not specified, will try to infer from the method's controller generic type
     */
    Class<?> entityType() default Object.class;

    /**
     * Custom message for access denied scenarios
     */
    String accessDeniedMessage() default "";

    /**
     * Whether to skip validation entirely (useful for custom endpoints)
     */
    boolean skipValidation() default false;

    /**
     * Access levels for different operations
     */
    enum AccessLevel {
        /** Anyone can access (e.g., public product catalog) */
        PUBLIC,
        /** Only authenticated users can access */
        AUTHENTICATED,
        /** Only the owner or admin can access */
        OWNER_OR_ADMIN,
        /** Only admin can access */
        ADMIN_ONLY
    }
}
