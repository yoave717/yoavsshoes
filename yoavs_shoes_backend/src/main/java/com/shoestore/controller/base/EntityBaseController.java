package com.shoestore.controller.base;

import com.shoestore.dto.base.BaseDto;
import com.shoestore.dto.base.BaseMapper;
import com.shoestore.dto.base.PageResponse;
import com.shoestore.entity.base.BaseEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.net.URI;
import java.util.List;

/**
 * Base controller class providing common functionality for entity-based controllers
 */
@Slf4j
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class EntityBaseController<E extends BaseEntity, D extends BaseDto, M extends BaseMapper<E, D>>
        extends BaseController {


    protected final M mapper;


    /**
     * Functional interface for extracting user ID from an entity
     */
    @FunctionalInterface
    public interface UserIdExtractor<T> {
        Long extractUserId(T entity);
    }

    /**
     * Convert entity to DTO using the mapper
     */
    protected D convertToDto(E entity) {
        return mapper.toDto(entity);
    }

    /**
     * Convert DTO to entity using the mapper
     */
    protected E convertToEntity(D dto) {
        return mapper.toEntity(dto);
    }

    /**
     * Convert list of entities to DTOs
     */
    protected List<D> convertToDtoList(List<E> entities) {
        return mapper.toDtoList(entities);
    }

    /**
     * Convert Page of entities to PageResponse of DTOs
     */
    protected PageResponse<D> convertToPageResponse(Page<E> page) {
        List<D> dtoContent = mapper.toDtoList(page.getContent());
        return PageResponse.of(page, dtoContent);
    }

    /**
     * Create success response with entity converted to DTO
     */
    protected ResponseEntity<StandardResponse<D>> successWithDto(E entity) {
        return success(convertToDto(entity));
    }

    /**
     * Create success response with entity list converted to DTOs
     */
    protected ResponseEntity<StandardResponse<List<D>>> successWithDtoList(List<E> entities) {
        return success(convertToDtoList(entities));
    }

    /**
     * Create success response with page converted to PageResponse
     */
    protected ResponseEntity<StandardResponse<PageResponse<D>>> successWithPageResponse(Page<E> page) {
        return success(convertToPageResponse(page));
    }

    /**
     * Create success response for creation with entity converted to DTO
     */
    protected ResponseEntity<StandardResponse<D>> createdWithDto(E entity) {
        return created(convertToDto(entity));
    }

    /**
     * Create success response for creation with location header and entity converted to DTO
     */
    protected ResponseEntity<StandardResponse<D>> createdWithDto(E entity, String resourcePath, Object... pathVariables) {
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(resourcePath)
                .buildAndExpand(pathVariables)
                .toUri();

        return ResponseEntity.created(location)
                .body(StandardResponse.success(convertToDto(entity), "Resource created successfully"));
    }
}