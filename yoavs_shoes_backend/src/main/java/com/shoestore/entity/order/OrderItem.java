package com.shoestore.entity.order;

import com.shoestore.entity.base.BaseEntity;
import com.shoestore.entity.shoe.ShoeModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

/**
 * OrderItem entity representing individual items within an order
 */
@Entity
@Table(name = "order_items")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem extends BaseEntity {

    /**
     * Order ID - always loaded
     */
    @Column(name = "order_id", nullable = false)
    @NotNull(message = "Order ID is required")
    private Long orderId;

    /**
     * The order this item belongs to - lazily loaded
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    @ToString.Exclude
    private Order order;

    /**
     * Shoe model ID - always loaded
     */
    @Column(name = "shoe_model_id", nullable = false)
    @NotNull(message = "Shoe model ID is required")
    private Long shoeModelId;

    /**
     * The shoe model being ordered - lazily loaded
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shoe_model_id", insertable = false, updatable = false)
    @ToString.Exclude
    private ShoeModel shoeModel;

    /**
     * Size of the shoe being ordered
     */
    @Column(name = "size", nullable = false, length = 10)
    @NotBlank(message = "Size is required")
    private String size;

    /**
     * Quantity of this item ordered
     */
    @Column(name = "quantity", nullable = false)
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    /**
     * Unit price at the time of order (price may change over time)
     */
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be positive")
    private BigDecimal unitPrice;

    /**
     * Total price for this line item (quantity * unit price)
     */
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Total price is required")
    @Positive(message = "Total price must be positive")
    private BigDecimal totalPrice;

    /**
     * Calculate and set total price based on quantity and unit price
     */
    public void calculateTotalPrice() {
        if (quantity != null && unitPrice != null) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    /**
     * Set quantity and recalculate total price
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateTotalPrice();
    }

    /**
     * Set unit price and recalculate total price
     */
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateTotalPrice();
    }

    /**
     * Pre-persist callback to ensure total price is calculated
     */
    @PrePersist
    @PreUpdate
    protected void calculateTotalPriceBeforeSave() {
        calculateTotalPrice();
    }

}
