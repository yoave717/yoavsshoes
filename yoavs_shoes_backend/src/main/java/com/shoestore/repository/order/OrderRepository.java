package com.shoestore.repository.order;

import com.shoestore.entity.order.Order;
import com.shoestore.entity.order.OrderStatus;
import com.shoestore.repository.base.BaseRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Order entity operations
 */
@Repository
public interface OrderRepository extends BaseRepository<Order, Long> {

    /**
     * Find orders by user ID
     */
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.orderDate DESC")
    Page<Order> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Find orders by status
     */
    @Query("SELECT o FROM Order o WHERE o.status = :status ORDER BY o.orderDate DESC")
    Page<Order> findByStatus(@Param("status") OrderStatus status, Pageable pageable);

    /**
     * Find order by ID with user relationship for access control
     */
    @Query("SELECT o FROM Order o JOIN FETCH o.user WHERE o.id = :id")
    Optional<Order> findByIdWithUser(@Param("id") Long id);

    /**
     * Count orders by status
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") OrderStatus status);
 
    /**
     * Check if order number exists
     */
    boolean existsByOrderNumber(String orderNumber);
}
