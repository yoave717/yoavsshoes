package com.shoestore.dto.order;

import com.fasterxml.jackson.annotation.JsonView;
import com.shoestore.dto.base.BaseCrudDto;
import com.shoestore.dto.user.UserAddressDto;
import com.shoestore.dto.user.UserDto;
import com.shoestore.dto.view.Views;
import com.shoestore.entity.order.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for order response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderDto extends BaseCrudDto {

    @JsonView(Views.Summary.class)
    private String orderNumber;

    @JsonView(Views.Summary.class)
    private OrderStatus status;

    @JsonView(Views.Summary.class)
    private BigDecimal totalAmount;

    @JsonView(Views.Summary.class)
    private LocalDateTime orderDate;

    @JsonView(Views.Detailed.class)
    private LocalDateTime shippedDate;

    @JsonView(Views.Detailed.class)
    private LocalDateTime deliveredDate;

    @JsonView(Views.Summary.class)
    private UserDto user;

    @JsonView(Views.Summary.class)
    private Long userId;

    @JsonView(Views.Detailed.class)
    private List<OrderItemDto> orderItems;

    @JsonView(Views.Detailed.class)
    private UserAddressDto shippingAddress;

    @JsonView(Views.Summary.class)
    private int totalItems;

    /**
     * DTO for creating new orders
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Order creation DTO")
    @EqualsAndHashCode(callSuper = true)
    public static class CreateOrderDto extends BaseCrudDto.CreateDto {
        
        @Schema(description = "User ID who is placing the order", required = true)
        private Long userId;

        @Schema(description = "Shipping address information", required = true)
        @NotNull(message = "Shipping address is required")
        private Long shippingAddressId;

        @Schema(description = "Order items", required = true)
        @NotEmpty(message = "Order must contain at least one item")
        @Valid
        private List<OrderItemDto.CreateOrderItemDto> items;
    }

    /**
     * DTO for updating orders (user updates)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Order update DTO")
    @EqualsAndHashCode(callSuper = true)
    public static class UpdateOrderDto extends BaseCrudDto.UpdateDto {

        @Schema(description = "Shipping address ID", example = "1")
        private Long shippingAddressId;

    }

    /**
     * DTO for admin order updates (includes admin-only fields)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Admin order update DTO")
    @EqualsAndHashCode(callSuper = true)
    public static class AdminUpdateOrderDto extends BaseCrudDto.AdminUpdateDto {

        @Schema(description = "Order status")
        private OrderStatus status;

        @Schema(description = "Shipping address ID", example = "1")
        private Long shippingAddressId;

        @Schema(description = "Shipped date")
        private LocalDateTime shippedDate;

        @Schema(description = "Delivered date")
        private LocalDateTime deliveredDate;

        @Schema(description = "Total amount override", example = "299.99")
        private BigDecimal totalAmount;
    }
}
