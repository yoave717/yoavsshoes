package com.shoestore.dto.shoe;

import com.shoestore.dto.base.BaseCrudMapper;
import com.shoestore.entity.shoe.Brand;
import org.springframework.stereotype.Component;

@Component
public class BrandMapper implements BaseCrudMapper<Brand, BrandDto,
        BrandDto.CreateBrandDto, BrandDto.UpdateBrandDto, BrandDto.AdminUpdateBrandDto> {


    private BrandDto buildBrandDto(Brand entity) {
        if (entity == null) {
            return null;
        }

        return BrandDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .logoUrl(entity.getLogoUrl())
                .isActive(entity.getIsActive())
                .build();
    }

    @Override
    public BrandDto toSimpleDto(Brand entity) {
        return buildBrandDto(entity);
    }

    @Override
    public BrandDto toDto(Brand entity) {
        BrandDto dto = buildBrandDto(entity);
        
        if (dto != null) {
            // Map base entity fields
            mapBaseEntityToDto(entity, dto);
        }

        return dto;
    }

    @Override
    public Brand toEntity(BrandDto dto) {
        if (dto == null) {
            return null;
        }

        Brand entity = Brand.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .logoUrl(dto.getLogoUrl())
                .isActive(dto.getIsActive())
                .build();

        // Map base DTO fields to entity
        mapBaseDtoToEntity(dto, entity);

        return entity;
    }

    @Override
    public Brand toEntity(BrandDto.CreateBrandDto dto) {
        if (dto == null) {
            return null;
        }

        return Brand.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .logoUrl(dto.getLogoUrl())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();
    }

    @Override
    public Brand toEntity(BrandDto.UpdateBrandDto dto) {
        if (dto == null) {
            return null;
        }

        return Brand.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .logoUrl(dto.getLogoUrl())
                .isActive(dto.getIsActive())
                .build();
    }
}
