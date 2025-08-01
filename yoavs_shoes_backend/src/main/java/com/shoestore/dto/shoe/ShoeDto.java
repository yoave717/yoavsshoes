package com.shoestore.dto.shoe;

import com.fasterxml.jackson.annotation.JsonView;
import com.shoestore.dto.base.BaseCrudDto;
import com.shoestore.dto.view.Views;
import com.shoestore.entity.shoe.Shoe;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ShoeDto extends BaseCrudDto {

    @JsonView(Views.Summary.class)
    private String name;

    @JsonView(Views.Summary.class)
    private BigDecimal basePrice;

    @JsonView(Views.Summary.class)
    private Shoe.Gender gender;

    @JsonView(Views.Summary.class)
    private BrandDto brand;

    @JsonView(Views.Summary.class)
    private ShoeCategoryDto category;

    @JsonView(Views.Detailed.class)
    private List<ShoeModelDto> models;

    @JsonView(Views.Summary.class)
    private Boolean isActive;

    /**
     * DTO for creating shoes
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Shoe creation DTO")
    @EqualsAndHashCode(callSuper = true)
    public static class CreateShoeDto extends BaseCrudDto.CreateDto {

        @Schema(description = "Shoe name", required = true, example = "Air Max 90")
        @NotBlank(message = "Shoe name is required")
        @Size(max = 255, message = "Shoe name cannot exceed 255 characters")
        private String name;

        @Schema(description = "Shoe description", example = "Classic running shoe with air cushioning")
        private String description;

        @Schema(description = "Base price", required = true, example = "149.99")
        @NotNull(message = "Base price is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Base price must be non-negative")
        private BigDecimal basePrice;

        @Schema(description = "Gender", required = true, example = "UNISEX")
        @NotBlank(message = "Gender is required")
        private Shoe.Gender gender;

        @Schema(description = "Brand ID", required = true, example = "1")
        @NotNull(message = "Brand ID is required")
        private Long brandId;

        @Schema(description = "Category ID", required = true, example = "1")
        @NotNull(message = "Category ID is required")
        private Long categoryId;

        @Schema(description = "Is active", example = "true")
        private Boolean isActive;
    }

    /**
     * DTO for updating shoes
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Shoe update DTO")
    @EqualsAndHashCode(callSuper = true)
    public static class UpdateShoeDto extends BaseCrudDto.UpdateDto {

        @Schema(description = "Shoe name", example = "Air Max 90")
        @Size(max = 255, message = "Shoe name cannot exceed 255 characters")
        private String name;

        @Schema(description = "Shoe description", example = "Classic running shoe with air cushioning")
        private String description;

        @Schema(description = "Base price", example = "149.99")
        @DecimalMin(value = "0.0", inclusive = true, message = "Base price must be non-negative")
        private BigDecimal basePrice;

        @Schema(description = "Gender", example = "UNISEX")
        private Shoe.Gender gender;

        @Schema(description = "Brand ID", example = "1")
        private Long brandId;

        @Schema(description = "Category ID", example = "1")
        private Long categoryId;

        @Schema(description = "Is active", example = "true")
        private Boolean isActive;
    }

    /**
     * DTO for admin updating shoes
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Shoe admin update DTO")
    @EqualsAndHashCode(callSuper = true)
    public static class AdminUpdateShoeDto extends BaseCrudDto.AdminUpdateDto {

        @Schema(description = "Shoe name", example = "Air Max 90")
        private String name;

        @Schema(description = "Shoe description", example = "Classic running shoe with air cushioning")
        private String description;

        @Schema(description = "Base price", example = "149.99")
        private BigDecimal basePrice;

        @Schema(description = "Gender", example = "UNISEX")
        private Shoe.Gender gender;

        @Schema(description = "Brand ID", example = "1")
        private Long brandId;

        @Schema(description = "Category ID", example = "1")
        private Long categoryId;

        @Schema(description = "Is active", example = "true")
        private Boolean isActive;
    }   

    /**
     * DTO for shoe inventory view
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Shoe inventory view DTO")
    @EqualsAndHashCode(callSuper = true)
    public static class ShoeInventoryViewDto extends ShoeDto {
      
        @JsonView(Views.Summary.class)
        private Long modelCount;

        @JsonView(Views.Summary.class)
        private Long totalStock;
    }
}
