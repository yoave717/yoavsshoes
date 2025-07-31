package com.shoestore.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a class as having user ownership
 * 
 * This annotation can be applied to entity classes or controller classes
 * to specify how to extract the user ID for ownership validation.
 * 
 * @author System
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserOwned {

    /**
     * The path to extract the user ID from the entity
     * Examples:
     * - "user.id" for entities with user field
     * - "owner.id" for entities with owner field
     * - "id" for User entities where entity ID == user ID
     */
    String userIdPath() default "user.id";

    /**
     * Alternative: Custom method name to extract user ID
     * If specified, will call this method on the entity to get user ID
     */
    String userIdMethod() default "";
}
