package com.shoestore.controller.shoe;

import com.fasterxml.jackson.annotation.JsonView;
import com.shoestore.controller.base.CrudController;
import com.shoestore.dto.base.PageResponse;
import com.shoestore.dto.shoe.ShoeInventoryDto;
import com.shoestore.dto.shoe.ShoeInventoryMapper;
import com.shoestore.dto.view.Views;
import com.shoestore.entity.shoe.ShoeInventory;
import com.shoestore.security.annotation.AccessControl;
import com.shoestore.service.shoe.ShoeInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST Controller for shoe inventory operations
 */
@RestController
@RequestMapping("/api/inventory")
@Slf4j
@Tag(name = "Shoe Inventory", description = "Shoe inventory management operations")
public class ShoeInventoryController extends CrudController<
        ShoeInventory,
        Long,
        ShoeInventoryDto.CreateShoeInventoryDto,
        ShoeInventoryDto.UpdateShoeInventoryDto,
        ShoeInventoryDto,
        ShoeInventoryMapper,
        ShoeInventoryService> {

    private final ShoeInventoryService inventoryService;

    public ShoeInventoryController(ShoeInventoryMapper mapper, ShoeInventoryService service) {
        super(service, "Shoe Inventory", mapper);
        this.inventoryService = service;
    }

    /**
     * Get inventory by shoe model and size
     */
    @GetMapping("/shoe-model/{shoeModelId}/size/{size}")
    @AccessControl(level = AccessControl.AccessLevel.PUBLIC)
    @Operation(
        summary = "Get inventory by shoe model and size",
        description = "Retrieve inventory information for a specific shoe model and size"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inventory retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Inventory not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<StandardResponse<ShoeInventoryDto>> getInventoryByShoeModelAndSize(
            @Parameter(description = "Shoe model ID") @PathVariable Long shoeModelId,
            @Parameter(description = "Shoe size") @PathVariable String size) {
        
        log.debug("Getting inventory for shoe model {} and size {}", shoeModelId, size);

        ShoeInventory inventory = inventoryService.getInventoryByShoeModelAndSize(shoeModelId, size);

        return success(convertToDto(inventory), "Inventory retrieved successfully");
    }

    /**
     * Get all inventory for a shoe model
     */
    @GetMapping("/shoe-model/{shoeModelId}")
    @AccessControl(level = AccessControl.AccessLevel.PUBLIC)
    @Operation(
        summary = "Get all inventory for a shoe model",
        description = "Retrieve all inventory entries for a specific shoe model"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inventory retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Shoe model not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<StandardResponse<List<ShoeInventoryDto>>> getInventoryByShoeModel(
            @Parameter(description = "Shoe model ID") @PathVariable Long shoeModelId) {
        
        log.debug("Getting all inventory for shoe model {}", shoeModelId);
        
        List<ShoeInventory> inventory = inventoryService.getInventoryByShoeModel(shoeModelId);

        return success(convertToDtoList(inventory), "Inventory retrieved successfully");
    }

    /**
     * Get available inventory for a shoe model
     */
    @GetMapping("/shoe-model/{shoeModelId}/available")
    @AccessControl(level = AccessControl.AccessLevel.PUBLIC)
    @Operation(
        summary = "Get available inventory for a shoe model",
        description = "Retrieve only available (non-zero quantity) inventory entries for a specific shoe model"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Available inventory retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Shoe model not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<StandardResponse<List<ShoeInventoryDto>>> getAvailableInventoryByShoeModel(
            @Parameter(description = "Shoe model ID") @PathVariable Long shoeModelId) {
        
        log.debug("Getting available inventory for shoe model {}", shoeModelId);
        
        List<ShoeInventory> inventory = inventoryService.getAvailableInventoryByShoeModel(shoeModelId);

        return success(convertToDtoList(inventory), "Available inventory retrieved successfully");
    }

    /**
     * Check if inventory is available
     */
    @GetMapping("/check")
    @AccessControl(level = AccessControl.AccessLevel.PUBLIC)
    @Operation(
        summary = "Check inventory availability",
        description = "Check if a specific quantity is available for a shoe model and size"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Availability check completed"),
        @ApiResponse(responseCode = "400", description = "Invalid parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<StandardResponse<Boolean>> checkInventoryAvailability(
            @Parameter(description = "Shoe model ID") @RequestParam Long shoeModelId,
            @Parameter(description = "Shoe size") @RequestParam String size,
            @Parameter(description = "Quantity to check") @RequestParam Integer quantity) {
        
        log.debug("Checking availability for shoe model {}, size {}, quantity {}", shoeModelId, size, quantity);
        
        boolean available = inventoryService.isInventoryAvailable(shoeModelId, size, quantity);

        return success(available, "Availability check completed");
    }

    // ========================================
    // ADMIN ENDPOINTS
    // ========================================

    /**
     * Get all inventory with pagination (admin only)
     */
    @AccessControl(level = AccessControl.AccessLevel.ADMIN_ONLY)
    @Operation(
        summary = "Get all inventory (Admin only)",
        description = "Retrieve paginated list of all inventory entries in the system"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inventory retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - admin access required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Override
    public ResponseEntity<StandardResponse<PageResponse<ShoeInventoryDto>>> getAll(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,

            @Parameter(description = "Page size (max 100)", example = "20")
            @RequestParam(defaultValue = "20") Integer size,

            @Parameter(description = "Sort field", example = "id")
            @RequestParam(required = false) String sortBy,

            @Parameter(description = "Sort direction (asc/desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.debug("Getting all inventory for admin");

        return super.getAll(page, size, sortBy, sortDir);
    }


    /**
     * Create new inventory entry (admin only)
     */
    @AccessControl(level = AccessControl.AccessLevel.ADMIN_ONLY)
    @Operation(
        summary = "Create inventory entry (Admin only)",
        description = "Create a new inventory entry for a shoe model and size"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Inventory created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid inventory data or duplicate entry"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - admin access required"),
        @ApiResponse(responseCode = "404", description = "Shoe model not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Override
    public ResponseEntity<StandardResponse<ShoeInventoryDto>> create(
            @Valid @RequestBody ShoeInventoryDto.CreateShoeInventoryDto newInventory) {

        return super.create(newInventory);
    }

    /**
     * Update inventory entry (admin only)
     */
    @AccessControl(level = AccessControl.AccessLevel.ADMIN_ONLY)
    @Operation(
        summary = "Update inventory entry (Admin only)",
        description = "Update quantities for an existing inventory entry"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inventory updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid inventory data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - admin access required"),
        @ApiResponse(responseCode = "404", description = "Inventory not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Override
    public ResponseEntity<StandardResponse<ShoeInventoryDto>> update(
            @Parameter(description = "Inventory ID") @PathVariable Long inventoryId,
            @Valid @RequestBody ShoeInventoryDto.UpdateShoeInventoryDto request) {
        
        return super.update(inventoryId, request);
    }

    /**
     * Update inventory entry by shoe model and size (admin only)
     */
    @AccessControl(level = AccessControl.AccessLevel.ADMIN_ONLY)
    @Operation(
        summary = "Update inventory by shoe model and size (Admin only)",
        description = "Update quantities for an inventory entry identified by shoe model ID and size"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inventory updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid inventory data or duplicate entry"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - admin access required"),
        @ApiResponse(responseCode = "404", description = "Shoe model or inventory not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/model/{shoeModelId}/size/{size}")
    @JsonView(Views.Detailed.class)
    public ResponseEntity<StandardResponse<ShoeInventoryDto>> updateByShoeModelAndSize(
            @Parameter(description = "Shoe model ID") @PathVariable Long shoeModelId,
            @Parameter(description = "Shoe size") @PathVariable String size,
            @Valid @RequestBody ShoeInventoryDto.UpdateShoeInventoryDto request) {

        ShoeInventoryDto updatedInventory = convertToDto(service.updateByShoeModelAndSize(shoeModelId, size, request));

        return success(updatedInventory);
    }

    @Override
    protected String[] getAllowedSortFields() {
        return new String[]{"id", "shoeModel", "size", "color", "quantity", "reservedQuantity"};
    }

    @Override
    protected Class<ShoeInventory> getEntityClass() {
        return ShoeInventory.class;
    }
}
