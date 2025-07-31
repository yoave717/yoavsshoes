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
 * Brand information DTO
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BrandDto extends BaseCrudDto{

    @JsonView(Views.Summary.class)
    private String name;

    @JsonView(Views.Detailed.class)
    private String description;

    @JsonView(Views.Summary.class)
    private String logoUrl;

    @JsonView(Views.Summary.class)
    private Boolean isActive;

    /**
     * DTO for creating brands
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Brand creation DTO")
    @EqualsAndHashCode(callSuper = true)
    public static class CreateBrandDto extends BaseCrudDto.CreateDto {

        @Schema(description = "Brand name", required = true, example = "Nike")
        @NotBlank(message = "Brand name is required")
        @Size(max = 100, message = "Brand name cannot exceed 100 characters")
        private String name;

        @Schema(description = "Brand description", example = "Leading athletic footwear brand")
        private String description;

        @Schema(description = "Logo URL")
        private String logoUrl;

        @Schema(description = "Is the brand active", example = "true")
        private Boolean isActive;
    }

    /**
     * DTO for updating brands
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Brand update DTO")
    @EqualsAndHashCode(callSuper = true)
    public static class UpdateBrandDto extends BaseCrudDto.UpdateDto {

        @Schema(description = "Brand name", example = "Nike")
        @Size(max = 100, message = "Brand name cannot exceed 100 characters")
        private String name;

        @Schema(description = "Brand description", example = "Leading athletic footwear brand")
        private String description;

        @Schema(description = "Logo URL")
        private String logoUrl;

        @Schema(description = "Is the brand active", example = "true")
        private Boolean isActive;
    }


    /**
     * DTO for admin updating brands
     */

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Brand admin update DTO")
    @EqualsAndHashCode(callSuper = true)
    public static class AdminUpdateBrandDto extends BaseCrudDto.AdminUpdateDto {

        @Schema(description = "Brand name", example = "Nike")
        private String name;

        @Schema(description = "Brand description", example = "Leading athletic footwear brand")
        private String description;

        @Schema(description = "Logo URL")
        private String logoUrl;

        @Schema(description = "Is the brand active", example = "true")
        private Boolean isActive;    
    }
}
