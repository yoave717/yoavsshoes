package com.shoestore.dto.shoe;

import com.fasterxml.jackson.annotation.JsonView;
import com.shoestore.dto.base.BaseCrudDto;
import com.shoestore.dto.view.Views;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for shoe model information
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ShoeModelDto extends BaseCrudDto {
    
    @JsonView(Views.Detailed.class)
    private ShoeDto shoe;

    @JsonView(Views.Summary.class)
    private String modelName;

    @JsonView(Views.Summary.class)
    private String color;

    @JsonView(Views.Summary.class)
    private String material;

    @JsonView(Views.Summary.class)
    private String sku;

    @JsonView(Views.Summary.class)
    private BigDecimal price;

    @JsonView(Views.Detailed.class)
    private String imageUrl;

    @JsonView(Views.Summary.class)
    private Boolean isActive;

    @JsonView(Views.Summary.class)
    private String displayName;

    @JsonView(Views.Summary.class)
    private String fullDisplayName;

    @JsonView(Views.Detailed.class)
    private List<ShoeInventoryDto> availableSizes;

    /**
     * DTO for creating shoe models
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Shoe model creation DTO")
    @EqualsAndHashCode(callSuper = true)
    public static class CreateShoeModelDto extends BaseCrudDto.CreateDto {

        @Schema(description = "Parent shoe ID", required = true, example = "1")
        @NotNull(message = "Shoe ID is required")
        private Long shoeId;

        @Schema(description = "Model name", required = true, example = "Classic")
        @NotBlank(message = "Model name is required")
        @Size(max = 255, message = "Model name cannot exceed 255 characters")
        private String modelName;

        @Schema(description = "Color", required = true, example = "Black")
        @NotBlank(message = "Color is required")
        @Size(max = 50, message = "Color cannot exceed 50 characters")
        private String color;

        @Schema(description = "Material", example = "Leather")
        @Size(max = 100, message = "Material cannot exceed 100 characters")
        private String material;

        @Schema(description = "SKU", required = true, example = "AM270-BLK-LEA")
        @NotBlank(message = "SKU is required")
        @Size(max = 100, message = "SKU cannot exceed 100 characters")
        private String sku;

        @Schema(description = "Price", required = true, example = "149.99")
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Price must be non-negative")
        private BigDecimal price;

        @Schema(description = "Image URL")
        private String imageUrl;

        @Schema(description = "Is active", example = "true")
        private Boolean isActive;
    }

    /**
     * DTO for updating shoe models
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Shoe model update DTO")
    @EqualsAndHashCode(callSuper = true)
    public static class UpdateShoeModelDto extends BaseCrudDto.UpdateDto {

        @Schema(description = "Model name", example = "Classic")
        @Size(max = 255, message = "Model name cannot exceed 255 characters")
        private String modelName;

        @Schema(description = "Color", example = "Black")
        @Size(max = 50, message = "Color cannot exceed 50 characters")
        private String color;

        @Schema(description = "Material", example = "Leather")
        @Size(max = 100, message = "Material cannot exceed 100 characters")
        private String material;

        @Schema(description = "Price", example = "149.99")
        @DecimalMin(value = "0.0", inclusive = true, message = "Price must be non-negative")
        private BigDecimal price;

        @Schema(description = "Image URL")
        private String imageUrl;

        @Schema(description = "Is active", example = "true")
        private Boolean isActive;
    }

    /**
     * DTO for admin updating shoe models
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Shoe model admin update DTO")
    @EqualsAndHashCode(callSuper = true)
    public static class AdminUpdateShoeModelDto extends BaseCrudDto.AdminUpdateDto {

        @Schema(description = "Model name", example = "Classic")
        @Size(max = 255, message = "Model name cannot exceed 255 characters")
        private String modelName;

        @Schema(description = "Color", example = "Black")
        @Size(max = 50, message = "Color cannot exceed 50 characters")
        private String color;

        @Schema(description = "Material", example = "Leather")
        @Size(max = 100, message = "Material cannot exceed 100 characters")
        private String material;

        @Schema(description = "SKU", example = "AM270-BLK-LEA")
        @Size(max = 100, message = "SKU cannot exceed 100 characters")
        private String sku;

        @Schema(description = "Price", example = "149.99")
        @DecimalMin(value = "0.0", inclusive = true, message = "Price must be non-negative")
        private BigDecimal price;

        @Schema(description = "Image URL")
        private String imageUrl;

        @Schema(description = "Is active", example = "true")
        private Boolean isActive;
    }
}
