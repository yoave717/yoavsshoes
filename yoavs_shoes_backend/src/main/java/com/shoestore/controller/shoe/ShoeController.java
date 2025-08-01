package com.shoestore.controller.shoe;

import com.fasterxml.jackson.annotation.JsonView;
import com.shoestore.controller.base.CrudController;
import com.shoestore.dto.base.PageResponse;
import com.shoestore.dto.shoe.ShoeDto;
import com.shoestore.dto.shoe.ShoeDto.ShoeInventoryViewDto;
import com.shoestore.dto.shoe.ShoeFilterCriteria;
import com.shoestore.dto.shoe.ShoeMapper;
import com.shoestore.dto.view.Views;
import com.shoestore.entity.shoe.IShoeInventoryView;
import com.shoestore.entity.shoe.Shoe;
import com.shoestore.service.shoe.ShoeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for shoe operations
 */
@RestController
@RequestMapping("/api/shoes")
@Slf4j
@Tag(name = "Shoes", description = "Shoe management operations")
public class ShoeController extends CrudController<
        Shoe, 
        Long, 
        ShoeDto.CreateShoeDto, 
        ShoeDto.UpdateShoeDto, 
        ShoeDto, 
        ShoeMapper, 
        ShoeService
> {

    public ShoeController(ShoeMapper mapper, ShoeService service) {
        super(service, "Shoe", mapper);
    }

    /**
     * Get paginated list of shoes with filters
     */
    @GetMapping("/filtered")
    @Operation(
        summary = "Get shoes with filters",
        description = "Retrieve a paginated list of shoes with optional filters for brand, category, and search"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Shoes retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @JsonView(Views.Detailed.class)
    public ResponseEntity<StandardResponse<PageResponse<ShoeDto>>> getShoesFiltered(
            @Parameter(description = "Page number (0-based)") 
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size") 
            @RequestParam(defaultValue = "20") int size,
            
            @Parameter(description = "Sort field (name, basePrice, createdAt)") 
            @RequestParam(defaultValue = "name") String sortBy,
            
            @Parameter(description = "Sort direction (ASC, DESC)") 
            @RequestParam(defaultValue = "ASC") String sortDirection,
            
            @Parameter(description = "Filter by brand IDs") 
            @RequestParam(required = false) List<Long> brandIds,
            
            @Parameter(description = "Filter by category IDs") 
            @RequestParam(required = false) List<Long> categoryIds,
            
            @Parameter(description = "Search term for shoe name or brand") 
            @RequestParam(required = false) String search,
            
            @Parameter(description = "Minimum base price") 
            @RequestParam(required = false) BigDecimal minPrice,
            
            @Parameter(description = "Maximum base price") 
            @RequestParam(required = false) BigDecimal maxPrice) {

        log.debug("Getting shoes with filters - page: {}, size: {}, sortBy: {}, sortDirection: {}", 
                 page, size, sortBy, sortDirection);

        ShoeFilterCriteria criteria = ShoeFilterCriteria.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .brandIds(brandIds)
                .categoryIds(categoryIds)
                .searchTerm(search)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .build();

        Page<Shoe> shoes = service.getShoes(criteria);
        log.debug("Retrieved {} shoes", shoes.getContent().size());
        PageResponse<ShoeDto> pageResponse = new PageResponse<>(shoes.map(this::convertToDto));

        return success(pageResponse, "Shoes retrieved successfully");
    }

    /**
     * Get shoes with model count and stock information
     */
    @GetMapping("/with-model-count")
    @Operation(
        summary = "Get shoes with model count and stock",
        description = "Retrieve shoes with aggregated model count and total stock information for inventory management"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Shoes with stock info retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @JsonView(Views.Summary.class)
    public ResponseEntity<StandardResponse<PageResponse<ShoeInventoryViewDto>>> getShoesWithModelCount(
            @Parameter(description = "Page number (0-based)") 
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size") 
            @RequestParam(defaultValue = "20") int size,
            
            @Parameter(description = "Sort field (name, basePrice, createdAt)") 
            @RequestParam(defaultValue = "name") String sortBy,
            
            @Parameter(description = "Sort direction (ASC, DESC)") 
            @RequestParam(defaultValue = "ASC") String sortDirection,
            
            @Parameter(description = "Filter by brand IDs") 
            @RequestParam(required = false) List<Long> brandIds,
            
            @Parameter(description = "Filter by category IDs") 
            @RequestParam(required = false) List<Long> categoryIds,
            
            @Parameter(description = "Search term for shoe name or brand") 
            @RequestParam(required = false) String search,
            
            @Parameter(description = "Filter by gender") 
            @RequestParam(required = false) String gender,
            
            @Parameter(description = "Minimum base price") 
            @RequestParam(required = false) BigDecimal minPrice,
            
            @Parameter(description = "Maximum base price") 
            @RequestParam(required = false) BigDecimal maxPrice) {

        log.debug("Getting shoes with model count - page: {}, size: {}, sortBy: {}, sortDirection: {}", 
                 page, size, sortBy, sortDirection);

        ShoeFilterCriteria criteria = ShoeFilterCriteria.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .brandIds(brandIds)
                .categoryIds(categoryIds)
                .searchTerm(search)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .activeOnly(true)
                .build();

        Page<IShoeInventoryView> shoes = service.getShoesWithModelCountAggregated(criteria);
                
        log.debug("Retrieved {} shoes with model count", shoes.getContent().size());
        PageResponse<ShoeInventoryViewDto> pageResponse = new PageResponse<>(shoes.map(view -> mapper.toInventoryViewDto(view.getShoe(), view.getModelCount(), view.getTotalStock())));
        
        return success(pageResponse, "Shoes with model count retrieved successfully");
    }

    /**
     * Get shoe statistics
     */
    @GetMapping("/stats")
    @Operation(
        summary = "Get shoe statistics",
        description = "Retrieve statistics about shoes including total count, model count, and stock information"
    )
    public ResponseEntity<StandardResponse<Map<String, Object>>> getShoeStatistics() {
        log.debug("Getting shoe statistics");

        Map<String, Object> stats = service.getShoeStatistics();

        return success(stats, "Shoe statistics retrieved successfully");
    }

    @Override
    protected String[] getAllowedSortFields() {
        return new String[]{"id", "name", "basePrice", "createdAt", "updatedAt"};
    }

    @Override
    protected Class<Shoe> getEntityClass() {
        return Shoe.class;
    }
}
