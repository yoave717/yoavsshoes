package com.shoestore.service.order;

import com.shoestore.dto.order.*;
import com.shoestore.entity.order.Order;
import com.shoestore.entity.order.OrderItem;
import com.shoestore.entity.order.OrderStatus;
import com.shoestore.entity.user.User;
import com.shoestore.entity.user.UserAddress;
import com.shoestore.exception.BadRequestException;
import com.shoestore.repository.order.OrderRepository;
import com.shoestore.service.base.BaseService;
import com.shoestore.service.shoe.ShoeInventoryService;
import com.shoestore.service.user.UserAddressService;
import com.shoestore.util.OrderNumberGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for managing orders
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class OrderService extends BaseService<Order, Long, OrderRepository> {

    private final OrderNumberGenerator orderNumberGenerator;
    private final ShoeInventoryService inventoryService;
    private final UserAddressService userAddressService;
    private final OrderItemService orderItemService;

    public OrderService(OrderRepository repository, 
                       OrderNumberGenerator orderNumberGenerator,
                       UserAddressService userAddressService,
                       ShoeInventoryService inventoryService,
                       OrderItemService orderItemService) {
        super(repository, "Order");
        this.orderNumberGenerator = orderNumberGenerator;
        this.userAddressService = userAddressService;
        this.inventoryService = inventoryService;
        this.orderItemService = orderItemService;
    }

    /**
     * Create a new order with items from cart/request
     */
    @Transactional
    public Order createOrder(User user, OrderDto.CreateOrderDto newOrder) {
        log.info("Creating order for user: {} with {} items", user.getEmail(), newOrder.getItems().size());
        
        UserAddress shippingAddress = userAddressService.getAddress(user, newOrder.getShippingAddressId());
        
        String orderNumber = orderNumberGenerator.generateOrderNumber();
        
        Order order = Order.builder()
                .userId(user.getId())
                .orderNumber(orderNumber)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .orderDate(LocalDateTime.now())
                .shippingAddressId(shippingAddress.getId())
                .build();
        
        Order savedOrder = repository.save(order);

        List<OrderItem> createdItems = orderItemService.createOrderItems(savedOrder.getId(), newOrder.getItems());
        
        BigDecimal totalAmount = orderItemService.calculateTotalAmount(createdItems);
        
        savedOrder.setTotalAmount(totalAmount);
        savedOrder = repository.save(savedOrder);   
        
        log.info("Created order {} with total amount: {}", orderNumber, savedOrder.getTotalAmount());

        return savedOrder;
    }

    /**
     * Get user's orders with pagination
     */
    public Page<Order> getUserOrders(Long userId, Pageable pageable) {
        log.debug("Getting orders for user: {}", userId);
        
        return repository.findByUserId(userId, pageable);
    }

    /**
     * Update order status
     */
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        log.info("Updating order {} status to {}", orderId, newStatus);

        Order order = getById(orderId);
        OrderStatus currentStatus = order.getStatus();
        
        // Validate status transition
        validateStatusTransition(currentStatus, newStatus);
        
        // Handle status-specific logic
        handleStatusChange(order, currentStatus, newStatus);
        
        order.setStatus(newStatus);
        Order updatedOrder = repository.save(order);
        
        log.info("Updated order {} status from {} to {}", orderId, currentStatus, newStatus);
        
        return updatedOrder;
    }

    /**
     * Get orders by status 
     */
    public Page<Order> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        log.debug("Getting orders with status: {}", status);

        return repository.findByStatus(status, pageable);
    }

    /**
     * Get order statistics
     */
    public Map<String, Object> getOrderStatistics() {
        log.debug("Getting order statistics");
        
        Map<String, Object> stats = new HashMap<>();
        
        // Total orders
        long totalOrders = repository.count();
        stats.put("totalOrders", totalOrders);
        
        // Orders by status
        for (OrderStatus status : OrderStatus.values()) {
            long count = repository.countByStatus(status);
            stats.put(status.name().toLowerCase() + "Orders", count);
        }
        
        // Total revenue (sum of all delivered orders)
        BigDecimal totalRevenue = repository.findAll().stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalRevenue", totalRevenue);
        
        // Pending orders specifically for quick access
        long pendingOrders = repository.countByStatus(OrderStatus.PENDING);
        stats.put("pendingOrders", pendingOrders);
        
        return stats;
    }

    private void validateStatusTransition(OrderStatus from, OrderStatus to) {
        // Define valid transitions
        switch (from) {
            case PENDING:
                if (to != OrderStatus.CONFIRMED && to != OrderStatus.CANCELLED) {
                    throw new BadRequestException("Invalid status transition from " + from + " to " + to);
                }
                break;
            case CONFIRMED:
                if (to != OrderStatus.PROCESSING && to != OrderStatus.CANCELLED) {
                    throw new BadRequestException("Invalid status transition from " + from + " to " + to);
                }
                break;
            case PROCESSING:
                if (to != OrderStatus.SHIPPED && to != OrderStatus.CANCELLED) {
                    throw new BadRequestException("Invalid status transition from " + from + " to " + to);
                }
                break;
            case SHIPPED:
                if (to != OrderStatus.DELIVERED) {
                    throw new BadRequestException("Invalid status transition from " + from + " to " + to);
                }
                break;
            case DELIVERED:
            case CANCELLED:
                throw new BadRequestException("Cannot change status from " + from);
        }
    }

    private void handleStatusChange(Order order, OrderStatus from, OrderStatus to) {
        switch (to) {
            case PENDING:
                // No special handling needed for pending
                break;
            case CONFIRMED:
                // Commit reserved inventory when order is confirmed
                for (OrderItem item : order.getOrderItems()) {
                    inventoryService.commitReservedInventory(
                            item.getShoeModel().getId(), 
                            item.getSize(), 
                            item.getQuantity()
                    );
                }
                order.confirm();
                log.info("Committed inventory for confirmed order {}", order.getOrderNumber());
                break;
            case SHIPPED:
                order.markAsShipped();
                break;
            case DELIVERED:
                order.markAsDelivered();
                break;
            case PROCESSING:
                order.startProcessing();
                break;
            case CANCELLED:
                // Release inventory when order is cancelled
                if (from == OrderStatus.PENDING) {
                    // If cancelling from PENDING, release reserved inventory
                    for (OrderItem item : order.getOrderItems()) {
                        inventoryService.releaseReservedInventory(
                                item.getShoeModel().getId(), 
                                item.getSize(), 
                                item.getQuantity()
                        );
                    }
                    log.info("Released reserved inventory for cancelled pending order {}", order.getOrderNumber());
                } else if (from == OrderStatus.CONFIRMED || from == OrderStatus.PROCESSING) {
                    // If cancelling from CONFIRMED/PROCESSING, return sold inventory back to available
                    for (OrderItem item : order.getOrderItems()) {
                        inventoryService.restoreInventory(
                                item.getShoeModel().getId(), 
                                item.getSize(), 
                                item.getQuantity()
                        );
                    }
                    log.info("Restored inventory for cancelled order {}", order.getOrderNumber());
                }
                order.markAsCancelled();
                break;
        }
    }

    @Override
    protected void updateEntityFields(Order existingEntity, Order newEntity) {
        // Orders are generally not updated directly, status changes are handled separately
        // This method is here to satisfy the base class requirement
    }

    @Override
    public Order update(Long id, Order entity) {
        // Orders are generally not updated directly, status changes are handled separately
        // This method is here to satisfy the base class requirement
        log.warn("Update operation is not applicable for Order entity. Use updateOrderStatus instead.");
        throw new BadRequestException("Update operation is not applicable for Order entity. Use updateOrderStatus instead.");
    }

    /**
     * Find address by ID with user relationship eagerly loaded
     * This method is used by the access control aspect to avoid lazy loading issues
     */
    public Optional<Order> findByIdWithUser(Long id) {
        log.debug("Finding address with user for access validation: {}", id);
        return repository.findByIdWithUser(id);
    }

    /**
     * Generic method for access control - delegates to findByIdWithUser
     * This follows the generic pattern expected by AccessControlAspect
     */
    public Optional<Order> findByIdWithOwner(Long id) {
        log.debug("Finding address with owner (user) for access validation: {}", id);
        return findByIdWithUser(id);
    }

}
