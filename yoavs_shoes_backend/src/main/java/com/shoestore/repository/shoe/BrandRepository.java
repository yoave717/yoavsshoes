package com.shoestore.repository.shoe;

import com.shoestore.entity.shoe.Brand;
import com.shoestore.repository.base.BaseRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Brand entity operations
 */
@Repository
public interface BrandRepository extends BaseRepository<Brand, Long> {

    /**
     * Find all active brands
     */
    List<Brand> findByIsActiveTrue();
}
