package com.shoestore.entity.shoe;

import com.shoestore.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Brand entity representing shoe brands in the system
 */
@Entity
@Table(name = "brands")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand extends BaseEntity {

    /**
     * Brand name - must be unique
     */
    @Column(name = "name", nullable = false, unique = true, length = 100)
    @NotBlank(message = "Brand name is required")
    @Size(max = 100, message = "Brand name cannot exceed 100 characters")
    private String name;

    /**
     * Brand description
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Brand logo URL
     */
    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    /**
     * Flag indicating if brand is active
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Shoes associated with this brand
     */
    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<Shoe> shoes = new ArrayList<>();

    /**
     * Get the display name for the brand
     */
    public String getDisplayName() {
        return name;
    }

    /**
     * Check if brand is currently active
     */
    public boolean isCurrentlyActive() {
        return Boolean.TRUE.equals(isActive);
    }
}
