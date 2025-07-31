package com.shoestore.entity.shoe;

import com.shoestore.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * ShoeInventory entity representing inventory for specific shoe model sizes
 */
@Entity
@Table(name = "shoe_inventory", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"shoe_model_id", "size"}))
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoeInventory extends BaseEntity {

    /**
     * Shoe model ID - always loaded
     */
    @Column(name = "shoe_model_id", nullable = false)
    @NotNull(message = "Shoe model ID is required")
    private Long shoeModelId;

    /**
     * Shoe model this inventory belongs to - lazily loaded
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shoe_model_id", insertable = false, updatable = false)
    @ToString.Exclude
    private ShoeModel shoeModel;

    /**
     * Size of the shoe
     */
    @Column(name = "size", nullable = false, length = 10)
    @NotBlank(message = "Size is required")
    @Size(max = 10, message = "Size cannot exceed 10 characters")
    private String size;

    /**
     * Available quantity
     */
    @Column(name = "quantity_available", nullable = false)
    @NotNull(message = "Available quantity is required")
    @Min(value = 0, message = "Available quantity must be non-negative")
    @Builder.Default
    private Integer quantityAvailable = 0;

    /**
     * Reserved quantity (for pending orders)
     */
    @Column(name = "quantity_reserved", nullable = false)
    @NotNull(message = "Reserved quantity is required")
    @Min(value = 0, message = "Reserved quantity must be non-negative")
    @Builder.Default
    private Integer quantityReserved = 0;

    /**
     * Check if this size is in stock
     */
    public boolean isInStock() {
        return quantityAvailable != null && quantityAvailable > 0;
    }

    /**
     * Check if this size is available (not reserved)
     */
    public boolean isAvailable() {
        return isInStock() && (quantityReserved == null || quantityAvailable > quantityReserved);
    }

    /**
     * Get available quantity (total - reserved)
     */
    public Integer getActualAvailableQuantity() {
        if (quantityAvailable == null) return 0;
        if (quantityReserved == null) return quantityAvailable;
        return Math.max(0, quantityAvailable - quantityReserved);
    }

    /**
     * Get total quantity
     */
    public Integer getTotalQuantity() {
        return quantityAvailable != null ? quantityAvailable : 0;
    }
}
