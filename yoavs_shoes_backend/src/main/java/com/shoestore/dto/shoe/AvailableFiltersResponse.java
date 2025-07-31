package com.shoestore.dto.shoe;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for available filter options
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Available filter options for product listing")
public class AvailableFiltersResponse {

    @Schema(description = "Available brands")
    private List<BrandInfo> brands;

    @Schema(description = "Available categories")
    private List<CategoryInfo> categories;

    @Schema(description = "Available colors")
    private List<String> colors;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Brand information")
    public static class BrandInfo {
        @Schema(description = "Brand ID", example = "1")
        private Long id;

        @Schema(description = "Brand name", example = "Nike")
        private String name;

        @Schema(description = "Number of products", example = "15")
        private Long productCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Category information")
    public static class CategoryInfo {
        @Schema(description = "Category ID", example = "1")
        private Long id;

        @Schema(description = "Category name", example = "Running Shoes")
        private String name;

        @Schema(description = "Number of products", example = "8")
        private Long productCount;
    }
}
