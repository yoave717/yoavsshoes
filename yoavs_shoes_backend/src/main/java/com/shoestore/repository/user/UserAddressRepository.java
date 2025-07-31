package com.shoestore.repository.user;

import com.shoestore.entity.user.UserAddress;
import com.shoestore.repository.base.BaseRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for UserAddress entity operations
 */
@Repository
public interface UserAddressRepository extends BaseRepository<UserAddress, Long> {

    /**
     * Find all addresses for a user ordered by default status and creation date
     */
    @Query("SELECT ua FROM UserAddress ua WHERE ua.user.id = :userId " +
            "ORDER BY ua.isDefault DESC, ua.createdAt ASC")
    List<UserAddress> findByUserIdOrderByDefaultAndCreatedAt(@Param("userId") Long userId);

    /**
     * Check if user has a default address
     */
    boolean existsByUserIdAndIsDefaultTrue(Long userId);

    /**
     * Count addresses for a user
     */
    long countByUserId(Long userId);

    /**
     * Set all addresses for a user as non-default
     */
    @Modifying
    @Query("UPDATE UserAddress ua SET ua.isDefault = false WHERE ua.user.id = :userId")
    void clearDefaultAddressForUser(@Param("userId") Long userId);


    /**
     * Find address by ID with user relationship eagerly loaded
     */
    @Query("SELECT ua FROM UserAddress ua JOIN FETCH ua.user WHERE ua.id = :id")
    Optional<UserAddress> findByIdWithUser(@Param("id") Long id);

}