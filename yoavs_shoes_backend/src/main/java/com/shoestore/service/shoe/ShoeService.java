package com.shoestore.service.shoe;

import com.shoestore.dto.shoe.ShoeFilterCriteria;
import com.shoestore.entity.shoe.*;
import com.shoestore.repository.shoe.*;
import com.shoestore.service.base.BaseService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.*;
import java.util.*;

/**
 * Service for shoe operations
 */
@Service
@Slf4j
@Transactional
public class ShoeService extends BaseService<Shoe, Long, ShoeRepository> {

    private final ShoeRepository shoeRepository;
    private final BrandRepository brandRepository;
    private final ShoeCategoryRepository categoryRepository;
    private final ShoeModelService shoeModelService;
    private final ShoeInventoryService shoeInventoryService;

    public ShoeService(
            ShoeRepository shoeRepository,
            BrandRepository brandRepository,
            ShoeCategoryRepository categoryRepository,
            ShoeModelService shoeModelService,
            ShoeInventoryService shoeInventoryService
    ) {
        super(shoeRepository, "Shoe");
        this.shoeRepository = shoeRepository;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.shoeInventoryService = shoeInventoryService;
        this.shoeModelService = shoeModelService;
    }

    @Override
    public Page<Shoe> findAll(Pageable pageable) {
        log.debug("Finding all Shoes with pagination and joins: {}", pageable);

        // Create specification with proper joins
        Specification<Shoe> spec = createSpecificationWithJoins(null);
        
        return shoeRepository.findAll(spec, pageable);
    }

    /**
     * Get paginated list of shoes with filters
     */
    public Page<Shoe> getShoes(ShoeFilterCriteria criteria) {
        log.debug("Getting shoes with criteria: {}", criteria);

        // Create specification for filtering with joins
        Specification<Shoe> spec = createSpecificationWithJoins(criteria);

        // Create pageable for pagination and sorting
        Pageable pageable = createPageable(criteria);

        // Execute query
        return shoeRepository.findAll(spec, pageable);
    }

       /**
     * Get shoes with model count using aggregation query
     */
    public Page<IShoeInventoryView> getShoesWithModelCountAggregated(ShoeFilterCriteria criteria) {
        log.debug("Getting shoes with aggregated model count for criteria: {}", criteria);
        
        // Create pageable for pagination and sorting
        Pageable pageable = createPageable(criteria);
        
        // Use repository method for aggregated data
        return shoeRepository.findShoesWithStockInfo(
            criteria.getBrandIds(),
            criteria.getCategoryIds(),
            criteria.getSearchTerm(),
            criteria.getGender(),
            criteria.getMinPrice(),
            criteria.getMaxPrice(),
            pageable
        );
    }

    /**
     * Get shoe stats
     * Total shoes, total models, total stock, low stock shoes
     * This method aggregates data across all shoes
     */
    public Map<String, Object> getShoeStatistics() {
        log.debug("Calculating shoe stats");

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalShoes", shoeRepository.count());
        stats.put("totalModels", shoeModelService.count());
        stats.put("totalStock", shoeInventoryService.getTotalAvailableStock());
        stats.put("lowStockShoes", shoeRepository.countShoeModelsWithLowStock(10)); 

        log.debug("Shoe stats calculated: {}", stats);
        return stats;
    }


    @Override
    @Transactional
    public Shoe create(Shoe shoe) {
        log.debug("Creating new shoe: {}", shoe.getName());
        
        

        shoe.setIsActive(true);
        return super.create(shoe);
    }

    @Override
    protected void updateEntityFields(Shoe existingShoe, Shoe newShoe) {
        log.debug("Updating shoe fields for: {}", existingShoe.getId());
        
        if (newShoe.getName() != null) {
            existingShoe.setName(newShoe.getName());
        }
        
        if (newShoe.getBasePrice() != null) {
            existingShoe.setBasePrice(newShoe.getBasePrice());
        }
        
        if (newShoe.getGender() != null) {
            existingShoe.setGender(newShoe.getGender());
        }
        
        if (newShoe.getBrand() != null && newShoe.getBrand().getId() != null) {
            Brand brand = brandRepository.findById(newShoe.getBrand().getId())
                .orElseThrow(() -> new IllegalArgumentException("Brand not found"));
            if (!brand.getIsActive()) {
                throw new IllegalArgumentException("Cannot update shoe with inactive brand");
            }
            existingShoe.setBrand(brand);
        }
        
        if (newShoe.getCategory() != null && newShoe.getCategory().getId() != null) {
            ShoeCategory category = categoryRepository.findById(newShoe.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            if (!category.getIsActive()) {
                throw new IllegalArgumentException("Cannot update shoe with inactive category");
            }
            existingShoe.setCategory(category);
        }
        
        if (newShoe.getIsActive() != null) {
            existingShoe.setIsActive(newShoe.getIsActive());
        }
    }

    /** 
     * Create Pageable for pagination and sorting
     */
    private Pageable createPageable(ShoeFilterCriteria criteria) {
        Sort.Direction direction = "DESC".equalsIgnoreCase(criteria.getSortDirection()) 
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        Sort sort = switch (criteria.getSortBy().toLowerCase()) {
            case "baseprice" -> Sort.by(direction, "basePrice");
            case "brand" -> Sort.by(direction, "brand.name");
            case "category" -> Sort.by(direction, "category.name");
            case "createdat" -> Sort.by(direction, "createdAt");
            default -> Sort.by(direction, "name");
        };

        return PageRequest.of(
                criteria.getPage() != null ? criteria.getPage() : 0,
                criteria.getSize() != null ? criteria.getSize() : 20,
                sort
        );
    }

    /**
     * Create JPA Specification with proper joins for filtering
     */
    private Specification<Shoe> createSpecificationWithJoins(ShoeFilterCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            // Add fetch joins to avoid N+1 queries including models

            List<Predicate> predicates = new ArrayList<>();

            // Always filter active entities
            predicates.add(criteriaBuilder.isTrue(root.get("isActive")));

            // If no criteria provided, return early
            if (criteria == null) {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }

            // Active only filter for related entities
            if (Boolean.TRUE.equals(criteria.getActiveOnly())) {
                predicates.add(criteriaBuilder.isTrue(root.get("brand").get("isActive")));
                predicates.add(criteriaBuilder.isTrue(root.get("category").get("isActive")));
            }

            // Brand IDs filter
            if (criteria.getBrandIds() != null && !criteria.getBrandIds().isEmpty()) {
                predicates.add(root.get("brand").get("id").in(criteria.getBrandIds()));
            }

            // Category IDs filter
            if (criteria.getCategoryIds() != null && !criteria.getCategoryIds().isEmpty()) {
                predicates.add(root.get("category").get("id").in(criteria.getCategoryIds()));
            }

            // Price range filters
            if (criteria.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("basePrice"), criteria.getMinPrice()));
            }
            if (criteria.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("basePrice"), criteria.getMaxPrice()));
            }

            // Search term filter
            if (criteria.getSearchTerm() != null && !criteria.getSearchTerm().trim().isEmpty()) {
                String searchPattern = "%" + criteria.getSearchTerm().toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("brand").get("name")), searchPattern)
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
   
}
