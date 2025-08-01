package com.shoestore.dto.shoe;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

import com.shoestore.entity.shoe.Shoe;

/**
 * DTO for shoe filter criteria
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Shoe filter criteria")
public class ShoeFilterCriteria {

    @Schema(description = "Brand IDs to filter by", example = "[1, 2, 3]")
    private List<Long> brandIds;

    @Schema(description = "Category IDs to filter by", example = "[1, 2]")
    private List<Long> categoryIds;

    @Schema(description = "Search term for shoe name or brand", example = "Air Max")
    private String searchTerm;

    @Schema(description = "Minimum base price", example = "50.0")
    private BigDecimal minPrice;

    @Schema(description = "Maximum base price", example = "500.0")
    private BigDecimal maxPrice;

    @Schema(description = "Gender to filter by", example = "MALE")
    private Shoe.Gender gender;

    @Schema(description = "Show only active shoes", example = "true")
    @Builder.Default
    private Boolean activeOnly = true;

    @Schema(description = "Sort field", example = "name", allowableValues = {"name", "basePrice", "brand", "createdAt"})
    @Builder.Default
    private String sortBy = "name";

    @Schema(description = "Sort direction", example = "ASC", allowableValues = {"ASC", "DESC"})
    @Builder.Default
    private String sortDirection = "ASC";

    @Schema(description = "Page number (0-based)", example = "0")
    @Builder.Default
    private Integer page = 0;

    @Schema(description = "Page size", example = "20")
    @Builder.Default
    private Integer size = 20;
}
