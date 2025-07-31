package com.shoestore.controller.user;

import com.fasterxml.jackson.annotation.JsonView;
import com.shoestore.controller.base.CrudController;
import com.shoestore.dto.user.UserAddressDto;
import com.shoestore.dto.user.UserAddressMapper;
import com.shoestore.dto.view.Views;
import com.shoestore.entity.user.User;
import com.shoestore.entity.user.UserAddress;
import com.shoestore.security.annotation.AccessControl;
import com.shoestore.security.annotation.UserOwned;
import com.shoestore.service.user.UserAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing user addresses
 */
@RestController
@RequestMapping("/api/addresses")
@Slf4j
@Tag(name = "User Addresses", description = "User address management operations")
@UserOwned(userIdPath = "user.id")  // Configure how to extract user ID from UserAddress entities
public class UserAddressController extends CrudController<
        UserAddress,
        Long,
        UserAddressDto.CreateAddressDto,
        UserAddressDto.UpdateAddressDto,
        UserAddressDto,
        UserAddressMapper,
        UserAddressService
        
> {

    /**
     * Constructor
     */
    public UserAddressController(UserAddressService userAddressService, UserAddressMapper userAddressMapper) {
        super(userAddressService, "user address", userAddressMapper);
    }

    // ==============================================
    // 1. CRUD ENDPOINTS (Override Base Operations)
    // ==============================================

    /**
     * Get current user's addresses
     */
    @GetMapping("/my-addresses")
    @AccessControl(level = AccessControl.AccessLevel.AUTHENTICATED)
    @Operation(
        summary = "Get current user's addresses",
        description = "Retrieve all addresses for the authenticated user"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Addresses retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @JsonView(Views.Detailed.class)
    public ResponseEntity<StandardResponse<List<UserAddressDto>>> getMyAddresses(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)", example = "20")
            @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field", example = "id")
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        User currentUser = getCurrentUser();
        log.debug("Getting all addresses for user: {}", currentUser.getId());
        
        List<UserAddress> addresses = service.getUserAddresses(currentUser);
        // For simplicity, return all addresses without pagination since users typically have few addresses
        List<UserAddressDto> response = mapper.toDtoList(addresses);

        return success(response, "Addresses retrieved successfully");
    }

    /**
     * Get address by ID - accessible by address owner or admin
     */
    @Override
    @AccessControl(level = AccessControl.AccessLevel.OWNER_OR_ADMIN, entityType = UserAddress.class)
    public ResponseEntity<StandardResponse<UserAddressDto>> getById(Long id) {  
        return super.getById(id);
    }

    /**
     * Create new address for the current user
     */
    @Override
    @AccessControl(level = AccessControl.AccessLevel.AUTHENTICATED)
    public ResponseEntity<StandardResponse<UserAddressDto>> create(UserAddressDto.CreateAddressDto createDto) {
        User currentUser = getCurrentUser();
        log.debug("Creating new address for user: {}", currentUser.getId());
        
        UserAddress address = service.createAddress(currentUser, createDto);
        UserAddressDto addressDto = mapper.toDto(address);

        return created(addressDto, "/{id}", address.getId());
    }

    /**
     * Update existing address - accessible by address owner or admin
     * Uses user-aware service method that enforces ownership
     */
    @Override
    @AccessControl(level = AccessControl.AccessLevel.OWNER_OR_ADMIN, entityType = UserAddress.class)
    public ResponseEntity<StandardResponse<UserAddressDto>> update(Long id, UserAddressDto.UpdateAddressDto updateDto) {
        User currentUser = getCurrentUser();
        log.debug("Updating address {} for user: {}", id, currentUser.getId());
        
        // Access validation is now handled by @AccessControl annotation
        UserAddress address = service.updateAddress(currentUser, id, updateDto);
        UserAddressDto addressDto = mapper.toDto(address);
        return success(addressDto, "Address updated successfully");
    }

    /**
     * Delete address by ID - accessible by address owner or admin
     */
    @Override
    @AccessControl(level = AccessControl.AccessLevel.OWNER_OR_ADMIN, entityType = UserAddress.class)
    public ResponseEntity<Void> delete(Long id) {
        log.debug("Deleting address with id: {}", id);
        
        // Access validation is now handled by @AccessControl annotation
        return super.delete(id);
    }

    // ==============================================
    // 2. OTHER ENDPOINTS (Custom Business Logic)
    // ==============================================

    /**
     * Admin route: Get all addresses for a specific user
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user addresses (Admin only)", description = "Get all addresses for a specific user - Admin access required")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user addresses"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @AccessControl(level = AccessControl.AccessLevel.ADMIN_ONLY)
    public ResponseEntity<StandardResponse<List<UserAddressDto>>> getUserAddresses(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)", example = "20")
            @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field", example = "id")
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.debug("Admin getting all addresses for user: {}", userId);
        
        List<UserAddress> addresses = service.getUserAddresses(userId);
        List<UserAddressDto> addressDtos = addresses.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        
        return success(addressDtos, "Addresses retrieved successfully");
    }

    /**
     * Get default address for the current user
     */
    @GetMapping("/default")
    @AccessControl(level = AccessControl.AccessLevel.AUTHENTICATED)
    @Operation(summary = "Get default address", description = "Retrieve the default address for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved default address"),
            @ApiResponse(responseCode = "404", description = "No default address found"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<StandardResponse<UserAddressDto>> getDefaultAddress() {
        User currentUser = getCurrentUser();
        log.debug("GET /api/addresses/default for user: {}", currentUser.getId());
        
        Optional<UserAddress> address = service.getDefaultAddress(currentUser);
        
        if (address.isEmpty()) {
            log.debug("No default address found for user: {}", currentUser.getId());
            return ResponseEntity.notFound().build();
        }

        UserAddressDto addressDto = mapper.toDto(address.get());
        
        return success(addressDto, "Default address retrieved successfully");
    }

    /**
     * Set an address as default for the current user
     */
    @PutMapping("/{addressId}/default")
    @AccessControl(level = AccessControl.AccessLevel.OWNER_OR_ADMIN, entityIdParam = "addressId", entityType = UserAddress.class)
    @Operation(summary = "Set default address", description = "Set an address as the default address for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully set as default address"),
            @ApiResponse(responseCode = "404", description = "Address not found"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied - Can only set own addresses as default")
    })
    public ResponseEntity<StandardResponse<UserAddressDto>> setDefaultAddress(
            @Parameter(description = "Address ID", required = true)
            @PathVariable Long addressId) {
        User currentUser = getCurrentUser();
        log.debug("PUT /api/addresses/{}/default for user: {}", addressId, currentUser.getId());

        UserAddress address = service.setDefaultAddress(currentUser, addressId);
        UserAddressDto addressDto = mapper.toDto(address);
        return success(addressDto, "Default address updated successfully");
    }

    /**
     * Check if the current user has any addresses
     */
    @GetMapping("/exists")
    @AccessControl(level = AccessControl.AccessLevel.AUTHENTICATED)
    @Operation(summary = "Check if user has addresses", description = "Check if the current user has any addresses")
    @ApiResponse(responseCode = "200", description = "Check completed")
    public ResponseEntity<StandardResponse<Boolean>> hasAddresses() {
        User currentUser = getCurrentUser();
        log.debug("GET /api/addresses/exists for user: {}", currentUser.getId());
        
        boolean hasAddresses = service.hasAddresses(currentUser);
        return success(hasAddresses);
    }

    /**
     * Check if the current user has a default address
     */
    @GetMapping("/default/exists")
    @AccessControl(level = AccessControl.AccessLevel.AUTHENTICATED)
    @Operation(summary = "Check if user has default address", description = "Check if the current user has a default address")
    @ApiResponse(responseCode = "200", description = "Check completed")
    public ResponseEntity<StandardResponse<Boolean>> hasDefaultAddress() {
        User currentUser = getCurrentUser();
        log.debug("GET /api/addresses/default/exists for user: {}", currentUser.getId());
        
        boolean hasDefault = service.hasDefaultAddress(currentUser);
        return success(hasDefault);
    }

    // ==============================================
    // 3. OVERRIDDEN LOGIC (Abstract Methods Implementation)
    // ==============================================

    @Override
    protected String[] getAllowedSortFields() {
        return new String[]{"id", "createdAt", "updatedAt", "firstName", "lastName", "email", "phoneNumber", "addressLine1", "city", "state", "country", "isDefault", "label"};
    }

    @Override
    protected Class<UserAddress> getEntityClass() {
        return UserAddress.class;
    }

}
