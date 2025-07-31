package com.shoestore.repository.shoe;

import com.shoestore.entity.shoe.Shoe;

import com.shoestore.repository.base.BaseRepository;

import org.springframework.stereotype.Repository;

/**
 * Repository interface for Shoe entity operations
 */
@Repository
public interface ShoeRepository extends BaseRepository<Shoe, Long> {

}
