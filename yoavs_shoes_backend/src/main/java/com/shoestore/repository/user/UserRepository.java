package com.shoestore.repository.user;

import com.shoestore.entity.user.User;
import com.shoestore.repository.base.BaseRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository interface for User entity operations
 */
@Repository
public interface UserRepository extends BaseRepository<User, Long> {

    /**
     * Find user by email address (used for authentication)
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by email address (case insensitive)
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Check if email exists (case insensitive)
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Search users by multiple criteria
     */
    @Query("SELECT u FROM User u WHERE " +
            "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
            "(:firstName IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
            "(:lastName IS NULL OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
            "(:isAdmin IS NULL OR u.isAdmin = :isAdmin)")
    Page<User> searchUsers(
            @Param("email") String email,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("isAdmin") Boolean isAdmin,
            Pageable pageable
    );

    /**
     * Count users by admin status
     */
    long countByIsAdmin(Boolean isAdmin);

    /**
     * Lock user account until specified time
     */
    @Modifying
    @Query("UPDATE User u SET u.accountLockedUntil = :lockUntil WHERE u.id = :userId")
    void lockUserAccount(@Param("userId") Long userId, @Param("lockUntil") LocalDateTime lockUntil);

}