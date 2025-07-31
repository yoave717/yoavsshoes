package com.shoestore.dto.shoe;

import com.shoestore.dto.base.BaseCrudMapper;
import com.shoestore.entity.shoe.ShoeCategory;
import org.springframework.stereotype.Component;

@Component
public class ShoeCategoryMapper implements BaseCrudMapper<ShoeCategory, ShoeCategoryDto,
        ShoeCategoryDto.CreateShoeCategoryDto, ShoeCategoryDto.UpdateShoeCategoryDto, ShoeCategoryDto.AdminUpdateShoeCategoryDto> {

    private ShoeCategoryDto buildShoeCategoryDto(ShoeCategory entity) {
        if (entity == null) {
            return null;
        }

        return ShoeCategoryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .isActive(entity.getIsActive())
                .build();
    }

    @Override
    public ShoeCategoryDto toSimpleDto(ShoeCategory entity) {
        return buildShoeCategoryDto(entity);
    }



    @Override
    public ShoeCategoryDto toDto(ShoeCategory entity) {
        ShoeCategoryDto dto = buildShoeCategoryDto(entity);
        if (dto != null) {
            // Map base entity fields
            mapBaseEntityToDto(entity, dto);
        }

        return dto;
    }

    @Override
    public ShoeCategory toEntity(ShoeCategoryDto dto) {
        if (dto == null) {
            return null;
        }

        ShoeCategory entity = ShoeCategory.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .isActive(dto.getIsActive())
                .build();

        // Map base DTO fields to entity
        mapBaseDtoToEntity(dto, entity);

        return entity;
    }

    @Override
    public ShoeCategory toEntity(ShoeCategoryDto.CreateShoeCategoryDto dto) {
        if (dto == null) {
            return null;
        }

        return ShoeCategory.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();
    }

    @Override
    public ShoeCategory toEntity(ShoeCategoryDto.UpdateShoeCategoryDto dto) {
        if (dto == null) {
            return null;
        }

        return ShoeCategory.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .isActive(dto.getIsActive())
                .build();
    }

    @Override
    public ShoeCategory toEntity(ShoeCategoryDto.AdminUpdateShoeCategoryDto dto) {
        if (dto == null) {
            return null;
        }

        return ShoeCategory.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .isActive(dto.getIsActive())
                .build();
    }
}
