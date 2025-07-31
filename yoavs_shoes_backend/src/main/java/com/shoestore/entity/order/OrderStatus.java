package com.shoestore.entity.order;

/**
 * Enumeration representing the possible statuses of an order
 */
public enum OrderStatus {
    /**
     * Order has been created but not yet confirmed
     */
    PENDING("Order is pending confirmation"),
    
    /**
     * Order has been confirmed and is awaiting processing
     */
    CONFIRMED("Order has been confirmed"),
    
    /**
     * Order is being processed/prepared
     */
    PROCESSING("Order is being processed"),
    
    /**
     * Order has been shipped to the customer
     */
    SHIPPED("Order has been shipped"),
    
    /**
     * Order has been delivered to the customer
     */
    DELIVERED("Order has been delivered"),
    
    /**
     * Order has been cancelled
     */
    CANCELLED("Order has been cancelled");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if the order status allows for cancellation
     */
    public boolean canBeCancelled() {
        return this == PENDING || this == CONFIRMED;
    }

    /**
     * Check if the order status indicates completion
     */
    public boolean isCompleted() {
        return this == DELIVERED || this == CANCELLED;
    }

    /**
     * Check if the order status indicates it's in progress
     */
    public boolean isInProgress() {
        return this == PROCESSING || this == SHIPPED;
    }
}
