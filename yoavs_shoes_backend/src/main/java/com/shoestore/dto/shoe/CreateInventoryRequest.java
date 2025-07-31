package com.shoestore.dto.shoe;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating shoe inventory entry
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Create shoe inventory request")
public class CreateInventoryRequest {

    @Schema(description = "Shoe model ID", example = "1", required = true)
    @NotNull(message = "Shoe model ID is required")
    private Long shoeModelId;

    @Schema(description = "Size", example = "9", required = true)
    @NotBlank(message = "Size is required")
    @Size(max = 10, message = "Size cannot exceed 10 characters")
    private String size;

    @Schema(description = "Available quantity", example = "20", required = true)
    @NotNull(message = "Available quantity is required")
    @Min(value = 0, message = "Available quantity must be non-negative")
    private Integer quantityAvailable;

    @Schema(description = "Reserved quantity", example = "0")
    @Min(value = 0, message = "Reserved quantity must be non-negative")
    @Builder.Default
    private Integer quantityReserved = 0;
}
