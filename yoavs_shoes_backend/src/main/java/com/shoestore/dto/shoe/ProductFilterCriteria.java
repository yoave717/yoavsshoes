package com.shoestore.dto.shoe;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


/**
 * DTO for product listing filter criteria
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product listing filter criteria")
public class ProductFilterCriteria {

    @Schema(description = "Brand IDs to filter by", example = "[1, 2, 3]")
    private List<Long> brandIds;

    @Schema(description = "Category IDs to filter by", example = "[1, 2]")
    private List<Long> categoryIds;

    @Schema(description = "Colors to filter by", example = "[\"White\", \"Black\"]")
    private List<String> colors;

    @Schema(description = "Sizes to filter by", example = "[\"42\", \"43\", \"44\"]")
    private List<String> sizes;

    @Schema(description = "Gender to filter by", example = "UNISEX")
    private String gender;

    @Schema(description = "Minimum price", example = "50.0")
    private BigDecimal minPrice;

    @Schema(description = "Maximum price", example = "500.0")
    private BigDecimal maxPrice;

    @Schema(description = "Search term for name/model", example = "Air Max")
    private String searchTerm;

    @Schema(description = "Show only products in stock", example = "true")
    private Boolean inStock;

    @Schema(description = "Show only active products", example = "true")
    @Builder.Default
    private Boolean activeOnly = true;

    @Schema(description = "Sort field", example = "price", allowableValues = {"name", "price", "brand", "createdAt"})
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
