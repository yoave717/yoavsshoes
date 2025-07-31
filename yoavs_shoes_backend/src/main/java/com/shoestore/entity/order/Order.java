package com.shoestore.entity.order;

import com.shoestore.entity.base.BaseEntity;
import com.shoestore.entity.user.User;
import com.shoestore.entity.user.UserAddress;
import com.shoestore.validation.ValidOrderNumber;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order entity representing customer orders
 */
@Entity
@Table(name = "orders")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"orderItems"})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Order extends BaseEntity {

    /**
     * The ID of the user who placed this order (always fetched)
     */
    @Column(name = "user_id", nullable = false)
    @NotNull(message = "User ID is required")
    private Long userId;

    /**
     * The user who placed this order (lazily loaded)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @ToString.Exclude
    private User user;

    /**
     * Unique 7-digit order number for tracking
     */
    @Column(name = "order_number", nullable = false, unique = true, length = 7)
    @NotBlank(message = "Order number is required")
    @ValidOrderNumber
    private String orderNumber;

    /**
     * Current status of the order
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @NotNull(message = "Order status is required")
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    /**
     * Total amount for the order
     */
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Total amount is required")
    @PositiveOrZero(message = "Total amount must be zero or positive")
    private BigDecimal totalAmount;

    /**
     * The ID of the shipping address for this order (always fetched)
     */
    @Column(name = "shipping_address_id", nullable = false)
    @NotNull(message = "Shipping address ID is required")
    private Long shippingAddressId;

    /**
     * Shipping address for the order
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_address_id", insertable = false, updatable = false)
    @ToString.Exclude
    private UserAddress shippingAddress;

    /**
     * Date when the order was placed
     */
    @Column(name = "order_date", nullable = false)
    @Builder.Default
    private LocalDateTime orderDate = LocalDateTime.now();

    /**
     * Date when the order was shipped (null if not yet shipped)
     */
    @Column(name = "shipped_date")
    private LocalDateTime shippedDate;

    /**
     * Date when the order was delivered (null if not yet delivered)
     */
    @Column(name = "delivered_date")
    private LocalDateTime deliveredDate;

    /**
     * Items in this order
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    /**
     * Calculate total amount from order items
     */
    public BigDecimal calculateTotalAmount() {
        if (getOrderItems() == null || getOrderItems().isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return getOrderItems().stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Update the total amount based on order items
     */
    public void updateTotalAmount() {
        this.totalAmount = calculateTotalAmount();
    }

    /**
     * Check if this order can be cancelled
     */
    public boolean canBeCancelled() {
        return status != null && status.canBeCancelled();
    }

    /**
     * Mark the order as shipped with current timestamp
     */
    public void markAsShipped() {
        this.status = OrderStatus.SHIPPED;
        this.shippedDate = LocalDateTime.now();
    }

    /**
     * Mark the order as delivered with current timestamp
     */
    public void markAsDelivered() {
        this.status = OrderStatus.DELIVERED;
        this.deliveredDate = LocalDateTime.now();
    }

    /**
     * Mark the order as cancelled
     */
    public void markAsCancelled() {
        if (!canBeCancelled()) {
            throw new IllegalStateException("Order cannot be cancelled in current status: " + status);
        }
        this.status = OrderStatus.CANCELLED;
    }

    /**
     * Confirm the order
     */
    public void confirm() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be confirmed");
        }
        this.status = OrderStatus.CONFIRMED;
    }

    /**
     * Start processing the order
     */
    public void startProcessing() {
        if (this.status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed orders can be processed");
        }
        this.status = OrderStatus.PROCESSING;
    }

    /**
     * Validate that the order number is in the correct 7-digit format
     */
    public void validateOrderNumber() {
        if (orderNumber == null || orderNumber.length() != 7) {
            throw new IllegalArgumentException("Order number must be exactly 7 digits");
        }
        
        try {
            int number = Integer.parseInt(orderNumber);
            if (number < 1000000 || number > 9999999) {
                throw new IllegalArgumentException("Order number must be between 1000000 and 9999999");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Order number must contain only digits");
        }
    }

    /**
     * Pre-persist validation
     */
    @PrePersist
    protected void validateBeforeSave() {
        if (orderNumber != null) {
            validateOrderNumber();
        }
    }
}
