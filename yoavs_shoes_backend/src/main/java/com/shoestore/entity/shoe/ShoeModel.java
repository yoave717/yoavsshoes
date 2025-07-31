package com.shoestore.entity.shoe;

import com.shoestore.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * ShoeModel entity representing specific shoe variants (color, material combinations)
 */
@Entity
@Table(name = "shoe_models")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoeModel extends BaseEntity {

    /**
     * Parent shoe this model belongs to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shoe_id", nullable = false)
    @NotNull(message = "Shoe is required")
    @ToString.Exclude
    private Shoe shoe;

    /**
     * Model name/variant name
     */
    @Column(name = "model_name", nullable = false, length = 255)
    @NotBlank(message = "Model name is required")
    @Size(max = 255, message = "Model name cannot exceed 255 characters")
    private String modelName;

    /**
     * Color of this shoe model
     */
    @Column(name = "color", nullable = false, length = 50)
    @NotBlank(message = "Color is required")
    @Size(max = 50, message = "Color cannot exceed 50 characters")
    private String color;

    /**
     * Material of this shoe model
     */
    @Column(name = "material", length = 100)
    @Size(max = 100, message = "Material cannot exceed 100 characters")
    private String material;

    /**
     * Unique SKU for this model
     */
    @Column(name = "sku", nullable = false, unique = true, length = 100)
    @NotBlank(message = "SKU is required")
    @Size(max = 100, message = "SKU cannot exceed 100 characters")
    private String sku;

    /**
     * Price of this specific model
     */
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be non-negative")
    private BigDecimal price;

    /**
     * Image URL for this model
     */
    @Column(name = "image_url", length = 255)
    private String imageUrl;

    /**
     * Flag indicating if model is active
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Available sizes for this model
     */
    @OneToMany(mappedBy = "shoeModel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<ShoeInventory> availableSizes = new ArrayList<>();

    /**
     * Get the display name for the model
     */
    public String getDisplayName() {
        return modelName + " - " + color;
    }

    /**
     * Get full display name including parent shoe
     */
    public String getFullDisplayName() {
        String shoeName = (shoe != null) ? shoe.getDisplayName() : "Unknown Shoe";
        return shoeName + " " + modelName + " - " + color;
    }

    /**
     * Check if model is currently active
     */
    public boolean isCurrentlyActive() {
        return Boolean.TRUE.equals(isActive);
    }

    /**
     * Get color and material description
     */
    public String getColorMaterialDescription() {
        if (material != null && !material.trim().isEmpty()) {
            return color + " (" + material + ")";
        }
        return color;
    }
}
