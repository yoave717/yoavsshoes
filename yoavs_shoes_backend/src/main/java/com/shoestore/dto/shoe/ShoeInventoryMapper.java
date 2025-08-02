package com.shoestore.dto.shoe;

import com.shoestore.dto.base.BaseCrudMapper;
import com.shoestore.entity.shoe.ShoeInventory;


import org.hibernate.Hibernate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class ShoeInventoryMapper implements BaseCrudMapper<ShoeInventory, ShoeInventoryDto,
        ShoeInventoryDto.CreateShoeInventoryDto, ShoeInventoryDto.UpdateShoeInventoryDto, ShoeInventoryDto.AdminUpdateShoeInventoryDto> {

    private final ShoeModelMapper shoeModelMapper;

    public ShoeInventoryMapper(@Lazy ShoeModelMapper shoeModelMapper) {
        this.shoeModelMapper = shoeModelMapper;
    }

    private ShoeInventoryDto buildShoeInventoryDto(ShoeInventory entity) {
    if (entity == null) {
        return null;
    }

    return ShoeInventoryDto.builder()
            .id(entity.getId())
            .size(entity.getSize())
            .quantityAvailable(entity.getQuantityAvailable())
            .quantityReserved(entity.getQuantityReserved())
            .actualAvailableQuantity(entity.getActualAvailableQuantity())
            .inStock(entity.isInStock())
            .available(entity.isAvailable())
            .shoeModelId(entity.getShoeModelId())
            .build();
}

    @Override
    public ShoeInventoryDto toDto(ShoeInventory entity) {
        ShoeInventoryDto dto = buildShoeInventoryDto(entity);
        if (dto != null) {
            // Map shoe model info if available and initialized
        if (entity.getShoeModel() != null && Hibernate.isInitialized(entity.getShoeModel())) {
            dto.setShoeModel(shoeModelMapper.toSimpleDto(entity.getShoeModel()));
        }

        // Map base entity fields
        mapBaseEntityToDto(entity, dto);
        }

        return dto;
    }

    @Override
    public ShoeInventoryDto toSimpleDto(ShoeInventory entity) {
        return buildShoeInventoryDto(entity);
    }


    @Override
    public ShoeInventory toEntity(ShoeInventoryDto dto) {
        if (dto == null) {
            return null;
        }

        ShoeInventory entity = ShoeInventory.builder()
                .size(dto.getSize())
                .quantityAvailable(dto.getQuantityAvailable())
                .quantityReserved(dto.getQuantityReserved())
                .build();

        // Map base DTO fields to entity
        mapBaseDtoToEntity(dto, entity);

        return entity;
    }

    @Override
    public ShoeInventory toEntity(ShoeInventoryDto.CreateShoeInventoryDto dto) {
        if (dto == null) {
            return null;
        }

        return ShoeInventory.builder()
                .size(dto.getSize())
                .quantityAvailable(dto.getQuantityAvailable())
                .quantityReserved(dto.getQuantityReserved() != null ? dto.getQuantityReserved() : 0)
                .shoeModelId(dto.getShoeModelId())
                .build();
    }

    @Override
    public ShoeInventory toEntity(ShoeInventoryDto.UpdateShoeInventoryDto dto) {
        if (dto == null) {
            return null;
        }

        return ShoeInventory.builder()
                .quantityAvailable(dto.getQuantityAvailable())
                .quantityReserved(dto.getQuantityReserved())
                .build();
    }

    @Override
    public ShoeInventory toEntity(ShoeInventoryDto.AdminUpdateShoeInventoryDto dto) {
        if (dto == null) {
            return null;
        }

        return ShoeInventory.builder()
                .size(dto.getSize())
                .quantityAvailable(dto.getQuantityAvailable())
                .quantityReserved(dto.getQuantityReserved())
                // Note: ShoeModel entity will be set in service layer if shoeModelId is provided
                .build();
    }
}
