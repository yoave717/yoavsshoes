package com.shoestore.security.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Thread-local cache for storing entities fetched during access control validation
 * This prevents duplicate database calls when the controller method needs the same entity
 */
@Component
@Slf4j
public class EntityCache {

    private static final ThreadLocal<Map<String, Object>> entityCache = 
        ThreadLocal.withInitial(HashMap::new);

    /**
     * Store an entity in the cache with a key
     */
    public void put(String key, Object entity) {
        log.debug("Caching entity with key: {}", key);
        entityCache.get().put(key, entity);
    }

    /**
     * Retrieve an entity from the cache
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> entityType) {
        Object entity = entityCache.get().get(key);
        if (entity != null && entityType.isInstance(entity)) {
            log.debug("Retrieved cached entity with key: {}", key);
            return (T) entity;
        }
        return null;
    }

    /**
     * Check if an entity exists in the cache
     */
    public boolean contains(String key) {
        return entityCache.get().containsKey(key);
    }

    /**
     * Remove an entity from the cache
     */
    public void remove(String key) {
        entityCache.get().remove(key);
        log.debug("Removed cached entity with key: {}", key);
    }

    /**
     * Clear all cached entities for the current thread
     * This should be called after request processing is complete
     */
    public void clear() {
        Map<String, Object> cache = entityCache.get();
        if (!cache.isEmpty()) {
            log.debug("Clearing entity cache with {} entries", cache.size());
            cache.clear();
        }
    }

    /**
     * Remove the ThreadLocal to prevent memory leaks
     * This should be called after request processing is complete
     */
    public void cleanup() {
        clear();
        entityCache.remove();
    }

    /**
     * Generate a cache key for an entity by type and ID
     */
    public static String generateKey(Class<?> entityType, Object id) {
        return entityType.getSimpleName() + ":" + id;
    }

    /**
     * Generate a cache key for an entity by type name and ID
     */
    public static String generateKey(String entityTypeName, Object id) {
        return entityTypeName + ":" + id;
    }
}
