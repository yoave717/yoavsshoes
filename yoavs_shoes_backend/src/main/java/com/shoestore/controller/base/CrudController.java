package com.shoestore.controller.base;

import com.fasterxml.jackson.annotation.JsonView;
import com.shoestore.dto.base.BaseCrudDto;
import com.shoestore.dto.base.BaseCrudMapper;
import com.shoestore.dto.base.PageResponse;
import com.shoestore.dto.view.Views;
import com.shoestore.entity.base.BaseEntity;
import com.shoestore.security.annotation.AccessControl;
import com.shoestore.service.base.BaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Abstract CRUD controller providing common REST operations
 *
 * @param <T> Entity type
 * @param <ID> Entity ID type
 * @param <CreateDto> DTO for creation requests
 * @param <UpdateDto> DTO for update requests
 * @param <ResponseDto> DTO for responses
 * @param <M> Mapper type for converting between entity and DTO
 * @param <S> Service type
 */
@Slf4j
public abstract class CrudController<
        T extends BaseEntity,
        ID,
        CreateDto extends BaseCrudDto.CreateDto,
        UpdateDto extends BaseCrudDto.UpdateDto,
        ResponseDto extends BaseCrudDto,
        M extends BaseCrudMapper<T, ResponseDto, CreateDto, UpdateDto, ? extends BaseCrudDto.AdminUpdateDto>,
        S extends BaseService<T, ID, ?>
        > extends EntityBaseController<T, ResponseDto, M> {

    protected final S service;
    protected final String entityName;

    /**
     * Constructor
     */
    protected CrudController(S service, String entityName, M mapper) {
        super(mapper);
        this.service = service;
        this.entityName = entityName;
    }

    // ==============================================
    // 1. CRUD ENDPOINTS (Standard REST Operations)
    // ==============================================

    /**
     * Get all entities with pagination
     */
    @Operation(summary = "Get all entities", description = "Retrieve all entities with pagination support")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entities retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    })
    @GetMapping
    @JsonView(Views.Detailed.class)
    public ResponseEntity<StandardResponse<PageResponse<ResponseDto>>> getAll(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,

            @Parameter(description = "Page size (max 100)", example = "20")
            @RequestParam(defaultValue = "20") Integer size,

            @Parameter(description = "Sort field", example = "id")
            @RequestParam(required = false) String sortBy,

            @Parameter(description = "Sort direction (asc/desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.debug("Getting all {} with page: {}, size: {}, sortBy: {}, sortDir: {}",
                entityName, page, size, sortBy, sortDir);

        validatePagination(page, size);
        validateSort(sortBy, getAllowedSortFields());

        Pageable pageable = createPageable(page, size, sortBy, sortDir);
        Page<T> entityPage = service.findAll(pageable);

        List<ResponseDto> dtoList = convertToDtoList(entityPage.getContent());
        PageResponse<ResponseDto> pageResponse = PageResponse.of(entityPage, dtoList);

        logAction("GET_ALL_" + entityName.toUpperCase(),
                String.format("Retrieved %d %s entities", entityPage.getNumberOfElements(), entityName));

        return success(pageResponse);
    }

    /**
     * Get entity by ID
     */
    @Operation(summary = "Get entity by ID", description = "Retrieve a specific entity by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entity found"),
            @ApiResponse(responseCode = "404", description = "Entity not found")
    })
    @GetMapping("/{id}")
    @AccessControl(level = AccessControl.AccessLevel.OWNER_OR_ADMIN, entityType = BaseEntity.class)
    public ResponseEntity<StandardResponse<ResponseDto>> getById(
            @Parameter(description = "Entity ID", required = true)
            @PathVariable ID id) {

        log.debug("Getting {} with id: {}", entityName, id);

        // Use the getCachedEntityOrFetch helper method for optimal caching
        T entity = getEntityWithCache(id);
        
        ResponseDto dto = mapper.toDto(entity);

        logAction("GET_" + entityName.toUpperCase(),
                String.format("Retrieved %s with id: %s", entityName, id));

        return success(dto);
    }

    /**
     * Create new entity
     */
    @Operation(summary = "Create new entity", description = "Create a new entity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Entity created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Entity already exists")
    })
    @PostMapping
    @AccessControl(level = AccessControl.AccessLevel.AUTHENTICATED, entityType = BaseEntity.class)
    public ResponseEntity<StandardResponse<ResponseDto>> create(
            @Parameter(description = "Entity data", required = true)
            @Valid @RequestBody CreateDto createDto) {

        log.debug("Creating new {}: {}", entityName, createDto);

        T entity = mapper.toEntity(createDto);
        T savedEntity = service.create(entity);
        ResponseDto responseDto = mapper.toDto(savedEntity);

        logEntityAction("CREATE", entityName, savedEntity.getId().toString(),
                String.format("Created new %s", entityName));

        return created(responseDto, "/{id}", savedEntity.getId());
    }

    /**
     * Update existing entity
     */
    @Operation(summary = "Update entity", description = "Update an existing entity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entity updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Entity not found")
    })
    @PutMapping("/{id}")
    @AccessControl(level = AccessControl.AccessLevel.ADMIN_ONLY, entityType = BaseEntity.class)
    public ResponseEntity<StandardResponse<ResponseDto>> update(
            @Parameter(description = "Entity ID", required = true)
            @PathVariable ID id,

            @Parameter(description = "Updated entity data", required = true)
            @Valid @RequestBody UpdateDto updateDto) {

        log.debug("Updating {} with id: {} - {}", entityName, id, updateDto);

        // Access validation is handled by @AccessControl annotation
        T entity = mapper.toEntity(updateDto);
        T updatedEntity = service.update(id, entity);
        ResponseDto responseDto = mapper.toDto(updatedEntity);

        logEntityAction("UPDATE", entityName, id.toString(),
                String.format("Updated %s", entityName));

        return success(responseDto, entityName + " updated successfully");
    }

    /**
     * Partial update of entity
     */
    @Operation(summary = "Partially update entity", description = "Partially update an existing entity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entity updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Entity not found")
    })
    @PatchMapping("/{id}")
    @AccessControl(level = AccessControl.AccessLevel.ADMIN_ONLY, entityType = BaseEntity.class)
    public ResponseEntity<StandardResponse<ResponseDto>> partialUpdate(
            @Parameter(description = "Entity ID", required = true)
            @PathVariable ID id,

            @Parameter(description = "Partial entity data", required = true)
            @RequestBody UpdateDto updateDto) {

        log.debug("Partially updating {} with id: {} - {}", entityName, id, updateDto);

        // Use the getCachedEntityOrFetch helper method for optimal caching
        T existingEntity = getEntityWithCache(id);
        
        T updatedEntity = service.save(existingEntity);
        ResponseDto responseDto = mapper.toDto(updatedEntity);

        logEntityAction("PATCH", entityName, id.toString(),
                String.format("Partially updated %s", entityName));

        return success(responseDto, entityName + " updated successfully");
    }

    /**
     * Delete entity by ID
     */
    @Operation(summary = "Delete entity", description = "Delete an entity by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Entity deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "409", description = "Entity cannot be deleted due to constraints")
    })
    @DeleteMapping("/{id}")
    @AccessControl(level = AccessControl.AccessLevel.ADMIN_ONLY, entityType = BaseEntity.class)
    public ResponseEntity<Void> delete(
            @Parameter(description = "Entity ID", required = true)
            @PathVariable ID id) {

        log.debug("Deleting {} with id: {}", entityName, id);

        service.deleteById(id);

        logEntityAction("DELETE", entityName, id.toString(),
                String.format("Deleted %s", entityName));

        return noContent();
    }

    // ==============================================
    // 2. OTHER ENDPOINTS (Utility Operations)
    // ==============================================

    /**
     * Check if entity exists
     */
    @Operation(summary = "Check if entity exists", description = "Check if an entity exists by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Check completed"),
            @ApiResponse(responseCode = "404", description = "Entity not found")
    })
    @GetMapping("/{id}/exists")
    @AccessControl(level = AccessControl.AccessLevel.ADMIN_ONLY, entityType = BaseEntity.class)
    public ResponseEntity<StandardResponse<Boolean>> exists(
            @Parameter(description = "Entity ID", required = true)
            @PathVariable ID id) {

        log.debug("Checking if {} exists with id: {}", entityName, id);

        // Use cache-optimized exists check
        boolean exists = entityExistsWithCache(id);

        return success(exists);
    }

    /**
     * Get entity count
     */
    @Operation(summary = "Get entity count", description = "Get the total count of entities")
    @ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    @GetMapping("/count")
    @AccessControl(level = AccessControl.AccessLevel.ADMIN_ONLY, entityType = BaseEntity.class)
    public ResponseEntity<StandardResponse<Long>> count() {
        log.debug("Getting count of {}", entityName);

        long count = service.count();

        return success(count);
    }

    // ==============================================
    // 3. ABSTRACT METHODS (Subclass Implementation Required)
    // ==============================================


    /**
     * Get allowed sort fields for validation
     */
    protected abstract String[] getAllowedSortFields();

    /**
     * Get the entity class for proper type-safe caching
     * This method should return the actual entity type, not BaseEntity
     */
    protected abstract Class<T> getEntityClass();

    // ==============================================
    // 4. HELPER METHODS (Internal Utilities)
    // ==============================================

    /**
     * Get service instance
     */
    protected S getService() {
        return service;
    }

    /**
     * Get entity name
     */
    protected String getEntityName() {
        return entityName;
    }

    /**
     * Get entity by ID with cache optimization
     * This method first checks the cache and only fetches from database if not found
     */
    protected T getEntityWithCache(ID id) {
        return getCachedEntityOrFetch(
            getEntityClass(),
            id,
            () -> service.getById(id)
        );
    }

    /**
     * Check if entity exists by ID with cache optimization
     * If entity is in cache, we know it exists without database call
     */
    protected boolean entityExistsWithCache(ID id) {
        T cachedEntity = getCachedEntity(getEntityClass(), id);
        if (cachedEntity != null) {
            log.debug("Entity exists in cache: {}", id);
            return true;
        }
        return service.existsById(id);
    }
}