package com.shoestore.repository.order;

import com.shoestore.entity.order.OrderItem;
import com.shoestore.repository.base.BaseRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for OrderItem entity operations
 */
@Repository
public interface OrderItemRepository extends BaseRepository<OrderItem, Long> {

    /**
     * Find all order items for a specific order ID
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);

}
