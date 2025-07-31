package com.shoestore.exception;

public class OrderProcessingException extends RuntimeException {

    private final Long orderId;
    private final String currentStatus;
    private final String attemptedAction;

    public OrderProcessingException(String message) {
        super(message);
        this.orderId = null;
        this.currentStatus = null;
        this.attemptedAction = null;
    }

    public OrderProcessingException(Long orderId, String currentStatus, String attemptedAction) {
        super(String.format("Cannot %s order %d in status %s", attemptedAction, orderId, currentStatus));
        this.orderId = orderId;
        this.currentStatus = currentStatus;
        this.attemptedAction = attemptedAction;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public String getAttemptedAction() {
        return attemptedAction;
    }
}