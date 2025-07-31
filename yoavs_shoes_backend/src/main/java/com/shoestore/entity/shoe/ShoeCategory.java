package com.shoestore.entity.shoe;

import com.shoestore.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ShoeCategory entity representing shoe categories in the system
 */
@Entity
@Table(name = "shoe_categories")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoeCategory extends BaseEntity {

    /**
     * Category name - must be unique
     */
    @Column(name = "name", nullable = false, unique = true, length = 100)
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name cannot exceed 100 characters")
    private String name;

    /**
     * Category description
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Flag indicating if category is active
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Shoes associated with this category
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<Shoe> shoes = new ArrayList<>();

    /**
     * Get the display name for the category
     */
    public String getDisplayName() {
        return name;
    }

    /**
     * Check if category is currently active
     */
    public boolean isCurrentlyActive() {
        return Boolean.TRUE.equals(isActive);
    }
}
