package com.shoestore.dto.shoe;

import com.shoestore.dto.base.BaseCrudMapper;
import com.shoestore.entity.shoe.ShoeModel;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

@Component
public class ShoeModelMapper implements BaseCrudMapper<ShoeModel, ShoeModelDto,
        ShoeModelDto.CreateShoeModelDto, ShoeModelDto.UpdateShoeModelDto, ShoeModelDto.AdminUpdateShoeModelDto> {

    private final ShoeMapper shoeMapper;
    private final ShoeInventoryMapper shoeInventoryMapper;


    public ShoeModelMapper(ShoeMapper shoeMapper, ShoeInventoryMapper shoeInventoryMapper) {
        this.shoeMapper = shoeMapper;
        this.shoeInventoryMapper = shoeInventoryMapper;
    }

    /**
     * Map ShoeModel entity to response DTO
     */
    @Override
    public ShoeModelDto toDto(ShoeModel entity) {

        ShoeModelDto dto = buildShoeModelDto(entity);

        // Map shoe info if available and initialized
        if (entity.getShoe() != null && Hibernate.isInitialized(entity.getShoe())) {
            dto.setShoe( shoeMapper.toSimpleDto(entity.getShoe()));
        }
        
        // Map available sizes only if collection is already initialized (not lazy loaded)
        if (entity.getAvailableSizes() != null && Hibernate.isInitialized(entity.getAvailableSizes())
            && !entity.getAvailableSizes().isEmpty()) {
            dto.setAvailableSizes(shoeInventoryMapper.toSimpleDtoList(entity.getAvailableSizes()));
        }

        mapBaseEntityToDto(entity, dto);
        
        return dto;
    }

    @Override
    public ShoeModelDto toSimpleDto(ShoeModel entity) {
        return buildShoeModelDto(entity);
    }

    private ShoeModelDto buildShoeModelDto(ShoeModel entity) {
        if (entity == null) {
            return null;
        }

        return ShoeModelDto.builder()
                .id(entity.getId())
                .modelName(entity.getModelName())
                .color(entity.getColor())
                .material(entity.getMaterial())
                .sku(entity.getSku())
                .price(entity.getPrice())
                .imageUrl(entity.getImageUrl())
                .isActive(entity.getIsActive())
                .displayName(entity.getDisplayName())
                .fullDisplayName(entity.getFullDisplayName())
                .build();
    }

    @Override
    public ShoeModel toEntity(ShoeModelDto dto) {
        if (dto == null) {
            return null;
        }

        ShoeModel entity = ShoeModel.builder()
                .modelName(dto.getModelName())
                .color(dto.getColor())
                .material(dto.getMaterial())
                .sku(dto.getSku())
                .price(dto.getPrice())
                .imageUrl(dto.getImageUrl())
                .isActive(dto.getIsActive())
                .build();

        // Map base DTO fields to entity
        mapBaseDtoToEntity(dto, entity);

        return entity;
    }

    @Override
    public ShoeModel toEntity(ShoeModelDto.CreateShoeModelDto dto) {
        if (dto == null) {
            return null;
        }

        return ShoeModel.builder()
                .modelName(dto.getModelName())
                .color(dto.getColor())
                .material(dto.getMaterial())
                .sku(dto.getSku())
                .price(dto.getPrice())
                .imageUrl(dto.getImageUrl())
                .shoeId(dto.getShoeId())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();
    }

}
