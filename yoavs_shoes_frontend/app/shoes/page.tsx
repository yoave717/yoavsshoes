'use client';

import { useState, useMemo } from 'react';
import { useProducts, useAvailableFilters, useCartActions } from '@hooks';
import ProductGrid from '../../components/ProductGrid';
import ProductFilters, { FilterState } from '../../components/ProductFilters';
import { ShoeModel, SortDirection } from '@/lib/types';

export default function ProductListingPage() {
  const [filters, setFilters] = useState<FilterState>({
    brandIds: [],
    categoryIds: [],
    colors: [],
    sizes: [],
    minPrice: undefined,
    maxPrice: undefined,
    inStock: false,
    search: '',
  });

  const [page, setPage] = useState(0);
  const [sortBy, setSortBy] = useState('createdAt');
  const [sortDirection, setSortDirection] = useState<SortDirection>('desc');
  const [wishlistItems, setWishlistItems] = useState<number[]>([]);

  // Get cart actions
  const { addProductToCart } = useCartActions();

  // Convert filters to API format
  const apiFilters = useMemo(() => ({
    page,
    size: 20,
    sortBy,
    sortDirection,
    ...(filters.brandIds.length > 0 && { brandIds: filters.brandIds }),
    ...(filters.categoryIds.length > 0 && { categoryIds: filters.categoryIds }),
    ...(filters.colors.length > 0 && { colors: filters.colors }),
    ...(filters.sizes.length > 0 && { sizes: filters.sizes }),
    ...(filters.minPrice && { minPrice: filters.minPrice }),
    ...(filters.maxPrice && { maxPrice: filters.maxPrice }),
    ...(filters.search && { search: filters.search }),
    ...(filters.inStock && { inStock: filters.inStock }),
  }), [filters, page, sortBy, sortDirection]);

  const { data: productsData, isLoading: productsLoading, error: productsError } = useProducts(apiFilters);
  const { data: availableFilters, isLoading: filtersLoading } = useAvailableFilters();

  // Convert products for grid display
  const convertedProducts = productsData;

  const handleFiltersChange = (newFilters: FilterState) => {
    setFilters(newFilters);
    setPage(0); // Reset to first page when filters change
  };

  const handlePageChange = (newPage: number) => {
    setPage(newPage);
    // Scroll to top of products grid
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleSortChange = (newSortBy: string, newSortDirection: SortDirection) => {
    setSortBy(newSortBy);
    setSortDirection(newSortDirection);
    setPage(0); // Reset to first page when sorting changes
  };

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const handleQuickView = (_product: ShoeModel) => {
    // TODO: Implement quick view modal
  };

  const handleAddToCart = (product: ShoeModel, size?: string) => {
    try {
      addProductToCart(product, size);
      
      // Show success notification
      alert(`Added ${product.modelName} to cart${size ? ` (Size: ${size})` : ''}!`);
      
    } catch {
      alert('Failed to add product to cart. Please try again.');
    }
  };

  const handleToggleWishlist = (product: ShoeModel) => {
    setWishlistItems(prev => 
      prev.includes(product.id)
        ? prev.filter(id => id !== product.id)
        : [...prev, product.id]
    );
  };

  if (productsError) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="text-red-600 text-lg font-medium mb-2">
            Error loading products
          </div>
          <div className="text-gray-600">
            {productsError.message || 'Something went wrong. Please try again.'}
          </div>
          <button
            onClick={() => window.location.reload()}
            className="mt-4 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
          >
            Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="text-center">
            <h1 className="text-4xl font-bold text-gray-900 sm:text-5xl">
              Our Shoe Collection
            </h1>
            <p className="mt-3 max-w-2xl mx-auto text-xl text-gray-500 sm:mt-4">
              Discover the perfect pair for every step of your journey
            </p>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="lg:grid lg:grid-cols-4 lg:gap-8">
          {/* Filters Sidebar */}
          <div className="lg:col-span-1">
            {availableFilters && !filtersLoading ? (
              <ProductFilters
                filters={{
                  brands: availableFilters.data.brands,
                  categories: availableFilters.data.categories,
                  colors: availableFilters.data.colors,
                  priceRange: { min: 0, max: 1000 }, // You might want to get this from API
                }}
                currentFilters={filters}
                onFiltersChange={handleFiltersChange}
              />
            ) : (
              <div className="hidden lg:block bg-white p-6 rounded-lg shadow-sm border border-gray-200">
                <div className="animate-pulse space-y-4">
                  <div className="h-6 bg-gray-200 rounded w-24"></div>
                  <div className="space-y-3">
                    {Array.from({ length: 5 }).map((_, i) => (
                      <div key={i} className="h-4 bg-gray-200 rounded"></div>
                    ))}
                  </div>
                </div>
              </div>
            )}
          </div>

          {/* Products Grid */}
          <div className="lg:col-span-3 mt-6 lg:mt-0">
            <ProductGrid
              products={convertedProducts?.data?.content || []}
              isLoading={productsLoading}
              onQuickView={handleQuickView}
              onAddToCart={handleAddToCart}
              onToggleWishlist={handleToggleWishlist}
              wishlistItems={wishlistItems}
              totalElements={productsData?.data.totalElements || 0}
              currentPage={productsData?.data.page || 0}
              totalPages={productsData?.data.totalPages || 0}
              pageSize={productsData?.data.size || 20}
              onPageChange={handlePageChange}
              onSortChange={handleSortChange}
              currentSort={{ sortBy, sortDirection }}
            />
          </div>
        </div>
      </div>
    </div>
  );
}
