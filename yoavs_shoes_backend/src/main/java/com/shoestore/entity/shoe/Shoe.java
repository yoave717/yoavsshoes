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
 * Shoe entity representing base shoe models in the system
 */
@Entity
@Table(name = "shoes")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shoe extends BaseEntity {

    /**
     * Gender enum for shoes
     */
    public enum Gender {
        MEN, WOMEN, UNISEX
    }

    /**
     * Brand of the shoe
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id", nullable = false)
    @NotNull(message = "Brand is required")
    @ToString.Exclude
    private Brand brand;

    /**
     * Category of the shoe
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Category is required")
    @ToString.Exclude
    private ShoeCategory category;

    /**
     * Shoe name
     */
    @Column(name = "name", nullable = false, length = 255)
    @NotBlank(message = "Shoe name is required")
    @Size(max = 255, message = "Shoe name cannot exceed 255 characters")
    private String name;

    /**
     * Shoe description
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Target gender for the shoe
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 20)
    @NotNull(message = "Gender is required")
    private Gender gender;

    /**
     * Base price of the shoe
     */
    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Base price must be non-negative")
    private BigDecimal basePrice;

    /**
     * Flag indicating if shoe is active
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Shoe models/variants associated with this shoe
     */
    @OneToMany(mappedBy = "shoe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<ShoeModel> shoeModels = new ArrayList<>();

    /**
     * Get the display name for the shoe
     */
    public String getDisplayName() {
        return name;
    }

    /**
     * Check if shoe is currently active
     */
    public boolean isCurrentlyActive() {
        return Boolean.TRUE.equals(isActive);
    }

}
