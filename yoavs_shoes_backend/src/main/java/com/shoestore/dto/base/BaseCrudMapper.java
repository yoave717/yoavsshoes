package com.shoestore.dto.base;

import java.util.List;
import java.util.stream.Collectors;

import com.shoestore.entity.base.BaseEntity;

/**
 * Base CRUD mapper interface that extends BaseMapper with support for Create and Update DTOs
 *
 * @param <E> Entity type extending BaseEntity
 * @param <D> DTO type extending BaseCrudDto
 * @param <C> Create DTO type extending BaseCrudDto.CreateDto
 * @param <U> Update DTO type extending BaseCrudDto.UpdateDto
 * @param <A> Admin Update DTO type extending BaseCrudDto.AdminUpdateDto
 */
public interface BaseCrudMapper<E extends BaseEntity, D extends BaseCrudDto, 
                               C extends BaseCrudDto.CreateDto, U extends BaseCrudDto.UpdateDto,
                               A extends BaseCrudDto.AdminUpdateDto> 
        extends BaseMapper<E, D> {

    /**
     * Convert Create DTO to entity
     */
    default E toEntity(C createDto) {
        throw new UnsupportedOperationException("toEntity(CreateDto) must be implemented by subclass");
    }
    
    /**
     * Convert Update DTO to entity
     */
    default E toEntity(U updateDto) {
        throw new UnsupportedOperationException("toEntity(UpdateDto) must be implemented by subclass");
    }
    
    /**
     * Convert Admin Update DTO to entity
     */
    default E toEntity(A adminUpdateDto) {
        throw new UnsupportedOperationException("toEntity(AdminUpdateDto) must be implemented by subclass");
    }


    default List<E> createToEntityList(List<C> createDto) {
        if (createDto == null) {
            return null;
        }
        return createDto.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
    
}
