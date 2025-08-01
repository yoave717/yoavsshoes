package com.shoestore.service.shoe;

import com.shoestore.dto.shoe.*;
import com.shoestore.entity.shoe.*;
import com.shoestore.exception.ResourceNotFoundException;
import com.shoestore.repository.shoe.*;
import com.shoestore.service.base.BaseService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.*;
import java.util.*;

/**
 * Service for product listing operations
 */
@Service
@Slf4j
@Transactional
public class ShoeModelService extends BaseService<ShoeModel, Long, ShoeModelRepository> {

    private final ShoeModelRepository shoeModelRepository;
    private final BrandRepository brandRepository;
    private final ShoeCategoryRepository categoryRepository;


    public ShoeModelService(
            ShoeModelRepository shoeModelRepository,
            BrandRepository brandRepository,
            ShoeCategoryRepository categoryRepository
    ) {
        super(shoeModelRepository, "ShoeModel");
        this.shoeModelRepository = shoeModelRepository;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Page<ShoeModel> findAll(Pageable pageable) {
        log.debug("Finding all ShoeModels with pagination and joins: {}", pageable);

        // Create specification with proper joins
        Specification<ShoeModel> spec = createSpecificationWithJoins(null);
        
        return shoeModelRepository.findAll(spec, pageable);
    }

    /**
     * Get paginated list of products with filters
     */
    public Page<ShoeModel> getProducts(ProductFilterCriteria criteria) {
        log.debug("Getting products with criteria: {}", criteria);

        // Create specification for filtering with joins
        Specification<ShoeModel> spec = createSpecificationWithJoins(criteria);

        // Create pageable for pagination and sorting
        Pageable pageable = createPageable(criteria);

        // Execute query
        return shoeModelRepository.findAll(spec, pageable);
    }

    /**
     * Get models by shoe ID
     */
    public List<ShoeModel> getShoeModels(Long shoeId) {
        log.debug("Getting shoe models for shoe ID: {}", shoeId);
        List<ShoeModel> models = shoeModelRepository.findByShoeId(shoeId);
        if (models.isEmpty()) {
            throw new ResourceNotFoundException("No models found for shoe ID: " + shoeId);
        }

        return models;
    }

    /**
     * Create Pageable for pagination and sorting
     */
    private Pageable createPageable(ProductFilterCriteria criteria) {
        Sort.Direction direction = "DESC".equalsIgnoreCase(criteria.getSortDirection()) 
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        Sort sort = switch (criteria.getSortBy().toLowerCase()) {
            case "price" -> Sort.by(direction, "price");
            case "brand" -> Sort.by(direction, "shoe.brand.name");
            case "createdat" -> Sort.by(direction, "createdAt");
            default -> Sort.by(direction, "shoe.name", "modelName");
        };

        return PageRequest.of(
                criteria.getPage() != null ? criteria.getPage() : 0,
                criteria.getSize() != null ? criteria.getSize() : 20,
                sort
        );
    }

    /**
     * Get available filter options
     */
    @Cacheable("availableFilters")
    public AvailableFiltersResponse getAvailableFilters() {
        log.debug("Getting available filter options");

        // Get all active brands with product counts
        List<AvailableFiltersResponse.BrandInfo> brands = brandRepository.findByIsActiveTrue()
                .stream()
                .map(brand -> AvailableFiltersResponse.BrandInfo.builder()
                        .id(brand.getId())
                        .name(brand.getName())
                        .productCount(shoeModelRepository.countByShoe_BrandAndIsActiveTrue(brand))
                        .build())
                .filter(brandInfo -> brandInfo.getProductCount() > 0)
                .toList();

        // Get all active categories with product counts
        List<AvailableFiltersResponse.CategoryInfo> categories = categoryRepository.findByIsActiveTrue()
                .stream()
                .map(category -> AvailableFiltersResponse.CategoryInfo.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .productCount(shoeModelRepository.countByShoe_CategoryAndIsActiveTrue(category))
                        .build())
                .filter(categoryInfo -> categoryInfo.getProductCount() > 0)
                .toList();

        // Get all available colors
        List<String> colors = shoeModelRepository.findDistinctColorsByIsActiveTrue();

        return AvailableFiltersResponse.builder()
                .brands(brands)
                .categories(categories)
                .colors(colors)
                .build();
    }

    @Override
    protected void updateEntityFields(ShoeModel existingEntity, ShoeModel newEntity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateEntityFields'");
    }

    /**
     * Create JPA Specification with proper joins for filtering
     */
    private Specification<ShoeModel> createSpecificationWithJoins(ProductFilterCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            // Add fetch joins to avoid N+1 queries
            if (query.getResultType().equals(ShoeModel.class)) {
                Fetch<ShoeModel, Shoe> shoeFetch = root.fetch("shoe", JoinType.LEFT);
                shoeFetch.fetch("brand", JoinType.LEFT);
                shoeFetch.fetch("category", JoinType.LEFT);
                root.fetch("availableSizes", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            // Always filter active entities
            predicates.add(criteriaBuilder.isTrue(root.get("isActive")));

            // If no criteria provided, return early
            if (criteria == null) {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }

            // Active only filter for related entities
            if (Boolean.TRUE.equals(criteria.getActiveOnly())) {
                predicates.add(criteriaBuilder.isTrue(root.get("shoe").get("isActive")));
                predicates.add(criteriaBuilder.isTrue(root.get("shoe").get("brand").get("isActive")));
                predicates.add(criteriaBuilder.isTrue(root.get("shoe").get("category").get("isActive")));
            }

            // Simple IN filters
            addInFilter(predicates , root.get("shoe").get("brand").get("id"), criteria.getBrandIds());
            addInFilter(predicates, root.get("shoe").get("category").get("id"), criteria.getCategoryIds());

            // Color filter with OR logic
            if (criteria.getColors() != null && !criteria.getColors().isEmpty()) {
                Predicate[] colorPredicates = criteria.getColors().stream()
                        .map(color -> criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("color")),
                                "%" + color.toLowerCase() + "%"))
                        .toArray(Predicate[]::new);
                predicates.add(criteriaBuilder.or(colorPredicates));
            }

            // Gender filter
            if (criteria.getGender() != null && !criteria.getGender().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("shoe").get("gender"), 
                        Shoe.Gender.valueOf(criteria.getGender().toUpperCase())));
            }

