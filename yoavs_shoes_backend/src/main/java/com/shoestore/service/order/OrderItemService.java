package com.shoestore.service.order;

import com.shoestore.dto.order.OrderItemDto;
import com.shoestore.entity.order.OrderItem;
import com.shoestore.entity.shoe.ShoeModel;
import com.shoestore.exception.BadRequestException;
import com.shoestore.exception.ResourceNotFoundException;
import com.shoestore.repository.order.OrderItemRepository;
import com.shoestore.service.base.BaseService;
import com.shoestore.service.shoe.ShoeModelService;
import com.shoestore.service.shoe.ShoeInventoryService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for managing order items
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class OrderItemService extends BaseService<OrderItem, Long, OrderItemRepository> {

    private final ShoeModelService shoeModelService;
    private final ShoeInventoryService shoeInventoryService;

    public OrderItemService(OrderItemRepository repository,
                           ShoeModelService shoeModelService,
                           ShoeInventoryService shoeInventoryService) {
        super(repository, "OrderItem");
        this.shoeModelService = shoeModelService;
        this.shoeInventoryService = shoeInventoryService;
    }

    @Override
    protected void updateEntityFields(OrderItem existingEntity, OrderItem newEntity) {
        // Update quantity and recalculate price
        if (newEntity.getQuantity() != null) {
            existingEntity.setQuantity(newEntity.getQuantity());
        }

        // Size can be updated if needed
        if (newEntity.getSize() != null && !newEntity.getSize().trim().isEmpty()) {
            existingEntity.setSize(newEntity.getSize());
        }

        // Unit price typically shouldn't change, but allow it for admin corrections
        if (newEntity.getUnitPrice() != null) {
            existingEntity.setUnitPrice(newEntity.getUnitPrice());
        }

        // Total price will be recalculated automatically via entity methods
        existingEntity.calculateTotalPrice();
    }

    
    /**
     * Create multiple order items for an order
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public List<OrderItem> createOrderItems(Long orderId, List<OrderItemDto.CreateOrderItemDto> itemDtoList) {
        log.info("Creating {} order items for order: {}", itemDtoList.size(), orderId);

        Map<Long, ShoeModel> shoeModelMap = loadShoeModels(itemDtoList);
        validateOrderItems(itemDtoList, shoeModelMap);
        
        List<OrderItemDto.CreateOrderItemDto> reservedItems = new ArrayList<>();
        
        try {
            reserveInventoryForItems(itemDtoList, reservedItems);

            return createAndSaveOrderItems(orderId, itemDtoList, shoeModelMap);
        } catch (Exception e) {
            releaseReservedInventory(reservedItems);
            throw e;
        }
    }

    /**
     * Calculate total amount for order items
     */
    public BigDecimal calculateTotalAmount(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    // ===========================================
    // PRIVATE HELPER METHODS
    // ===========================================


    private void validateShoeModelActive(ShoeModel shoeModel) {
        if (Boolean.FALSE.equals(shoeModel.getIsActive()) || Boolean.FALSE.equals(shoeModel.getShoe().getIsActive())) {
            throw new BadRequestException("Shoe model " + shoeModel.getId() + " is not available");
        }
    }

    private Map<Long, ShoeModel> loadShoeModels(List<OrderItemDto.CreateOrderItemDto> itemDtoList) {
        List<Long> shoeModelIds = itemDtoList.stream()
                .map(OrderItemDto.CreateOrderItemDto::getShoeModelId)
                .distinct().toList();
                
        return shoeModelService.findAllById(shoeModelIds)
                .stream()
                .collect(Collectors.toMap(ShoeModel::getId, shoeModel -> shoeModel));
    }

    private void validateOrderItems(List<OrderItemDto.CreateOrderItemDto> itemDtoList, Map<Long, ShoeModel> shoeModelMap) {
        for (OrderItemDto.CreateOrderItemDto itemDto : itemDtoList) {
            ShoeModel shoeModel = shoeModelMap.get(itemDto.getShoeModelId());

            if (shoeModel == null) {
                throw new ResourceNotFoundException("ShoeModel", itemDto.getShoeModelId().toString());
            }
            
            validateShoeModelActive(shoeModel);
            
            if (itemDto.getQuantity() <= 0) {
                throw new BadRequestException("Quantity must be positive");
            }
            
            if (!shoeInventoryService.isInventoryAvailable(itemDto.getShoeModelId(), itemDto.getSize(), itemDto.getQuantity())) {
                throw new BadRequestException("Insufficient inventory for shoe model " + itemDto.getShoeModelId() +
                                            " size " + itemDto.getSize() + ". Requested: " + itemDto.getQuantity());
            }
        }
    }

    private void reserveInventoryForItems(List<OrderItemDto.CreateOrderItemDto> itemDtoList, List<OrderItemDto.CreateOrderItemDto> reservedItems) {
        for (OrderItemDto.CreateOrderItemDto itemDto : itemDtoList) {
            shoeInventoryService.reserveInventory(itemDto.getShoeModelId(), itemDto.getSize(), itemDto.getQuantity());
            reservedItems.add(itemDto);
        }
    }

    private List<OrderItem> createAndSaveOrderItems(Long orderId, List<OrderItemDto.CreateOrderItemDto> itemDtoList, Map<Long, ShoeModel> shoeModelMap) {
        List<OrderItem> orderItems = itemDtoList.stream().map(itemDto -> {
            ShoeModel shoeModel = shoeModelMap.get(itemDto.getShoeModelId());
            
            OrderItem orderItem = OrderItem.builder()
                    .orderId(orderId)
                    .shoeModelId(itemDto.getShoeModelId())
                    .size(itemDto.getSize())
                    .quantity(itemDto.getQuantity())
                    .unitPrice(shoeModel.getPrice())
                    .build();
                    
            orderItem.calculateTotalPrice();
            return orderItem;
        }).toList();

        List<OrderItem> savedItems = repository.saveAll(orderItems);
        log.info("Created {} order items for order {}", savedItems.size(), orderId);
        return savedItems;
    }

    private void releaseReservedInventory(List<OrderItemDto.CreateOrderItemDto> reservedItems) {
        for (OrderItemDto.CreateOrderItemDto itemDto : reservedItems) {
            try {
                shoeInventoryService.releaseReservedInventory(itemDto.getShoeModelId(), itemDto.getSize(), itemDto.getQuantity());
            } catch (Exception e) {
                log.error("Failed to release reserved inventory for shoe model {} size {}: {}", 
                         itemDto.getShoeModelId(), itemDto.getSize(), e.getMessage());
            }
        }
    }
}
