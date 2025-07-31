package com.shoestore.util;

import com.shoestore.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Utility class for generating unique 7-digit order numbers
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderNumberGenerator {

    private final OrderRepository orderRepository;
    private final Random random = new SecureRandom();

    /**
     * Generate a unique 7-digit order number
     * Format: 1000000 - 9999999
     */
    public String generateOrderNumber() {
        int maxAttempts = 100; // Prevent infinite loops
        int attempts = 0;
        
        while (attempts < maxAttempts) {
            // Generate random 7-digit number (1000000 - 9999999)
            int orderNumber = 1000000 + random.nextInt(9000000);
            String orderNumberStr = String.valueOf(orderNumber);
            
            // Check if this number already exists
            if (!orderRepository.existsByOrderNumber(orderNumberStr)) {
                log.debug("Generated unique order number: {}", orderNumberStr);
                return orderNumberStr;
            }
            
            attempts++;
            log.debug("Order number {} already exists, attempt {}/{}", orderNumberStr, attempts, maxAttempts);
        }
        
        // Fallback: if we can't generate a unique 7-digit number, 
        // use timestamp-based approach with 7 digits
        long timestamp = System.currentTimeMillis();
        String fallbackNumber = String.valueOf(timestamp % 10000000L + 1000000L);
        
        log.warn("Could not generate unique 7-digit order number after {} attempts, using fallback: {}", 
                maxAttempts, fallbackNumber);
        
        return fallbackNumber;
    }

    /**
     * Validate that an order number is in the correct 7-digit format
     */
    public boolean isValidOrderNumber(String orderNumber) {
        if (orderNumber == null || orderNumber.length() != 7) {
            return false;
        }
        
        try {
            int number = Integer.parseInt(orderNumber);
            return number >= 1000000 && number <= 9999999;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