            // Price range filters
            if (criteria.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), criteria.getMinPrice()));
            }
            if (criteria.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), criteria.getMaxPrice()));
            }

            // Search term filter
            if (criteria.getSearchTerm() != null && !criteria.getSearchTerm().trim().isEmpty()) {
                String searchPattern = "%" + criteria.getSearchTerm().toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("shoe").get("name")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("modelName")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("shoe").get("brand").get("name")), searchPattern)
                ));
            }

            // Size filter with subquery
            if (criteria.getSizes() != null && !criteria.getSizes().isEmpty() && query != null) {
                Subquery<Long> sizeSubquery = query.subquery(Long.class);
                Root<ShoeInventory> inventoryRoot = sizeSubquery.from(ShoeInventory.class);
                sizeSubquery.select(inventoryRoot.get("shoeModel").get("id"))
                        .where(
                                criteriaBuilder.equal(inventoryRoot.get("shoeModel"), root),
                                inventoryRoot.get("size").in(criteria.getSizes()),
                                criteriaBuilder.greaterThan(inventoryRoot.get("quantityAvailable"), 0)
                        );
                predicates.add(criteriaBuilder.exists(sizeSubquery));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Helper method to add IN filter predicates
     */
    private void addInFilter(List<Predicate> predicates, 
                           Path<?> path, List<?> values) {
        if (values != null && !values.isEmpty()) {
            predicates.add(path.in(values));
        }
    }
}
