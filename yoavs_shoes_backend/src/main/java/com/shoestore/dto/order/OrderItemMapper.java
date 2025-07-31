package com.shoestore.dto.order;

import com.shoestore.dto.base.BaseCrudMapper;
import com.shoestore.dto.shoe.ShoeModelMapper;
import com.shoestore.entity.order.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper implements BaseCrudMapper<OrderItem, OrderItemDto,
        OrderItemDto.CreateOrderItemDto, OrderItemDto.UpdateOrderItemDto, OrderItemDto.AdminUpdateOrderItemDto> {

    private final ShoeModelMapper shoeModelMapper;

    public OrderItemMapper(ShoeModelMapper shoeModelMapper) {
        this.shoeModelMapper = shoeModelMapper;
    }

    @Override
    public OrderItemDto toDto(OrderItem entity) {
        if (entity == null) {
            return null;
        }

        OrderItemDto dto = OrderItemDto.builder()
                .orderId(entity.getOrderId())
                .shoeModelId(entity.getShoeModelId())
                .size(entity.getSize())
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .totalPrice(entity.getTotalPrice())
                .build();

        // Map shoe model info
        if (entity.getShoeModel() != null) {
            dto.setShoeModel(shoeModelMapper.toSimpleDto(entity.getShoeModel()));
        }

        // Map base entity fields
        mapBaseEntityToDto(entity, dto);

        return dto;
    }

    @Override
    public OrderItem toEntity(OrderItemDto dto) {
        if (dto == null) {
            return null;
        }

        OrderItem entity = OrderItem.builder()
                .orderId(dto.getOrderId())
                .shoeModelId(dto.getShoeModelId())
                .size(dto.getSize())
                .quantity(dto.getQuantity())
                .unitPrice(dto.getUnitPrice())
                .totalPrice(dto.getTotalPrice())
                .build();

        // Map base DTO fields to entity
        mapBaseDtoToEntity(dto, entity);

        return entity;
    }

    /**
     * Convert CreateOrderItemDto to OrderItem entity
     */
    @Override
    public OrderItem toEntity(OrderItemDto.CreateOrderItemDto dto) {
        if (dto == null) {
            return null;
        }

        return OrderItem.builder()
                .orderId(dto.getOrderId())
                .shoeModelId(dto.getShoeModelId())
                .size(dto.getSize())
                .quantity(dto.getQuantity())
                .build();
    }

}
