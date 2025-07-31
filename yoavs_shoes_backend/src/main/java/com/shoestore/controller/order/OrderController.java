package com.shoestore.controller.order;

import com.shoestore.controller.base.CrudController;
import com.shoestore.dto.base.PageResponse;
import com.shoestore.dto.order.OrderDto;
import com.shoestore.dto.order.OrderMapper;
import com.shoestore.dto.view.Views;
import com.shoestore.entity.order.Order;
import com.shoestore.entity.order.OrderStatus;
import com.shoestore.entity.user.User;
import com.shoestore.security.annotation.AccessControl;
import com.shoestore.security.annotation.UserOwned;
import com.shoestore.service.order.OrderService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@Slf4j
@Tag(name = "Orders", description = "Order management operations")
@CrossOrigin(origins = "*", maxAge = 3600)
@UserOwned(userIdPath = "user.id")  // Configure how to extract user ID from Order entities
public class OrderController extends CrudController<
        Order,
        Long,
        OrderDto.CreateOrderDto,
        OrderDto.UpdateOrderDto,
        OrderDto,
        OrderMapper,
        OrderService
> {

    /**
     * Constructor
     */
    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        super(orderService, "order", orderMapper);
    }

    // ==============================================
    // 1. CRUD ENDPOINTS (Override Base Operations)
    // ==============================================


    /**
     * Create new order for the current user
     */
    @Override
    @JsonView(Views.Detailed.class)
    @AccessControl(level = AccessControl.AccessLevel.AUTHENTICATED)
    public ResponseEntity<StandardResponse<OrderDto>> create(OrderDto.CreateOrderDto createDto) {
        User currentUser = getCurrentUser();
        log.info("Creating new order for user: {}", currentUser.getId());
        
        // Validate that user can only create orders for themselves, unless they are admin
        if (!currentUser.getId().equals(createDto.getUserId()) && !isCurrentUserAdmin()) {
            log.warn("User {} attempted to create order for user {}", currentUser.getId(), createDto.getUserId());
            return ResponseEntity.badRequest()
                    .body(StandardResponse.error("You can only create orders for yourself"));
        }
        
        Order order = service.createOrder(currentUser, createDto);
        OrderDto orderDto = convertToDto(order); // Convert to DTO
        return created(orderDto, "/{id}", order.getId());
    }

    /**
     * Update existing order - not typically allowed for orders
     * Override to prevent updates
     */
    @Override
    public ResponseEntity<StandardResponse<OrderDto>> update(Long id, OrderDto.UpdateOrderDto updateDto) {
        log.warn("Attempt to update order {} - updates not allowed", id);
        return ResponseEntity.badRequest()
                .body(StandardResponse.error("Orders cannot be updated directly. Use status change endpoints instead."));
    }

    /**
     * Delete order by ID - not typically allowed, use cancel instead
     */
    @Override
    public ResponseEntity<Void> delete(Long id) {
        log.warn("Attempt to delete order {} - deletion not allowed", id);
        return ResponseEntity.badRequest().build();
    }

    // ==============================================
    // 2. ORDER-SPECIFIC ENDPOINTS
    // ==============================================

    /**
     * Get current user's orders
     */
    @GetMapping("/my-orders")
    @JsonView(Views.Detailed.class)
    @AccessControl(level = AccessControl.AccessLevel.AUTHENTICATED)
    @Operation(
        summary = "Get current user's orders",
        description = "Retrieve paginated list of orders for the authenticated user"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<StandardResponse<PageResponse<OrderDto>>> getMyOrders(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)", example = "20")
            @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field", example = "id")
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        User currentUser = getCurrentUser();
        log.debug("Getting all orders for user: {}", currentUser.getId());
        
        // Create pageable and get user orders
        var pageable = createPageable(page, size, sortBy, sortDir);
        Page<Order> orders = service.getUserOrders(currentUser.getId(), pageable);

        PageResponse<OrderDto> pageResponse = new PageResponse<>(orders.map(this::convertToDto));

        return success(pageResponse);
    }

    /**
     * Cancel an order
     */
    @PutMapping("/{orderId}/cancel")
    @JsonView(Views.Detailed.class)
    @AccessControl(level = AccessControl.AccessLevel.OWNER_OR_ADMIN, entityType = Order.class, entityIdParam = "orderId")
    @Operation(
        summary = "Cancel an order",
        description = "Cancel a pending order for the authenticated user"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Order cannot be cancelled"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - not owner of the order"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<StandardResponse<OrderDto>> cancelOrder(
            @Parameter(description = "Order ID") @PathVariable Long orderId) {

        log.info("Cancelling order {}", orderId);

        Order cancelledOrder = service.updateOrderStatus(orderId, OrderStatus.CANCELLED);

        return success(convertToDto(cancelledOrder), "Order cancelled successfully");
    }

    // ==============================================
    // 3. ADMIN ENDPOINTS
    // ==============================================

    /**
     * Admin route: Get all orders for a specific user
     */
    @GetMapping("/user/{userId}")
    @JsonView(Views.Admin.class)
    @Operation(summary = "Get user orders (Admin only)", description = "Get all orders for a specific user - Admin access required")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user orders"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @AccessControl(level = AccessControl.AccessLevel.ADMIN_ONLY)
    public ResponseEntity<StandardResponse<PageResponse<OrderDto>>> getUserOrders(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)", example = "20")
            @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field", example = "id")
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc")
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.debug("Admin getting all orders for user: {}", userId);
        
        var pageable = createPageable(page, size, sortBy, sortDir);
        Page<Order> orders = service.getUserOrders(userId, pageable);
        PageResponse<OrderDto> pageResponse = new PageResponse<>(orders.map(this::convertToDto));

        return success(pageResponse);
    }

    /**
     * Admin route: Get all orders
     */
    @JsonView(Views.Admin.class)
    @Operation(summary = "Get all orders (Admin only)", description = "Get all orders with pagination - Admin access required")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all orders"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @AccessControl(level = AccessControl.AccessLevel.ADMIN_ONLY)
    @Override
    public ResponseEntity<StandardResponse<PageResponse<OrderDto>>> getAll(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)", example = "20")
            @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field", example = "id")
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        
        return super.getAll(page, size, sortBy, sortDir);
    }

    /**
     * Get orders by status (admin only) - updated endpoint path
     */
    @GetMapping("/status/{status}")
    @JsonView(Views.Admin.class)
    @AccessControl(level = AccessControl.AccessLevel.ADMIN_ONLY)
    @Operation(
        summary = "Get orders by status (Admin only)",
        description = "Retrieve paginated list of orders filtered by status"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - admin access required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<StandardResponse<PageResponse<OrderDto>>> getOrdersByStatusAdmin(
            @Parameter(description = "Order status") @PathVariable OrderStatus status,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)", example = "20")
            @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field", example = "id")
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.debug("Admin getting orders with status: {}", status);
        
        var pageable = createPageable(page, size, sortBy, sortDir);
        Page<Order> orders = service.getOrdersByStatus(status, pageable);
        PageResponse<OrderDto> pageResponse = new PageResponse<>(orders.map(this::convertToDto));

        return success(pageResponse);
    }

    /**
     * Admin route: Get order statistics
     */
    @GetMapping("/stats")
    @JsonView(Views.Admin.class)
    @Operation(summary = "Get order statistics (Admin only)", description = "Get order statistics - Admin access required")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved order statistics"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @AccessControl(level = AccessControl.AccessLevel.ADMIN_ONLY)
    public ResponseEntity<StandardResponse<Map<String, Object>>> getOrderStats() {
        log.debug("Admin getting order statistics");
        
        Map<String, Object> stats = service.getOrderStatistics();
        
        return success(stats, "Order statistics retrieved successfully");
    }

    /**
     * Update order status (admin only)
     */
    @PutMapping("/{orderId}/status")
    @JsonView(Views.Admin.class)
    @AccessControl(level = AccessControl.AccessLevel.ADMIN_ONLY)
    @Operation(
        summary = "Update order status (Admin only)",
        description = "Update the status of an order"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status transition"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - admin access required"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<StandardResponse<OrderDto>> updateOrderStatus(
            @Parameter(description = "Order ID") @PathVariable Long orderId,
            @Parameter(description = "New order status") @RequestParam OrderStatus status) {
        
        log.info("Updating order {} status to: {}", orderId, status);

        Order updatedOrder = service.updateOrderStatus(orderId, status);

        return success(convertToDto(updatedOrder), "Order status updated successfully");
    }

    /**
     * Process order (admin only)
     */
    @PutMapping("/{orderId}/process")
    @JsonView(Views.Admin.class)
    @AccessControl(level = AccessControl.AccessLevel.ADMIN_ONLY)
    @Operation(
        summary = "Process order (Admin only)",
        description = "Process a pending order, moving it to confirmed status"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order processed successfully"),
        @ApiResponse(responseCode = "400", description = "Order cannot be processed"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - admin access required"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<StandardResponse<OrderDto>> processOrder(
            @Parameter(description = "Order ID") @PathVariable Long orderId) {
        
        log.info("Processing order: {}", orderId);

        Order processedOrder = service.updateOrderStatus(orderId, OrderStatus.CONFIRMED);

        return success(convertToDto(processedOrder), "Order processed successfully");
    }

    // ==============================================
    // 4. CUSTOM ENDPOINTS
    // ==============================================

    /**
     * Get order by ID with full details (override base method)
     */
    @Override
    @GetMapping("/{id}")
    @JsonView(Views.Detailed.class)
    @AccessControl(level = AccessControl.AccessLevel.OWNER_OR_ADMIN, entityType = Order.class, entityIdParam = "id")
    @Operation(
        summary = "Get order by ID with full details",
        description = "Retrieve a specific order with all details including items and shipping address"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - not owner of the order"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<StandardResponse<OrderDto>> getById(
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long id) {

        return super.getById(id);
    }

    // ==============================================
    // 5. HELPER METHODS (Override from base if needed)
    // ==============================================

    @Override
    protected String[] getAllowedSortFields() {
        return new String[]{"id", "orderNumber", "status", "totalAmount", "orderDate", "createdAt", "updatedAt"};
    }

    @Override
    protected Class<Order> getEntityClass() {
        return Order.class;
    }


}