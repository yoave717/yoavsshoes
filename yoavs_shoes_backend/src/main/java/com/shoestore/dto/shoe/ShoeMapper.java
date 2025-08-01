package com.shoestore.dto.shoe;

import com.shoestore.dto.base.BaseCrudMapper;
import com.shoestore.dto.shoe.ShoeDto.ShoeInventoryViewDto;
import com.shoestore.entity.shoe.Shoe;
import org.springframework.stereotype.Component;

@Component
public class ShoeMapper implements BaseCrudMapper<Shoe, ShoeDto,
        ShoeDto.CreateShoeDto, ShoeDto.UpdateShoeDto, ShoeDto.AdminUpdateShoeDto> {

    private final BrandMapper brandMapper;
    private final ShoeCategoryMapper categoryMapper;

    public ShoeMapper(BrandMapper brandMapper, ShoeCategoryMapper categoryMapper) {
        this.brandMapper = brandMapper;
        this.categoryMapper = categoryMapper;
    }

    private ShoeDto buildShoeDto(Shoe entity) {
        if (entity == null) {
            return null;
        }

        return ShoeDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .basePrice(entity.getBasePrice())
                .gender(entity.getGender())
                .brand(brandMapper.toSimpleDto(entity.getBrand()))
                .category(categoryMapper.toSimpleDto(entity.getCategory()))
                .build();
    }

    @Override
    public ShoeDto toSimpleDto(Shoe entity) {
        return buildShoeDto(entity);
    }

    @Override
    public ShoeDto toDto(Shoe entity) {
        ShoeDto dto = buildShoeDto(entity);

        if (dto != null) {
            // Map base entity fields
            mapBaseEntityToDto(entity, dto);
        }

        return dto;
    }

    @Override
    public Shoe toEntity(ShoeDto dto) {
        if (dto == null) {
            return null;
        }

        Shoe entity = Shoe.builder()
                .name(dto.getName())
                .basePrice(dto.getBasePrice())
                .gender(dto.getGender())
                .build();

        // Map base DTO fields to entity
        mapBaseDtoToEntity(dto, entity);

        return entity;
    }

    @Override
    public Shoe toEntity(ShoeDto.CreateShoeDto dto) {
        if (dto == null) {
            return null;
        }

        return Shoe.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .basePrice(dto.getBasePrice())
                .brandId(dto.getBrandId())
                .categoryId(dto.getCategoryId())
                .gender(dto.getGender())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();
    }

    @Override
    public Shoe toEntity(ShoeDto.UpdateShoeDto dto) {
        if (dto == null) {
            return null;
        }
        
        return Shoe.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .basePrice(dto.getBasePrice())
                .gender(dto.getGender())
                .isActive(dto.getIsActive())
                // Note: Brand and Category entities will be set in service layer using brandId and categoryId
                .build();
    }

    public ShoeInventoryViewDto toInventoryViewDto(Shoe shoe, Long modelCount, Long totalStock) {
        if (shoe == null) {
            return null;
        }

        ShoeDto shoeDto =  toDto(shoe);

        ShoeInventoryViewDto shoeInventoryViewDto = ShoeInventoryViewDto.builder()
                .id(shoeDto.getId())
                .name(shoeDto.getName())
                .basePrice(shoeDto.getBasePrice())
                .gender(shoeDto.getGender())
                .brand(shoeDto.getBrand())
                .category(shoeDto.getCategory())
                .modelCount(modelCount)
                .totalStock(totalStock)
                .build();

        // Map base entity fields to inventory view DTO
        mapBaseEntityToDto(shoe, shoeInventoryViewDto);

        return shoeInventoryViewDto;
    }
}
