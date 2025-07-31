package com.shoestore.security.service;

import com.shoestore.entity.user.User;
import com.shoestore.exception.UnauthorizedException;
import com.shoestore.security.CustomUserDetailsService;
import com.shoestore.security.annotation.AccessControl;
import com.shoestore.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service for handling access control validation
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AccessControlService {

    private final UserService userService;

    /**
     * Get the currently authenticated user
     */
    public User getCurrentUser() {
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
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Check if user is admin
     */
    public boolean isAdmin(User user) {
        return user.hasAdminRole();
    }

    /**
     * Check if current user is admin
     */
    public boolean isCurrentUserAdmin() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate access based on access level and entity ownership
     */
    public void validateAccess(AccessControl.AccessLevel level, Long entityId, Long entityOwnerId) {
        switch (level) {
            case PUBLIC:
                // No validation needed
                return;
                
            case AUTHENTICATED:
                // Just verify user is authenticated
                getCurrentUser();
                return;
                
            case ADMIN_ONLY:
                validateAdminAccess();
                return;
                
            case OWNER_OR_ADMIN:
                validateOwnerOrAdminAccess(entityOwnerId);
                return;
                
            default:
                throw new UnauthorizedException("Unknown access level: " + level);
        }
    }

    /**
     * Validate admin access
     */
    public void validateAdminAccess() {
        User currentUser = getCurrentUser();
        if (!isAdmin(currentUser)) {
            throw new UnauthorizedException("Admin access required");
        }
    }

    /**
     * Validate owner or admin access
     */
    public void validateOwnerOrAdminAccess(Long entityOwnerId) {
        User currentUser = getCurrentUser();
        
        // Admin can access anything
        if (isAdmin(currentUser)) {
            return;
        }

        if (entityOwnerId == null) {
            throw new UnauthorizedException("Could not determine entity ownership");
        }

        if (!currentUser.getId().equals(entityOwnerId)) {
            throw new UnauthorizedException("Access denied: not the owner of this entity");
        }
    }

    /**
     * Validate user entity access (for user-specific operations)
     */
    public User validateUserEntityAccess(Long userId, String operation, AccessControl.AccessLevel accessLevel) {
        User currentUser = getCurrentUser();

        if (accessLevel == AccessControl.AccessLevel.ADMIN_ONLY) {
            if (!isAdmin(currentUser)) {
                throw new UnauthorizedException("Admin access required for " + operation);
            }
            return userService.getById(userId);
        }

        if (accessLevel == AccessControl.AccessLevel.OWNER_OR_ADMIN) {
            if (isAdmin(currentUser)) {
                return userService.getById(userId);
            }

            if (!currentUser.getId().equals(userId)) {
                throw new UnauthorizedException("Cannot " + operation + " another user's data");
            }
        }

        return currentUser;
    }
}
