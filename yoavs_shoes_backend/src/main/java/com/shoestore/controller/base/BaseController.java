package com.shoestore.controller.base;

import com.fasterxml.jackson.annotation.JsonView;
import com.shoestore.dto.view.Views;
import com.shoestore.entity.user.User;
import com.shoestore.exception.UnauthorizedException;
import com.shoestore.security.CustomUserDetailsService;
import com.shoestore.security.service.EntityCache;
import com.shoestore.service.user.UserService;
import com.shoestore.util.LoggingUtil;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple base controller for controllers that don't manage specific entities
 */
@Slf4j
public abstract class BaseController {

    @Autowired
    protected UserService userService;

    @Autowired
    private EntityCache entityCache;

    /**
     * Get the currently authenticated user
     */
    protected User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Authentication required");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetailsService.UserPrincipal userPrincipal) {
            User user = userService.getById(userPrincipal.getId());
            return user;
        }

        throw new UnauthorizedException("Invalid authentication principal");
    }

    /**
     * Get the current user ID
     */
    protected Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Check if current user is admin
     */
    protected boolean isCurrentUserAdmin() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if a specific user has admin role
     */
    protected boolean isAdmin(User user) {
        return user != null && user.hasAdminRole();
    }

    /**
     * Get a cached entity if it exists
     */
    protected <T> T getCachedEntity(Class<T> entityType, Object id) {
        if (entityCache != null) {
            String cacheKey = EntityCache.generateKey(entityType, id);
            return entityCache.get(cacheKey, entityType);
        }
        return null;
    }

    /**
     * Get a cached entity or fetch it if not cached
     */
    protected <T> T getCachedEntityOrFetch(Class<T> entityType, Object id, 
                                         java.util.function.Supplier<T> fetchFunction) {
        T cachedEntity = getCachedEntity(entityType, id);
        if (cachedEntity != null) {
            log.debug("Using cached entity: {}", entityType.getSimpleName() + ":" + id);
            return cachedEntity;
        }
        
        log.debug("Entity not cached, fetching: {}", entityType.getSimpleName() + ":" + id);
        T entity = fetchFunction.get();
        
        if (entity != null && entityCache != null) {
            String cacheKey = EntityCache.generateKey(entityType, id);
            entityCache.put(cacheKey, entity);
        }
        
        return entity;
    }

    /**
     * Create pageable with default values
     */
    protected Pageable createPageable(Integer page, Integer size, String sortBy, String sortDirection) {
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0 && size <= 100) ? size : 20;

        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            Sort.Direction direction = Sort.Direction.ASC;
            if ("desc".equalsIgnoreCase(sortDirection)) {
                direction = Sort.Direction.DESC;
            }
            sort = Sort.by(direction, sortBy);
        }

        return PageRequest.of(pageNumber, pageSize, sort);
    }

    /**
     * Create pageable with default sorting
     */
    protected Pageable createPageable(Integer page, Integer size) {
        return createPageable(page, size, null, null);
    }

    /**
     * Create success response with data
     */
    protected <T> ResponseEntity<StandardResponse<T>> success(T data) {
        return ResponseEntity.ok(StandardResponse.success(data));
    }

    /**
     * Create success response with data and message
     */
    protected <T> ResponseEntity<StandardResponse<T>> success(T data, String message) {
        return ResponseEntity.ok(StandardResponse.success(data, message));
    }

    /**
     * Create success response for creation (201)
     */
    protected <T> ResponseEntity<StandardResponse<T>> created(T data) {
        return ResponseEntity.status(201).body(StandardResponse.success(data, "Resource created successfully"));
    }

    /**
     * Create success response for creation with location header
     */
    protected <T> ResponseEntity<StandardResponse<T>> created(T data, String resourcePath, Object... pathVariables) {
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(resourcePath)
                .buildAndExpand(pathVariables)
                .toUri();

        return ResponseEntity.created(location)
                .body(StandardResponse.success(data, "Resource created successfully"));
    }

    /**
     * Create no content response (204)
     */
    protected ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }

    /**
     * Create accepted response (202)
     */
    protected <T> ResponseEntity<StandardResponse<T>> accepted(T data, String message) {
        return ResponseEntity.status(202).body(StandardResponse.success(data, message));
    }

    /**
     * Log controller action
     */
    protected void logAction(String action, String details) {
        try {
            Long userId = getCurrentUserId();
            LoggingUtil.logUserAction(userId, action, details);
        } catch (Exception e) {
            log.debug("Could not log user action: {}", e.getMessage());
        }
    }

    /**
     * Log controller action with entity information
     */
    protected void logEntityAction(String action, String entityType, String entityId, String details) {
        LoggingUtil.logEntityOperation(action, entityType, entityId, details);
    }

    /**
     * Validate pagination parameters
     */
    protected void validatePagination(Integer page, Integer size) {
        if (page != null && page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (size != null && (size <= 0 || size > 100)) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }
    }

    /**
     * Validate sort parameters
     */
    protected void validateSort(String sortBy, String[] allowedSortFields) {
        if (sortBy != null && allowedSortFields != null) {
            boolean isValidSort = false;
            for (String allowedField : allowedSortFields) {
                if (allowedField.equalsIgnoreCase(sortBy)) {
                    isValidSort = true;
                    break;
                }
            }
            if (!isValidSort) {
                throw new IllegalArgumentException(
                        "Invalid sort field: " + sortBy + ". Allowed fields: " + String.join(", ", allowedSortFields)
                );
            }
        }
    }

    /**
     * Create metadata for paginated responses
     */
    protected Map<String, Object> createPageMetadata(org.springframework.data.domain.Page<?> page) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("page", page.getNumber());
        metadata.put("size", page.getSize());
        metadata.put("totalElements", page.getTotalElements());
        metadata.put("totalPages", page.getTotalPages());
        metadata.put("first", page.isFirst());
        metadata.put("last", page.isLast());
        metadata.put("numberOfElements", page.getNumberOfElements());
        metadata.put("empty", page.isEmpty());
        return metadata;
    }

    /**
     * Generic API Response wrapper
     */
    public static class StandardResponse<T> {
        @JsonView(Views.Summary.class)
        private boolean success;
        
        @JsonView(Views.Summary.class)
        private String message;
        
        @JsonView(Views.Summary.class)
        private T data;
        
        @JsonView(Views.Summary.class)
        private Map<String, Object> metadata;
        
        @JsonView(Views.Summary.class)
        private LocalDateTime timestamp;

        public StandardResponse() {
            this.timestamp = LocalDateTime.now();
        }

        public StandardResponse(boolean success, String message, T data) {
            this();
            this.success = success;
            this.message = message;
            this.data = data;
        }

        // Static factory methods
        public static <T> StandardResponse<T> success(T data) {
            return new StandardResponse<>(true, "Success", data);
        }

        public static <T> StandardResponse<T> success(T data, String message) {
            return new StandardResponse<>(true, message, data);
        }

        public static <T> StandardResponse<T> error(String message) {
            return new StandardResponse<>(false, message, null);
        }

        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public T getData() { return data; }
        public void setData(T data) { this.data = data; }

        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

        // Builder pattern
        public StandardResponse<T> withMetadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        public StandardResponse<T> withMetadata(String key, Object value) {
            if (this.metadata == null) {
                this.metadata = new HashMap<>();
            }
            this.metadata.put(key, value);
            return this;
        }
    }
}
