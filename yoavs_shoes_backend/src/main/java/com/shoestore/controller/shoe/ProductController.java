package com.shoestore.controller.shoe;

import com.fasterxml.jackson.annotation.JsonView;
import com.shoestore.controller.base.CrudController;
import com.shoestore.dto.base.PageResponse;
import com.shoestore.dto.shoe.AvailableFiltersResponse;
import com.shoestore.dto.shoe.ProductFilterCriteria;
import com.shoestore.dto.shoe.ShoeModelDto;
import com.shoestore.dto.shoe.ShoeModelMapper;
import com.shoestore.dto.view.Views;
import com.shoestore.entity.shoe.ShoeModel;
import com.shoestore.service.shoe.ShoeModelService;
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

/**
 * REST Controller for product listing operations
 */
@RestController
@RequestMapping("/api/products")
@Slf4j
@Tag(name = "Products", description = "Product listing and filtering operations")
public class ProductController extends CrudController<
        ShoeModel, 
        Long, 
        ShoeModelDto.CreateShoeModelDto, 
        ShoeModelDto.UpdateShoeModelDto, 
        ShoeModelDto, 
        ShoeModelMapper, 
        ShoeModelService
> {

    public ProductController(ShoeModelMapper mapper, ShoeModelService service) {
        super(service, "Product", mapper);
    }


    /**
     * Get paginated list of products with filters
     */
    @GetMapping("/filtered")
    @Operation(
        summary = "Get products with filters",
        description = "Retrieve a paginated list of products with optional filters for brand, category, color, size, and price range"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @JsonView(Views.Detailed.class)
    public ResponseEntity<StandardResponse<PageResponse<ShoeModelDto>>> getAll(
            @Parameter(description = "Page number (0-based)") 
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size") 
            @RequestParam(defaultValue = "20") int size,
            
            @Parameter(description = "Sort field (name, price, createdAt)") 
            @RequestParam(defaultValue = "createdAt") String sortBy,
            
            @Parameter(description = "Sort direction (ASC, DESC)") 
            @RequestParam(defaultValue = "DESC") String sortDirection,
            
            @Parameter(description = "Filter by brand IDs") 
            @RequestParam(required = false) List<Long> brandIds,
            
            @Parameter(description = "Filter by category IDs") 
            @RequestParam(required = false) List<Long> categoryIds,
            
            @Parameter(description = "Filter by colors") 
            @RequestParam(required = false) List<String> colors,
            
            @Parameter(description = "Filter by sizes") 
            @RequestParam(required = false) List<String> sizes,
            
            @Parameter(description = "Minimum price") 
            @RequestParam(required = false) BigDecimal minPrice,
            
            @Parameter(description = "Maximum price") 
            @RequestParam(required = false) BigDecimal maxPrice,
            
            @Parameter(description = "Search term for product name or model") 
            @RequestParam(required = false) String search,
            
            @Parameter(description = "Filter by availability (true/false)") 
            @RequestParam(required = false) Boolean inStock) {

        log.debug("Getting products with filters - page: {}, size: {}, sortBy: {}, sortDirection: {}", 
                 page, size, sortBy, sortDirection);

        ProductFilterCriteria criteria = ProductFilterCriteria.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .brandIds(brandIds)
                .categoryIds(categoryIds)
                .colors(colors)
                .sizes(sizes)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .searchTerm(search)
                .inStock(inStock)
                .build();
        Page<ShoeModel> products = service.getProducts(criteria);
        log.debug("Retrieved {} products", products.getContent().size());
        PageResponse<ShoeModelDto> pageResponse = new PageResponse<>(products.map(this::convertToDto));

        return success(pageResponse, "Products retrieved successfully");
    }

    /**
     * Get available filters
     */
    @GetMapping("/filters")
    @Operation(
        summary = "Get available filters",
        description = "Retrieve available filter options like brands, categories, colors, and price ranges"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Filters retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<StandardResponse<AvailableFiltersResponse>> getAvailableFilters() {
        log.debug("Getting available filters");
        
        AvailableFiltersResponse filters = service.getAvailableFilters();
        
        log.debug("Retrieved filters with {} brands, {} categories, {} colors", 
                 filters.getBrands().size(), 
                 filters.getCategories().size(), 
                 filters.getColors().size());

        return success(filters, "Available filters retrieved successfully");
    }

    /**
     * Search products by text
     */
    @GetMapping("/search")
    @Operation(
        summary = "Search products",
        description = "Search products by name, model, or brand with pagination"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Search completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid search parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<StandardResponse<PageResponse<ShoeModelDto>>> searchProducts(
            @Parameter(description = "Search query", required = true) 
            @RequestParam String q,
            
            @Parameter(description = "Page number (0-based)") 
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size") 
            @RequestParam(defaultValue = "20") int size,
            
            @Parameter(description = "Sort field") 
            @RequestParam(defaultValue = "createdAt") String sortBy,
            
            @Parameter(description = "Sort direction") 
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        log.debug("Searching products with query: '{}', page: {}, size: {}", q, page, size);

        ProductFilterCriteria criteria = ProductFilterCriteria.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .searchTerm(q)
                .build();

        Page<ShoeModel> products = service.getProducts(criteria);
        PageResponse<ShoeModelDto> response = new PageResponse<>(products.map(this::convertToDto));
        log.debug("Found {} products for search query: '{}'",
                 response.getContent().size(), q);

        return success(response, "Search completed successfully");
    }

    /*
     * Get models for a specific shoe
     */
    @GetMapping("/shoe/{shoeId}")
    @Operation(
        summary = "Get shoe models",
        description = "Retrieve all models for a specific shoe by its ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Shoe models retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Shoe not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @JsonView(Views.Detailed.class)
    public ResponseEntity<StandardResponse<List<ShoeModelDto>>> getShoeModels(@PathVariable Long shoeId) {
        log.debug("Getting models for shoe ID: {}", shoeId);
        List<ShoeModel> models = service.getShoeModels(shoeId);
        List<ShoeModelDto> modelDtos = models.stream()
                .map(this::convertToDto).toList();
        return success(modelDtos, "Shoe models retrieved successfully");
    }

    @Override
    protected String[] getAllowedSortFields() {
        return new String[]{"createdAt", "name", "price"};
    }

    @Override
    protected Class<ShoeModel> getEntityClass() {
        return ShoeModel.class;
    }
}
