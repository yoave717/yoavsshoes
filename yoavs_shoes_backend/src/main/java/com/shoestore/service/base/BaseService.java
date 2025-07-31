package com.shoestore.service.base;

import com.shoestore.entity.base.BaseEntity;
import com.shoestore.exception.ResourceNotFoundException;
import com.shoestore.repository.base.BaseRepository;
import com.shoestore.util.LoggingUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Base service class providing common CRUD operations for all services
 *
 * @param <T> Entity type
 * @param <ID> Entity ID type
 * @param <R> Repository type
 */
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public abstract class BaseService<T extends BaseEntity, ID, R extends BaseRepository<T, ID>> {

    protected final R repository;
    protected final String entityName;

    /**
     * Find entity by ID
     */
    public Optional<T> findById(ID id) {
        log.debug("Finding {} with id: {}", entityName, id);
        return repository.findById(id);
    }

    /**
     * Get entity by ID or throw exception
     */
    public T getById(ID id) {
        return findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(entityName, id.toString()));
    }

    /**
     * Find all entities
     */
    public List<T> findAll() {
        log.debug("Finding all {}", entityName);
        return repository.findAll();
    }

    /**
     * Find all entities with pagination
     */
    public Page<T> findAll(Pageable pageable) {
        log.debug("Finding all {} with pagination: {}", entityName, pageable);
        return repository.findAll(pageable);
    }

    /**
     * Check if entity exists by ID
     */
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

    /**
     * Count all entities
     */
    public long count() {
        return repository.count();
    }

    /**
     * Create new entity
     */
    @Transactional
    public T create(T entity) {
        log.debug("Creating new {}: {}", entityName, entity);

        validateBeforeCreate(entity);
        T savedEntity = repository.save(entity);

        LoggingUtil.logEntityOperation(
                "CREATE",
                entityName,
                savedEntity.getId().toString(),
                "Created new " + entityName
        );

        log.info("Created {} with id: {}", entityName, savedEntity.getId());
        return savedEntity;
    }

    /**
     * Update existing entity
     */
    @Transactional
    public T update(ID id, T entity) {
        log.debug("Updating {} with id: {}", entityName, id);

        T existingEntity = getById(id);
        validateBeforeUpdate(existingEntity, entity);

        updateEntityFields(existingEntity, entity);
        T savedEntity = repository.save(existingEntity);

        LoggingUtil.logEntityOperation(
                "UPDATE",
                entityName,
                id.toString(),
                "Updated " + entityName
        );

        log.info("Updated {} with id: {}", entityName, id);
        return savedEntity;
    }

    /**
     * Delete entity by ID
     */
    @Transactional
    public void deleteById(ID id) {
        log.debug("Deleting {} with id: {}", entityName, id);

        T entity = getEntityById(id); // Ensure entity exists
        validateBeforeDelete(entity);

        repository.deleteById(id);

        LoggingUtil.logEntityOperation(
                "DELETE",
                entityName,
                id.toString(),
                "Deleted " + entityName
        );

        log.info("Deleted {} with id: {}", entityName, id);
    }

    /**
     * Delete entity
     */
    @Transactional
    public void delete(T entity) {
        log.debug("Deleting {}: {}", entityName, entity);

        validateBeforeDelete(entity);
        repository.delete(entity);

        LoggingUtil.logEntityOperation(
                "DELETE",
                entityName,
                entity.getId().toString(),
                "Deleted " + entityName
        );

        log.info("Deleted {} with id: {}", entityName, entity.getId());
    }

    /**
     * Save entity (create or update)
     */
    @Transactional
    public T save(T entity) {
        boolean isNew = entity.getId() == null;
        String operation = isNew ? "CREATE" : "UPDATE";

        log.debug("Saving {}: {}", entityName, entity);

        if (isNew) {
            validateBeforeCreate(entity);
        } else {
            @SuppressWarnings("unchecked")
            T existingEntity = getById((ID) entity.getId());
            validateBeforeUpdate(existingEntity, entity);
        }

        T savedEntity = repository.save(entity);

        LoggingUtil.logEntityOperation(
                operation,
                entityName,
                savedEntity.getId().toString(),
                operation.toLowerCase() + "d " + entityName
        );

        log.info("{} {} with id: {}", operation.toLowerCase() + "d", entityName, savedEntity.getId());
        return savedEntity;
    }

    /**
     * Save all entities
     */
    @Transactional
    public List<T> saveAll(List<T> entities) {
        log.debug("Saving {} {} entities", entities.size(), entityName);

        entities.forEach(this::validateBeforeCreate);
        List<T> savedEntities = repository.saveAll(entities);

        LoggingUtil.logEntityOperation(
                "BULK_CREATE",
                entityName,
                String.valueOf(entities.size()),
                "Bulk created " + entities.size() + " " + entityName + " entities"
        );

        log.info("Saved {} {} entities", savedEntities.size(), entityName);
        return savedEntities;
    }

    /**
     * Delete all entities
     */
    @Transactional
    public void deleteAll() {
        log.warn("Deleting all {} entities", entityName);

        long count = repository.count();
        repository.deleteAll();

        LoggingUtil.logEntityOperation(
                "DELETE_ALL",
                entityName,
                String.valueOf(count),
                "Deleted all " + count + " " + entityName + " entities"
        );

        log.warn("Deleted all {} {} entities", count, entityName);
    }

    /**
     * Find entities by IDs
     */
    public List<T> findAllById(Iterable<ID> ids) {
        log.debug("Finding {} entities by IDs", entityName);
        return repository.findAllById(ids);
    }

    // Protected methods for subclasses to override

    /**
     * Validate entity before creation
     * Override in subclasses for specific validation logic
     */
    protected void validateBeforeCreate(T entity) {
        // Default implementation - no validation
        log.debug("Validating {} before creation", entityName);
    }

    /**
     * Validate entity before update
     * Override in subclasses for specific validation logic
     */
    protected void validateBeforeUpdate(T existingEntity, T newEntity) {
        // Default implementation - no validation
        log.debug("Validating {} before update", entityName);
    }

    /**
     * Validate entity before deletion
     * Override in subclasses for specific validation logic
     */
    protected void validateBeforeDelete(T entity) {
        // Default implementation - no validation
        log.debug("Validating {} before deletion", entityName);
    }

    /**
     * Update entity fields during update operation
     * Override in subclasses to implement specific field updates
     */
    protected abstract void updateEntityFields(T existingEntity, T newEntity);

    /**
     * Get repository instance
     */
    protected R getRepository() {
        return repository;
    }

    /**
     * Get entity name
     */
    protected String getEntityName() {
        return entityName;
    }

    /**
     * Get entity by ID (internal use - returns entity, not DTO)
     */
    protected T getEntityById(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(entityName, id.toString()));
    }
}