package com.shoestore.exception;

public class InsufficientStockException extends RuntimeException {

    private final Long productId;
    private final String size;
    private final int requestedQuantity;
    private final int availableQuantity;

    public InsufficientStockException(String message) {
        super(message);
        this.productId = null;
        this.size = null;
        this.requestedQuantity = 0;
        this.availableQuantity = 0;
    }

    public InsufficientStockException(Long productId, String size, int requestedQuantity, int availableQuantity) {
        super(String.format("Insufficient stock for product %d (size %s): requested %d, available %d",
                productId, size, requestedQuantity, availableQuantity));
        this.productId = productId;
        this.size = size;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    public Long getProductId() {
        return productId;
    }

    public String getSize() {
        return size;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }
}