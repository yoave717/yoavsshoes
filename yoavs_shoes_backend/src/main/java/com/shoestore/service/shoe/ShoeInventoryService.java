package com.shoestore.service.shoe;

import com.shoestore.entity.shoe.ShoeInventory;
import com.shoestore.entity.shoe.ShoeModel;
import com.shoestore.exception.ResourceNotFoundException;
import com.shoestore.repository.shoe.ShoeInventoryRepository;
import com.shoestore.service.base.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing shoe inventory
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ShoeInventoryService extends BaseService<ShoeInventory, Long, ShoeInventoryRepository> {

    private final ShoeModelService shoeModelService;

    public ShoeInventoryService(ShoeInventoryRepository repository, ShoeModelService shoeModelService) {
        super(repository, "ShoeInventory");
        this.shoeModelService = shoeModelService;
    }

    @Override
    protected void updateEntityFields(ShoeInventory existingEntity, ShoeInventory newEntity) {
        existingEntity.setQuantityAvailable(newEntity.getQuantityAvailable());
        existingEntity.setQuantityReserved(newEntity.getQuantityReserved());
    }

    /**
     * Get inventory by shoe model and size
     */
    public ShoeInventory getInventoryByShoeModelAndSize(Long shoeModelId, String size) {
        log.debug("Getting inventory for shoe model {} and size {}", shoeModelId, size);

        ShoeModel shoeModel = shoeModelService.getById(shoeModelId);

        return repository.findByShoeModelAndSize(shoeModel, size).orElseThrow(() ->
                new ResourceNotFoundException("ShoeInventory", "shoeModelId=" + shoeModelId + ", size=" + size));
    }

    /**
     * Get all inventory for a shoe model
     */
    public List<ShoeInventory> getInventoryByShoeModel(Long shoeModelId) {
        log.debug("Getting all inventory for shoe model {}", shoeModelId);

        ShoeModel shoeModel = shoeModelService.getById(shoeModelId);

        return repository.findByShoeModel(shoeModel);
    }

    /**
     * Get available inventory for a shoe model
     */
    public List<ShoeInventory> getAvailableInventoryByShoeModel(Long shoeModelId) {
        log.debug("Getting available inventory for shoe model {}", shoeModelId);

        ShoeModel shoeModel = shoeModelService.getById(shoeModelId);

        return repository.findAvailableByShoeModel(shoeModel);
    }

    /**
     * Get total available stock across all shoe models
     */
    public Long getTotalAvailableStock() {
        log.debug("Getting total available stock across all shoe models");
        return repository.getTotalAvailableStock();
    }

    /**
     * Reserve inventory for an order
     */
    @Transactional
    public boolean reserveInventory(Long shoeModelId, String size, Integer quantity) {
        log.debug("Reserving inventory: shoeModelId={}, size={}, quantity={}", shoeModelId, size, quantity);
        
        ShoeModel shoeModel = shoeModelService.getById(shoeModelId);
        
        Optional<ShoeInventory> inventoryOpt = repository.findByShoeModelAndSize(shoeModel, size);
        if (inventoryOpt.isEmpty()) {
            log.warn("No inventory found for shoe model {} and size {}", shoeModelId, size);
            return false;
        }
        
        ShoeInventory inventory = inventoryOpt.get();
        Integer availableQuantity = inventory.getActualAvailableQuantity();
        
        if (availableQuantity < quantity) {
            log.warn("Insufficient inventory: requested={}, available={}", quantity, availableQuantity);
            return false;
        }
        
        inventory.setQuantityReserved(inventory.getQuantityReserved() + quantity);
        repository.save(inventory);
        
        log.info("Reserved {} units for shoe model {} size {}. New reserved total: {}", 
                quantity, shoeModelId, size, inventory.getQuantityReserved());
        
        return true;
    }

    /**
     * Release reserved inventory 
     */
    @Transactional
    public void releaseReservedInventory(Long shoeModelId, String size, Integer quantity) {
        log.debug("Releasing reserved inventory: shoeModelId={}, size={}, quantity={}", shoeModelId, size, quantity);

        ShoeModel shoeModel = shoeModelService.getById(shoeModelId);

        Optional<ShoeInventory> inventoryOpt = repository.findByShoeModelAndSize(shoeModel, size);
        if (inventoryOpt.isPresent()) {
            ShoeInventory inventory = inventoryOpt.get();
            int newReserved = Math.max(0, inventory.getQuantityReserved() - quantity);
            inventory.setQuantityReserved(newReserved);
            repository.save(inventory);
            
            log.info("Released {} reserved units for shoe model {} size {}. New reserved total: {}", 
                    quantity, shoeModelId, size, newReserved);
        } else {
            log.warn("No inventory found to release for shoe model {} and size {}", shoeModelId, size);
        }
    }

    /**
     * Commit reserved inventory (convert to sold)
     */
    @Transactional
    public void commitReservedInventory(Long shoeModelId, String size, Integer quantity) {
        log.debug("Committing reserved inventory: shoeModelId={}, size={}, quantity={}", shoeModelId, size, quantity);

        ShoeModel shoeModel = shoeModelService.getById(shoeModelId);

        Optional<ShoeInventory> inventoryOpt = repository.findByShoeModelAndSize(shoeModel, size);
        if (inventoryOpt.isPresent()) {
            ShoeInventory inventory = inventoryOpt.get();
            
            // Reduce both available and reserved quantities
            inventory.setQuantityAvailable(Math.max(0, inventory.getQuantityAvailable() - quantity));
            inventory.setQuantityReserved(Math.max(0, inventory.getQuantityReserved() - quantity));
            
            repository.save(inventory);
            
            log.info("Committed {} units for shoe model {} size {}. Available: {}, Reserved: {}", 
                    quantity, shoeModelId, size, inventory.getQuantityAvailable(), inventory.getQuantityReserved());
        } else {
            log.warn("No inventory found to commit for shoe model {} and size {}", shoeModelId, size);
        }
    }

    /**
     * Check if inventory is available for purchase
     */
    public boolean isInventoryAvailable(Long shoeModelId, String size, Integer quantity) {
        ShoeModel shoeModel = shoeModelService.getById(shoeModelId);
        
        if (shoeModel == null) {
            return false;
        }
        
        Optional<ShoeInventory> inventoryOpt = repository.findByShoeModelAndSize(shoeModel, size);
        if (inventoryOpt.isEmpty()) {
            return false;
        }
        
        ShoeInventory inventory = inventoryOpt.get();
        return inventory.isAvailable() && inventory.getActualAvailableQuantity() >= quantity;
    }

    /**
     * Reserve inventory by inventory ID
     */
    @Transactional
    public boolean reserveInventoryById(Long inventoryId, Integer quantity) {
        log.debug("Reserving inventory by ID: {} quantity: {}", inventoryId, quantity);
        
        ShoeInventory inventory = getById(inventoryId);
        
        if (inventory.getQuantityAvailable() < quantity) {
            log.warn("Insufficient available inventory for ID {}: available={}, requested={}", 
                    inventoryId, inventory.getQuantityAvailable(), quantity);
            return false;
        }
        
        inventory.setQuantityAvailable(inventory.getQuantityAvailable() - quantity);
        inventory.setQuantityReserved(inventory.getQuantityReserved() + quantity);
        
        repository.save(inventory);
        
        log.info("Reserved {} units for inventory ID: {}", quantity, inventoryId);
        return true;
    }

    /**
     * Restore inventory (return sold inventory back to available stock)
     * Used when an order is cancelled after being confirmed/processed
     */
    @Transactional
    public void restoreInventory(Long shoeModelId, String size, Integer quantity) {
        log.debug("Restoring inventory: shoeModelId={}, size={}, quantity={}", shoeModelId, size, quantity);

        ShoeModel shoeModel = shoeModelService.getById(shoeModelId);

        Optional<ShoeInventory> inventoryOpt = repository.findByShoeModelAndSize(shoeModel, size);
        if (inventoryOpt.isPresent()) {
            ShoeInventory inventory = inventoryOpt.get();
            
            // Add the quantity back to available inventory
            inventory.setQuantityAvailable(inventory.getQuantityAvailable() + quantity);
            
            repository.save(inventory);
            
            log.info("Restored {} units for shoe model {} size {}. New available total: {}", 
                    quantity, shoeModelId, size, inventory.getQuantityAvailable());
        } else {
            log.warn("No inventory found to restore for shoe model {} and size {}", shoeModelId, size);
        }
    }
}
