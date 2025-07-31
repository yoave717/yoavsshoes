package com.shoestore.dto.order;

import com.shoestore.dto.base.BaseCrudMapper;
import com.shoestore.dto.user.UserAddressMapper;
import com.shoestore.dto.user.UserMapper;
import com.shoestore.entity.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper implements BaseCrudMapper<Order, OrderDto,
        OrderDto.CreateOrderDto, OrderDto.UpdateOrderDto, OrderDto.AdminUpdateOrderDto> {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public OrderDto toDto(Order entity) {
        if (entity == null) {
            return null;
        }

        OrderDto dto = OrderDto.builder()
                .orderNumber(entity.getOrderNumber())
                .status(entity.getStatus())
                .totalAmount(entity.getTotalAmount())
                .orderDate(entity.getOrderDate())
                .shippedDate(entity.getShippedDate())
                .deliveredDate(entity.getDeliveredDate())
                .userId(entity.getUserId())
                .build();

        // Map related entities
        if (entity.getUser() != null) {
            dto.setUser(userMapper.toDto(entity.getUser()));
        }

        if (entity.getOrderItems() != null) {
            dto.setOrderItems(entity.getOrderItems().stream()
                    .map(orderItemMapper::toDto)
                    .toList());
            dto.setTotalItems(entity.getOrderItems().size());
        }

        if (entity.getShippingAddress() != null) {
            // Map shipping address - you'll need to inject AddressMapper or create conversion method
            dto.setShippingAddress(userAddressMapper.toDto(entity.getShippingAddress()));
        }

        // Map base entity fields
        mapBaseEntityToDto(entity, dto);

        return dto;
    }

    @Override
    public Order toEntity(OrderDto dto) {
        if (dto == null) {
            return null;
        }

        Order entity = Order.builder()
                .orderNumber(dto.getOrderNumber())
                .status(dto.getStatus())
                .totalAmount(dto.getTotalAmount())
                .orderDate(dto.getOrderDate())
                .shippedDate(dto.getShippedDate())
                .deliveredDate(dto.getDeliveredDate())
                .userId(dto.getUserId())
                .build();

        // Map base DTO fields to entity
        mapBaseDtoToEntity(dto, entity);

        return entity;
    }

    /**
     * Convert CreateOrderDto to Order entity
     */
    @Override
    public Order toEntity(OrderDto.CreateOrderDto dto) {
        if (dto == null) {
            return null;
        }
        
        Order entity = Order.builder()
                .userId(dto.getUserId())
                .shippingAddressId(dto.getShippingAddressId())
                .orderItems(dto.getItems() != null ? orderItemMapper.createToEntityList(dto.getItems()) : null)
                .build();

        return entity;
    }

}
