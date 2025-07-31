package com.shoestore.repository.shoe;

import com.shoestore.entity.shoe.ShoeInventory;
import com.shoestore.entity.shoe.ShoeModel;
import com.shoestore.repository.base.BaseRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ShoeInventory entity operations
 */
@Repository
public interface ShoeInventoryRepository extends BaseRepository<ShoeInventory, Long> {

    /**
     * Find inventory by shoe model and size
     */
    Optional<ShoeInventory> findByShoeModelAndSize(ShoeModel shoeModel, String size);

    /**
     * Find all inventory for a shoe model
     */
    List<ShoeInventory> findByShoeModel(ShoeModel shoeModel);

    /**
     * Find available sizes for a shoe model
     */
    @Query("SELECT si FROM ShoeInventory si WHERE si.shoeModel = :shoeModel AND si.quantityAvailable > 0")
    List<ShoeInventory> findAvailableByShoeModel(@Param("shoeModel") ShoeModel shoeModel);
}
