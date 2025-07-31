package com.shoestore.repository.shoe;

import com.shoestore.entity.shoe.ShoeModel;
import com.shoestore.entity.shoe.Brand;
import com.shoestore.entity.shoe.ShoeCategory;
import com.shoestore.repository.base.BaseRepository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for ShoeModel entity operations
 */
@Repository
public interface ShoeModelRepository extends BaseRepository<ShoeModel, Long>, JpaSpecificationExecutor<ShoeModel> {

    /**
     * Find distinct colors for active models
     */
    @Query("SELECT DISTINCT sm.color FROM ShoeModel sm WHERE sm.isActive = true AND sm.color IS NOT NULL")
    List<String> findDistinctColorsByIsActiveTrue();

    /**
     * Count active models by brand
     */
    @Query("SELECT COUNT(sm) FROM ShoeModel sm WHERE sm.shoe.brand = :brand AND sm.isActive = true")
    long countByShoe_BrandAndIsActiveTrue(@Param("brand") Brand brand);

    /**
     * Count active models by category
     */
    @Query("SELECT COUNT(sm) FROM ShoeModel sm WHERE sm.shoe.category = :category AND sm.isActive = true")
    long countByShoe_CategoryAndIsActiveTrue(@Param("category") ShoeCategory category);
}
