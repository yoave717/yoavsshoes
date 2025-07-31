package com.shoestore.dto.order;

import com.fasterxml.jackson.annotation.JsonView;
import com.shoestore.dto.base.BaseCrudDto;
import com.shoestore.dto.shoe.ShoeModelDto;
import com.shoestore.dto.view.Views;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for order item information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderItemDto extends BaseCrudDto {

    @JsonView(Views.Detailed.class)
    private ShoeModelDto shoeModel;

    @JsonView(Views.Summary.class)
    private Long orderId;

    @JsonView(Views.Summary.class)
    private Long shoeModelId;

    @JsonView(Views.Summary.class)
    private String size;

    @JsonView(Views.Summary.class)
    private Integer quantity;

    @JsonView(Views.Summary.class)
    private BigDecimal unitPrice;

    @JsonView(Views.Summary.class)
    private BigDecimal totalPrice;

    /**
     * DTO for creating order items (used in order creation)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Order item creation DTO")
    @EqualsAndHashCode(callSuper = true)
    public static class CreateOrderItemDto extends BaseCrudDto.CreateDto {

        @Schema(description = "Order ID", required = true, example = "1")
        private Long orderId;

        @Schema(description = "Shoe model ID", required = true, example = "1")
        @NotNull(message = "Shoe model ID is required")
        private Long shoeModelId;

        @Schema(description = "Shoe size", required = true, example = "9")
        @NotNull(message = "Size is required")
        private String size;

        @Schema(description = "Quantity", required = true, example = "2")
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        private Integer quantity;
    }

    /**
     * DTO for updating order items
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Order item update DTO")
    @EqualsAndHashCode(callSuper = true)
    public static class UpdateOrderItemDto extends BaseCrudDto.UpdateDto {

        @Schema(description = "Quantity", example = "3")
        @Positive(message = "Quantity must be positive")
        private Integer quantity;
    }

    /**
     * DTO for admin updating order items
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Order item admin update DTO")
    @EqualsAndHashCode(callSuper = true)
    public static class AdminUpdateOrderItemDto extends BaseCrudDto.AdminUpdateDto {

        @Schema(description = "Quantity", example = "3")
        @Positive(message = "Quantity must be positive")
        private Integer quantity;
    }
}
