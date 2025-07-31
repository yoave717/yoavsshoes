package com.shoestore.security.aspect;

import com.shoestore.exception.UnauthorizedException;
import com.shoestore.security.annotation.AccessControl;
import com.shoestore.security.annotation.UserOwned;
import com.shoestore.security.service.AccessControlService;
import com.shoestore.security.service.EntityCache;
import com.shoestore.service.base.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Aspect that intercepts methods annotated with @AccessControl
 * and performs automatic access validation
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AccessControlAspect {

    private final ApplicationContext applicationContext;
    private final AccessControlService accessControlService;
    private final EntityCache entityCache;

    /**
     * Intercept methods annotated with @AccessControl and validate access
     */
    @Before("@annotation(accessControl)")
    public void validateAccess(JoinPoint joinPoint, AccessControl accessControl) {
        if (accessControl.skipValidation()) {
            log.debug("Skipping access validation for method: {}", joinPoint.getSignature().getName());
            return;
        }

        log.debug("Validating access for method: {} with level: {}", 
                 joinPoint.getSignature().getName(), accessControl.level());

        try {
            // Extract entity ID from method parameters
            Long entityId = extractEntityId(joinPoint, accessControl.entityIdParam());
            
            if (entityId == null && accessControl.level() == AccessControl.AccessLevel.OWNER_OR_ADMIN) {
                throw new UnauthorizedException("Entity ID is required for ownership validation");
            }

            // For ownership validation, we need to load the entity and extract owner ID
            Long entityOwnerId = null;
            if (accessControl.level() == AccessControl.AccessLevel.OWNER_OR_ADMIN && entityId != null) {
                entityOwnerId = extractEntityOwnerId(entityId, accessControl, joinPoint);
            }

            // Perform access validation using the service
            accessControlService.validateAccess(accessControl.level(), entityId, entityOwnerId);
            
        } catch (UnauthorizedException e) {
            // Re-throw with custom message if provided
            String message = accessControl.accessDeniedMessage().isEmpty() 
                ? e.getMessage()
                : accessControl.accessDeniedMessage();
            
            log.warn("Access validation failed: {}", e.getMessage());
            throw new UnauthorizedException(message);
            
        } catch (Exception e) {
            String message = accessControl.accessDeniedMessage().isEmpty() 
                ? "Access denied for operation: " + joinPoint.getSignature().getName()
                : accessControl.accessDeniedMessage();
            
            log.warn("Access validation failed: {}", e.getMessage());
            throw new UnauthorizedException(message);
        }
    }

    /**
     * Extract entity ID from method parameters
     */
    private Long extractEntityId(JoinPoint joinPoint, String paramName) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameterNames.length; i++) {
            if (paramName.equals(parameterNames[i])) {
                Object value = args[i];
                if (value instanceof Long) {
                    return (Long) value;
                } else if (value instanceof String) {
                    try {
                        return Long.parseLong((String) value);
                    } catch (NumberFormatException e) {
                        log.warn("Could not parse entity ID '{}' as Long", value);
                        return null;
                    }
                }
            }
        }

        // If not found in parameters, try to extract from path variables
        return extractFromPathVariables(paramName);
    }

    /**
     * Extract entity ID from path variables when not found in method parameters
     */
    private Long extractFromPathVariables(String paramName) {
        try {
            ServletRequestAttributes requestAttributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
            if (requestAttributes != null) {
                HttpServletRequest request = requestAttributes.getRequest();
                String pathInfo = request.getPathInfo();
                
                // Simple extraction for REST-style URLs like /api/users/{id}
                if (pathInfo != null && pathInfo.matches(".*/(\\d+)(/.*)?")) {
                    String[] parts = pathInfo.split("/");
                    for (String part : parts) {
                        if (part.matches("\\d+")) {
                            return Long.parseLong(part);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Could not extract entity ID from path variables: {}", e.getMessage());
        }
        
        return null;
    }

    /**
     * Extract owner user ID from entity
     */
    private Long extractEntityOwnerId(Long entityId, AccessControl accessControl, JoinPoint joinPoint) {
        try {
            // Load the entity
            Object entity = loadEntity(entityId, accessControl, joinPoint);
            if (entity == null) {
                throw new UnauthorizedException("Entity not found for ownership validation");
            }

            // Extract user ID from entity
            return extractUserIdFromEntity(entity, joinPoint);

        } catch (Exception e) {
            log.error("Error extracting entity owner ID: {}", e.getMessage());
            throw new UnauthorizedException("Could not determine entity ownership");
        }
    }

    /**
     * Load entity by ID for ownership validation
     */
    private Object loadEntity(Long entityId, AccessControl accessControl, JoinPoint joinPoint) {
        try {
            // Determine entity type
            Class<?> entityType = accessControl.entityType();
            if (entityType == Object.class) {
                // Try to infer from controller generic type
                entityType = inferEntityTypeFromController(joinPoint.getTarget());
            }

            if (entityType == null || entityType == Object.class) {
                log.warn("Could not determine entity type for access validation");
                return null;
            }

            // Check cache first
            String cacheKey = EntityCache.generateKey(entityType, entityId);
            Object cachedEntity = entityCache.get(cacheKey, entityType);
            if (cachedEntity != null) {
                log.debug("Using cached entity for access validation: {}", cacheKey);
                return cachedEntity;
            }

            // Find the appropriate service to load the entity
            Object service = findServiceForEntity(entityType);
            if (service != null) {
                Object entity = null;
                
                // Try to find a method that includes necessary relationships for access control
                // Look for methods like findByIdWithRelations, findByIdForAccessControl, etc.
                String[] possibleMethodNames = {
                    "findByIdWithUser", // UserAddress specific
                    "findByIdWithOwner", // Generic owner relationship
                    "findByIdWithRelations", // Generic relationships
                    "findByIdForAccessControl", // Specific for access control
                    "findByIdEager" // Eager loading
                };
                
                for (String methodName : possibleMethodNames) {
                    try {
                        Method relationMethod = service.getClass().getMethod(methodName, Long.class);
                        Object result = relationMethod.invoke(service, entityId);
                        
                        // Handle Optional return types
                        if (result instanceof Optional) {
                            entity = ((Optional<?>) result).orElse(null);
                        } else {
                            entity = result;
                        }
                        
                        if (entity != null) {
                            log.debug("Used {} method for {} entity", methodName, entityType.getSimpleName());
                            break;
                        }
                    } catch (NoSuchMethodException e) {
                        log.debug("{} method not found for {}", methodName, entityType.getSimpleName());
                    }
                }
                
                // Fallback to standard getById method
                if (entity == null) {
                    Method getByIdMethod = service.getClass().getMethod("getById", Object.class);
                    entity = getByIdMethod.invoke(service, entityId);
                    log.debug("Used standard getById method for {} entity", entityType.getSimpleName());
                }
                
                // Cache the entity for future use in the same request
                if (entity != null) {
                    entityCache.put(cacheKey, entity);
                    log.debug("Cached entity for future use: {}", cacheKey);
                }
                
                return entity;
            }

            log.warn("No service found for entity type: {}", entityType.getName());
            return null;

        } catch (Exception e) {
            log.error("Error loading entity for access validation: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Infer entity type from controller's generic type
     */
    private Class<?> inferEntityTypeFromController(Object controller) {
        Type genericSuperclass = controller.getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericSuperclass;
            Type[] typeArgs = paramType.getActualTypeArguments();
            if (typeArgs.length > 0 && typeArgs[0] instanceof Class) {
                return (Class<?>) typeArgs[0];
            }
        }
        return null;
    }

    /**
     * Find service bean for the given entity type
     */
    private Object findServiceForEntity(Class<?> entityType) {
        try {
            // Try to find service by naming convention
            String serviceName = entityType.getSimpleName().toLowerCase() + "Service";
            return applicationContext.getBean(serviceName);
        } catch (Exception e) {
            log.debug("Could not find service by convention for entity: {}", entityType.getName());
            
            // Try to find any BaseService that might handle this entity type
            String[] serviceNames = applicationContext.getBeanNamesForType(BaseService.class);
            for (String name : serviceNames) {
                try {
                    Object service = applicationContext.getBean(name);
                    // This is a simplified check - you might want to enhance this
                    // to properly determine if a service handles a specific entity type
                    return service;
                } catch (Exception ex) {
                    // Continue searching
                }
            }
            
            return null;
        }
    }

    /**
     * Extract user ID from entity using @UserOwned annotation or common patterns
     */
    private Long extractUserIdFromEntity(Object entity, JoinPoint joinPoint) {
        try {
            // First, check if the controller or method has @UserOwned annotation
            UserOwned userOwned = findUserOwnedAnnotation(joinPoint);
            
            if (userOwned != null) {
                return extractUserIdUsingAnnotation(entity, userOwned);
            }

            // Fallback: try common field names
            return extractUserIdUsingCommonFields(entity);

        } catch (Exception e) {
            log.error("Error extracting user ID from entity: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Find @UserOwned annotation on method, controller, or entity
     */
    private UserOwned findUserOwnedAnnotation(JoinPoint joinPoint) {
        // Check method first
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        UserOwned methodAnnotation = method.getAnnotation(UserOwned.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }

        // Check controller class
        Class<?> controllerClass = joinPoint.getTarget().getClass();
        UserOwned controllerAnnotation = controllerClass.getAnnotation(UserOwned.class);
        if (controllerAnnotation != null) {
            return controllerAnnotation;
        }

        return null;
    }

    /**
     * Extract user ID using @UserOwned annotation configuration
     */
    private Long extractUserIdUsingAnnotation(Object entity, UserOwned userOwned) {
        try {
            if (!userOwned.userIdMethod().isEmpty()) {
                // Use custom method
                Method method = entity.getClass().getMethod(userOwned.userIdMethod());
                Object result = method.invoke(entity);
                return result instanceof Long ? (Long) result : null;
            }

            // Use field path
            return extractUserIdFromPath(entity, userOwned.userIdPath());

        } catch (Exception e) {
            log.error("Error extracting user ID using annotation: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extract user ID from nested field path (e.g., "user.id")
     */
    private Long extractUserIdFromPath(Object entity, String path) {
        try {
            log.debug("Extracting user ID from path '{}' on entity: {}", path, 
                     entity != null ? entity.getClass().getSimpleName() : "null");
            
            String[] parts = path.split("\\.");
            Object current = entity;

            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                if (current == null) {
                    log.debug("Current object is null at part '{}' (index {})", part, i);
                    return null;
                }

                // Handle Hibernate proxy objects - get the actual class
                Class<?> actualClass = current.getClass();
                boolean isProxy = actualClass.getName().contains("$HibernateProxy");
                if (isProxy) {
                    actualClass = actualClass.getSuperclass();
                    log.debug("Detected Hibernate proxy, using superclass: {}", actualClass.getName());
                }

                Field field = findField(actualClass, part);
                if (field == null) {
                    log.debug("Field '{}' not found in class: {}", part, actualClass.getName());
                    return null;
                }

                field.setAccessible(true);
                Object previousCurrent = current;
                current = field.get(current);
                
                log.debug("Extracted field '{}': {} -> {}", part, 
                         previousCurrent.getClass().getSimpleName(),
                         current != null ? current.getClass().getSimpleName() : "null");
                
                // If we got a Hibernate proxy, try to initialize it to get the actual value
                if (current != null && current.getClass().getName().contains("$HibernateProxy")) {
                    try {
                        log.debug("Attempting to initialize Hibernate proxy");
                        // Force initialization of the proxy by accessing a method
                        current.getClass().getMethod("getId").invoke(current);
                        log.debug("Successfully initialized Hibernate proxy");
                    } catch (Exception proxyException) {
                        log.debug("Could not initialize Hibernate proxy: {}", proxyException.getMessage());
                        // Continue with the proxy object - it might still work
                    }
                }
                
                // Handle lazy-loaded entities - if current is null after field access,
                // it might be because the lazy proxy hasn't been initialized
                if (current == null) {
                    log.warn("Lazy-loaded '{}' field is null - entity wasn't fetched with joins. Consider using JOIN FETCH or a specialized repository method", part);
                    return null;
                }
            }

            if (current instanceof Long) {
                log.debug("Successfully extracted user ID: {}", current);
                return (Long) current;
            } else {
                log.debug("Final value is not a Long: {} (type: {})", 
                         current, current != null ? current.getClass().getName() : "null");
                return null;
            }

        } catch (Exception e) {
            log.error("Error extracting user ID from path '{}': {}", path, e.getMessage());
            log.debug("Entity class: {}, Exception type: {}, Stack trace: ", 
                     entity != null ? entity.getClass().getName() : "null", 
                     e.getClass().getSimpleName(), e);
            return null;
        }
    }

    /**
     * Extract user ID using common field names and patterns
     */
    private Long extractUserIdUsingCommonFields(Object entity) {
        // Common patterns for ownership relationships
        String[] commonFields = {
            "userId",           // Direct user ID field
            "user.id",          // User relationship with ID field
            "owner.id",         // Owner relationship with ID field  
            "ownerId",          // Direct owner ID field
            "createdBy.id",     // Created by user relationship
            "createdById",      // Direct created by ID field
            "assignedTo.id",    // Assigned to user relationship
            "assignedToId",     // Direct assigned to ID field
            "customer.id",      // Customer relationship
            "customerId"        // Direct customer ID field
        };
        
        for (String fieldPath : commonFields) {
            Long userId = extractUserIdFromPath(entity, fieldPath);
            if (userId != null) {
                log.debug("Found user ID {} using field path: {}", userId, fieldPath);
                return userId;
            }
        }

        log.debug("No user ID found using common field patterns for entity: {}", 
                 entity.getClass().getSimpleName());
        return null;
    }

    /**
     * Find field in class hierarchy
     */
    private Field findField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    /**
     * Guidelines for implementing specialized entity loading methods in services:
     * 
     * To support access control validation with lazy-loaded relationships, services can implement
     * one or more of these methods (in order of preference):
     * 
     * 1. findByIdForAccessControl(Long id) - Specifically for access control, loads necessary relationships
     * 2. findByIdWithOwner(Long id) - Loads entity with owner/user relationship
     * 3. findByIdWithRelations(Long id) - Loads entity with all necessary relationships  
     * 4. findByIdEager(Long id) - Loads entity with eager fetching
     * 5. findByIdWithUser(Long id) - Entity-specific (e.g., UserAddress)
     * 
     * These methods should return either the entity directly or Optional<Entity>.
     * They should use JOIN FETCH in their @Query annotations to avoid lazy loading issues.
     * 
     * Example for any entity with a user relationship:
     * 
     * @Query("SELECT e FROM EntityName e JOIN FETCH e.user WHERE e.id = :id")
     * Optional<EntityName> findByIdWithUser(@Param("id") Long id);
     * 
     * @Query("SELECT e FROM EntityName e JOIN FETCH e.owner WHERE e.id = :id") 
     * Optional<EntityName> findByIdWithOwner(@Param("id") Long id);
     */
}
