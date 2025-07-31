package com.shoestore.controller.user;

import com.shoestore.controller.base.CrudController;
import com.shoestore.dto.base.PageResponse;
import com.shoestore.dto.user.UserDto;
import com.shoestore.dto.user.UserMapper;
import com.shoestore.entity.user.User;
import com.shoestore.security.annotation.AccessControl;
import com.shoestore.service.user.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for user management (Admin only)
 */
@RestController
@RequestMapping("/api/users")
@Slf4j
@Tag(name = "User Management", description = "Admin user management operations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController extends CrudController<
        User,
        Long,
        UserDto.CreateUserDto,
        UserDto.UpdateUserDto,
        UserDto,
        UserMapper,
        UserService> {

    public UserController(UserMapper mapper, UserService service) {
        super(service, "User", mapper);
    }

    /**
     * Admin route: Get all users with pagination
     */
    @GetMapping("/all")
    @AccessControl(level = AccessControl.AccessLevel.ADMIN_ONLY)
    @Operation(summary = "Get all users (Admin only)", description = "Get all users with pagination - Admin access required")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<StandardResponse<PageResponse<UserDto>>> getAllUsers(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("Admin getting all users: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);

        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<User> userPage = userService.findAll(pageable);
        PageResponse<UserDto> response = convertToPageResponse(userPage);
        
        return success(response, "Users retrieved successfully");
    }

    /**
     * Admin route: Search users with filters
     */
    @GetMapping("/search")
    @AccessControl(level = AccessControl.AccessLevel.ADMIN_ONLY)
    @Operation(summary = "Search users (Admin only)", description = "Search users with various filters - Admin access required")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved search results"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<StandardResponse<PageResponse<UserDto>>> searchUsers(
            @Parameter(description = "Email filter (partial match)")
            @RequestParam(required = false) String email,
            @Parameter(description = "First name filter (partial match)")
            @RequestParam(required = false) String firstName,
            @Parameter(description = "Last name filter (partial match)")
            @RequestParam(required = false) String lastName,
            @Parameter(description = "Filter by admin status")
            @RequestParam(required = false) Boolean isAdmin,
            @Parameter(description = "Filter by active status")
            @RequestParam(required = false) Boolean isActive,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") Integer size) {

        log.debug("Admin searching users with filters: email={}, firstName={}, lastName={}, isAdmin={}, isActive={}", 
                email, firstName, lastName, isAdmin, isActive);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        
        Page<User> userPage = userService.searchUsers(email, firstName, lastName, isAdmin, isActive, pageable);
        PageResponse<UserDto> response = convertToPageResponse(userPage);
        
        return success(response, "Search completed successfully");
    }

    /**
     * Admin route: Get user statistics
     */
    @GetMapping("/stats")
    @AccessControl(level = AccessControl.AccessLevel.ADMIN_ONLY)
    @Operation(summary = "Get user statistics (Admin only)", description = "Get user statistics for admin dashboard - Admin access required")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<StandardResponse<Map<String, Object>>> getUserStatistics() {
        log.debug("Admin getting user statistics");

        Map<String, Object> stats = userService.getUserStatistics();
        return success(stats, "User statistics retrieved successfully");
    }

    @Override
    protected String[] getAllowedSortFields() {
        return new String[]{"email", "firstName", "lastName", "isAdmin", "isActive"};
    }

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }


}