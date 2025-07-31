package com.shoestore.dto.base;

import com.shoestore.entity.base.BaseEntity;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

public interface BaseMapper<E extends BaseEntity, D extends BaseDto> {

    /**
     * Convert entity to DTO
     */
    D toDto(E entity);
    
    /**
     * Convert DTO to entity
     */
    E toEntity(D dto);

    /**
     * 
     * Convert entity to simple DTO without nested objects
     */
    default D toSimpleDto(E entity) {
        throw new UnsupportedOperationException("toSimpleDto must be implemented by subclass");
    }

    /**
     * Convert list of entities to list of simple DTOs
     */
    default List<D> toSimpleDtoList(List<E> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toSimpleDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert list of entities to list of DTOs
     */
    default List<D> toDtoList(List<E> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert list of DTOs to list of entities
     */
    default List<E> toEntityList(List<D> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Map base fields from entity to DTO
     */
    default void mapBaseEntityToDto(BaseEntity entity, BaseDto dto) {
        if (entity != null && dto != null) {
            dto.setId(entity.getId());
            dto.setCreatedAt(entity.getCreatedAt());
            dto.setUpdatedAt(entity.getUpdatedAt());
            dto.setCreatedBy(entity.getCreatedBy());
            dto.setUpdatedBy(entity.getUpdatedBy());
            dto.setVersion(entity.getVersion());
        }
    }
    
    /**
     * Map base fields from DTO to entity (excluding audit fields)
     */
    default void mapBaseDtoToEntity(BaseDto dto, BaseEntity entity) {
        if (dto != null && entity != null) {
            entity.setId(dto.getId());
            entity.setVersion(dto.getVersion());
            // Note: audit fields (created/updated by/at) are managed by JPA auditing
        }
    }

    /**
     * Helper method to map Page<T> to PageResponse<D>
     */
    default PageResponse<D> mapToPageResponse(Page<E> page) {
        List<D> content = page.getContent().stream()
                .map(this::toDto)
                .toList();
                
        return PageResponse.of(page, content);
    }

}
