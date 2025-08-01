package com.shoestore.repository.shoe;

import com.shoestore.entity.shoe.IShoeInventoryView;
import com.shoestore.entity.shoe.Shoe;
import com.shoestore.repository.base.BaseRepository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Shoe entity operations
 */
@Repository
public interface ShoeRepository extends BaseRepository<Shoe, Long>, JpaSpecificationExecutor<Shoe> {

    List<Shoe> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);

    
    /**
     * Get shoes with aggregated model and stock information
     */
    @Query(
        "SELECT s as shoe, " +
        "COUNT(DISTINCT sm.id) as modelCount, " +
        "COALESCE(SUM(si.quantityAvailable), 0) as totalStock " +
        "FROM Shoe s " +
        "LEFT JOIN s.shoeModels sm ON sm.isActive = true " +
        "LEFT JOIN sm.availableSizes si " +
        "WHERE s.isActive = true " +
        "AND (:brandIds IS NULL OR s.brand.id IN :brandIds) " +
        "AND (:categoryIds IS NULL OR s.category.id IN :categoryIds) " +
        "AND (:searchTerm IS NULL OR :searchTerm = '' OR " +
        "LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
        "LOWER(s.brand.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
        "AND (:gender IS NULL OR :gender = '' OR s.gender = :gender) " +
        "AND (:minPrice IS NULL OR s.basePrice >= :minPrice) " +
        "AND (:maxPrice IS NULL OR s.basePrice <= :maxPrice) " +
        "GROUP BY s "
    )
    Page<IShoeInventoryView> findShoesWithStockInfo(
            @Param("brandIds") List<Long> brandIds,
            @Param("categoryIds") List<Long> categoryIds,
            @Param("searchTerm") String searchTerm,
            @Param("gender") Shoe.Gender gender,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    /*
     * Count shoe models with available stock less than a specified threshold
     * available stock is determined by the ShoeInventory entity, each shoe model has a list of available sizes
     */
    @Query("SELECT COUNT(s) " +
           "FROM Shoe s " +
           "JOIN s.shoeModels sm " +
           "JOIN sm.availableSizes si " +
           "WHERE si.quantityAvailable < :threshold AND s.isActive = true")
    long countShoeModelsWithLowStock(@Param("threshold") int threshold);

}
