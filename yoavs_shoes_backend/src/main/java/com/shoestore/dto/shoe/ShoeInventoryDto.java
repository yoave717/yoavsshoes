package com.shoestore.dto.shoe;

import com.fasterxml.jackson.annotation.JsonView;
import com.shoestore.dto.base.BaseCrudDto;
import com.shoestore.dto.view.Views;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * DTO for shoe inventory information
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Shoe inventory information")
public class ShoeInventoryDto extends BaseCrudDto {

    @JsonView(Views.Summary.class)
    private Long shoeModelId;

    @JsonView(Views.Summary.class)
    private String size;

    @JsonView(Views.Summary.class)
    private Integer quantityAvailable;

    @JsonView(Views.Detailed.class)
    private Integer quantityReserved;

    @JsonView(Views.Summary.class)
    private Integer actualAvailableQuantity;

    @JsonView(Views.Summary.class)
    private Boolean inStock;

    @JsonView(Views.Summary.class)
    private Boolean available;

    @JsonView(Views.Detailed.class)
    private ShoeModelDto shoeModel;

   
    /**
     * DTO for creating shoe inventory
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Shoe inventory creation DTO")
    @EqualsAndHashCode(callSuper = true)
    public static class CreateShoeInventoryDto extends BaseCrudDto.CreateDto {

        @Schema(description = "Shoe model ID", required = true, example = "1")
        @NotNull(message = "Shoe model ID is required")
        private Long shoeModelId;

        @Schema(description = "Size", required = true, example = "9")
        @NotBlank(message = "Size is required")
        private String size;

        @Schema(description = "Available quantity", required = true, example = "15")
        @NotNull(message = "Quantity available is required")
        @Min(value = 0, message = "Quantity available must be non-negative")
        private Integer quantityAvailable;

        @Schema(description = "Reserved quantity", example = "0")
        @Min(value = 0, message = "Quantity reserved must be non-negative")
        private Integer quantityReserved;
    }

    /**
     * DTO for updating shoe inventory
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Shoe inventory update DTO")
    @EqualsAndHashCode(callSuper = true)
    public static class UpdateShoeInventoryDto extends BaseCrudDto.UpdateDto {

        @Schema(description = "Available quantity", example = "15")
        @Min(value = 0, message = "Quantity available must be non-negative")
        private Integer quantityAvailable;

        @Schema(description = "Reserved quantity", example = "2")
        @Min(value = 0, message = "Quantity reserved must be non-negative")
        private Integer quantityReserved;
    }

    /**
     * DTO for admin updating shoe inventory
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Shoe inventory admin update DTO")
    @EqualsAndHashCode(callSuper = true)
    public static class AdminUpdateShoeInventoryDto extends BaseCrudDto.AdminUpdateDto {

        @Schema(description = "Shoe model ID", example = "1")
        private Long shoeModelId;

        @Schema(description = "Size", example = "9")
        private String size;

        @Schema(description = "Available quantity", example = "15")
        @Min(value = 0, message = "Quantity available must be non-negative")
        private Integer quantityAvailable;

        @Schema(description = "Reserved quantity", example = "2")
        @Min(value = 0, message = "Quantity reserved must be non-negative")
        private Integer quantityReserved;
    }
}
