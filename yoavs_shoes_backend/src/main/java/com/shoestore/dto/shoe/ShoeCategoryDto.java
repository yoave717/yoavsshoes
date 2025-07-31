package com.shoestore.dto.shoe;

import com.fasterxml.jackson.annotation.JsonView;
import com.shoestore.dto.base.BaseCrudDto;
import com.shoestore.dto.view.Views;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Category information DTO
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Category information")
@EqualsAndHashCode(callSuper = true)
public class ShoeCategoryDto extends BaseCrudDto {
    @JsonView(Views.Summary.class)
    private String name;

    @JsonView(Views.Detailed.class)
    private String description;

    @JsonView(Views.Detailed.class)
    private Boolean isActive;

    /**
     * DTO for creating shoe categories
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Shoe category creation DTO")
    @EqualsAndHashCode(callSuper = true)
    public static class CreateShoeCategoryDto extends BaseCrudDto.CreateDto {

        @Schema(description = "Category name", required = true, example = "Running")
        @NotBlank(message = "Category name is required")
        @Size(max = 100, message = "Category name cannot exceed 100 characters")
        private String name;

        @Schema(description = "Category description", example = "Shoes designed for running")
        @Size(max = 500, message = "Description cannot exceed 500 characters")
        private String description;

        @Schema(description = "Is the category active", example = "true")
        private Boolean isActive;
    }

    /**
     * DTO for updating shoe categories
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Shoe category update DTO")
    @EqualsAndHashCode(callSuper = true)
    public static class UpdateShoeCategoryDto extends BaseCrudDto.UpdateDto {

        @Schema(description = "Category name", example = "Running")
        @Size(max = 100, message = "Category name cannot exceed 100 characters")
        private String name;

        @Schema(description = "Category description", example = "Shoes designed for running")
        @Size(max = 500, message = "Description cannot exceed 500 characters")
        private String description;

        @Schema(description = "Is the category active", example = "true")
        private Boolean isActive;
    }

    /**
     * DTO for admin updating shoe categories
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Shoe category admin update DTO")
    @EqualsAndHashCode(callSuper = true)
    public static class AdminUpdateShoeCategoryDto extends BaseCrudDto.AdminUpdateDto {

        @Schema(description = "Category name", example = "Running")
        @Size(max = 100, message = "Category name cannot exceed 100 characters")
        private String name;

        @Schema(description = "Category description", example = "Shoes designed for running")
        @Size(max = 500, message = "Description cannot exceed 500 characters")
        private String description;

        @Schema(description = "Is the category active", example = "true")
        private Boolean isActive;
    }
}
