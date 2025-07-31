package com.shoestore.repository.base;

import com.shoestore.entity.base.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Base repository interface providing common database operations
 *
 * @param <T> Entity type extending BaseEntity
 * @param <ID> Entity ID type
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID> extends JpaRepository<T, ID> {

    /**
     * Find entities created between two dates
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.createdAt BETWEEN :startDate AND :endDate")
    List<T> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);

    /**
     * Find entities created after a specific date
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.createdAt >= :date")
    List<T> findByCreatedAtAfter(@Param("date") LocalDateTime date);

    /**
     * Find entities created before a specific date
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.createdAt <= :date")
    List<T> findByCreatedAtBefore(@Param("date") LocalDateTime date);

    /**
     * Find entities updated after a specific date
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.updatedAt >= :date")
    List<T> findByUpdatedAtAfter(@Param("date") LocalDateTime date);

    /**
     * Find entities created by a specific user
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.createdBy = :createdBy")
    List<T> findByCreatedBy(@Param("createdBy") String createdBy);

    /**
     * Find entities updated by a specific user
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.updatedBy = :updatedBy")
    List<T> findByUpdatedBy(@Param("updatedBy") String updatedBy);

    /**
     * Find entities created today
     */
    @Query("SELECT e FROM #{#entityName} e WHERE CAST(e.createdAt AS date) = CURRENT_DATE")
    List<T> findCreatedToday();

    /**
     * Find entities created this week
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.createdAt >= :weekStart")
    List<T> findCreatedThisWeek(@Param("weekStart") LocalDateTime weekStart);

    /**
     * Find entities created this month
     */
    @Query("SELECT e FROM #{#entityName} e WHERE " +
            "EXTRACT(YEAR FROM e.createdAt) = EXTRACT(YEAR FROM CURRENT_DATE) AND " +
            "EXTRACT(MONTH FROM e.createdAt) = EXTRACT(MONTH FROM CURRENT_DATE)")
    List<T> findCreatedThisMonth();

    /**
     * Find entities created this year
     */
    @Query("SELECT e FROM #{#entityName} e WHERE EXTRACT(YEAR FROM e.createdAt) = EXTRACT(YEAR FROM CURRENT_DATE)")
    List<T> findCreatedThisYear();

    /**
     * Count entities created between two dates
     */
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.createdAt BETWEEN :startDate AND :endDate")
    long countByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    /**
     * Count entities created today
     */
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE CAST(e.createdAt AS date) = CURRENT_DATE")
    long countCreatedToday();

    /**
     * Count entities created this week
     */
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.createdAt >= :weekStart")
    long countCreatedThisWeek(@Param("weekStart") LocalDateTime weekStart);

    /**
     * Count entities created this month
     */
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE " +
            "EXTRACT(YEAR FROM e.createdAt) = EXTRACT(YEAR FROM CURRENT_DATE) AND " +
            "EXTRACT(MONTH FROM e.createdAt) = EXTRACT(MONTH FROM CURRENT_DATE)")
    long countCreatedThisMonth();

    /**
     * Find latest entities (most recently created)
     */
    @Query("SELECT e FROM #{#entityName} e ORDER BY e.createdAt DESC")
    Page<T> findLatest(Pageable pageable);

    /**
     * Find recently updated entities
     */
    @Query("SELECT e FROM #{#entityName} e ORDER BY e.updatedAt DESC")
    Page<T> findRecentlyUpdated(Pageable pageable);

    /**
     * Find entities with specific version (for optimistic locking)
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.version = :version")
    List<T> findByVersion(@Param("version") Long version);

    /**
     * Find entities with version greater than specified
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.version > :version")
    List<T> findByVersionGreaterThan(@Param("version") Long version);

    /**
     * Soft delete (if your entities support it)
     * This is a generic approach - implement if your entities have a 'deleted' flag
     */
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.updatedAt = CURRENT_TIMESTAMP WHERE e.id = :id")
    void touch(@Param("id") ID id);

    /**
     * Bulk update created by field
     */
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.updatedBy = :updatedBy, e.updatedAt = CURRENT_TIMESTAMP WHERE e.id IN :ids")
    void bulkUpdateUpdatedBy(@Param("ids") List<ID> ids, @Param("updatedBy") String updatedBy);

    /**
     * Find entities ordered by creation date (ascending)
     */
    @Query("SELECT e FROM #{#entityName} e ORDER BY e.createdAt ASC")
    List<T> findAllOrderByCreatedAtAsc();

    /**
     * Find entities ordered by creation date (descending)
     */
    @Query("SELECT e FROM #{#entityName} e ORDER BY e.createdAt DESC")
    List<T> findAllOrderByCreatedAtDesc();

    /**
     * Find entities ordered by update date (descending)
     */
    @Query("SELECT e FROM #{#entityName} e ORDER BY e.updatedAt DESC")
    List<T> findAllOrderByUpdatedAtDesc();

    /**
     * Find entities by ID list ordered by creation date
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.id IN :ids ORDER BY e.createdAt DESC")
    List<T> findByIdInOrderByCreatedAtDesc(@Param("ids") List<ID> ids);

    /**
     * Check if entity was created after a specific date
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM #{#entityName} e WHERE e.id = :id AND e.createdAt > :date")
    boolean isCreatedAfter(@Param("id") ID id, @Param("date") LocalDateTime date);

    /**
     * Check if entity was updated after a specific date
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM #{#entityName} e WHERE e.id = :id AND e.updatedAt > :date")
    boolean isUpdatedAfter(@Param("id") ID id, @Param("date") LocalDateTime date);

    /**
     * Get creation and update statistics
     */
    @Query("SELECT " +
            "COUNT(e) as total, " +
            "COUNT(CASE WHEN CAST(e.createdAt AS date) = CURRENT_DATE THEN 1 END) as createdToday, " +
            "COUNT(CASE WHEN e.createdAt >= :weekStart THEN 1 END) as createdThisWeek, " +
            "COUNT(CASE WHEN EXTRACT(YEAR FROM e.createdAt) = EXTRACT(YEAR FROM CURRENT_DATE) AND EXTRACT(MONTH FROM e.createdAt) = EXTRACT(MONTH FROM CURRENT_DATE) THEN 1 END) as createdThisMonth " +
            "FROM #{#entityName} e")
    Object[] getCreationStatistics(@Param("weekStart") LocalDateTime weekStart);

    /**
     * Find first entity (oldest)
     */
    @Query("SELECT e FROM #{#entityName} e ORDER BY e.createdAt ASC")
    Optional<T> findFirst();

    /**
     * Find last entity (newest)
     */
    @Query("SELECT e FROM #{#entityName} e ORDER BY e.createdAt DESC")
    Optional<T> findLast();

    /**
     * Get audit trail (creation and modification info)
     */
    @Query("SELECT e.id, e.createdAt, e.createdBy, e.updatedAt, e.updatedBy, e.version FROM #{#entityName} e WHERE e.id = :id")
    Object[] getAuditInfo(@Param("id") ID id);

    /**
     * Find entities that haven't been updated for a specific period
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.updatedAt < :cutoffDate")
    List<T> findStaleEntities(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Find entities created or updated by a specific user
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.createdBy = :user OR e.updatedBy = :user")
    List<T> findByCreatedByOrUpdatedBy(@Param("user") String user);
}