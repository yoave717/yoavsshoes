package com.shoestore.repository.shoe;

import com.shoestore.entity.shoe.ShoeCategory;
import com.shoestore.repository.base.BaseRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for ShoeCategory entity operations
 */
@Repository
public interface ShoeCategoryRepository extends BaseRepository<ShoeCategory, Long> {

    /**
     * Find all active categories
     */
    List<ShoeCategory> findByIsActiveTrue();
}
