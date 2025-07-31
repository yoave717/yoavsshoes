'use client';

import { useState } from 'react';
import { Grid, List, ArrowUpDown } from 'lucide-react';
import ProductCard from './ProductCard';
import { ShoeModel } from '@/lib/types';

interface ProductGridProps {
  products: ShoeModel[];
  isLoading?: boolean;
  onQuickView?: (product: ShoeModel) => void;
  onAddToCart?: (product: ShoeModel, size?: string) => void;
  onToggleWishlist?: (product: ShoeModel) => void;
  wishlistItems?: number[];
  totalElements: number;
  currentPage: number;
  totalPages: number;
  pageSize: number;
  onPageChange: (page: number) => void;
  onSortChange: (sortBy: string, sortDirection: string) => void;
  currentSort: { sortBy: string; sortDirection: string };
}

const SORT_OPTIONS = [
  { value: 'createdAt_DESC', label: 'Newest First', sortBy: 'createdAt', sortDirection: 'DESC' },
  { value: 'createdAt_ASC', label: 'Oldest First', sortBy: 'createdAt', sortDirection: 'ASC' },
  { value: 'price_ASC', label: 'Price: Low to High', sortBy: 'price', sortDirection: 'ASC' },
  { value: 'price_DESC', label: 'Price: High to Low', sortBy: 'price', sortDirection: 'DESC' },
  { value: 'name_ASC', label: 'Name: A to Z', sortBy: 'name', sortDirection: 'ASC' },
  { value: 'name_DESC', label: 'Name: Z to A', sortBy: 'name', sortDirection: 'DESC' },
];

export default function ProductGrid({
  products,
  isLoading = false,
  onQuickView,
  onAddToCart,
  onToggleWishlist,
  wishlistItems = [],
  totalElements,
  currentPage,
  totalPages,
  pageSize,
  onPageChange,
  onSortChange,
  currentSort,
}: ProductGridProps) {
  const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid');
  const [showSortDropdown, setShowSortDropdown] = useState(false);

  const currentSortValue = `${currentSort.sortBy}_${currentSort.sortDirection}`;
  const currentSortOption = SORT_OPTIONS.find(option => option.value === currentSortValue) || SORT_OPTIONS[0];

  const handleSortChange = (option: typeof SORT_OPTIONS[0]) => {
    onSortChange(option.sortBy, option.sortDirection);
    setShowSortDropdown(false);
  };

  const generatePageNumbers = () => {
    const pages = [];
    const maxVisiblePages = 5;
    const halfVisible = Math.floor(maxVisiblePages / 2);
    
    let startPage = Math.max(0, currentPage - halfVisible);
    const endPage = Math.min(totalPages - 1, startPage + maxVisiblePages - 1);
    
    if (endPage - startPage < maxVisiblePages - 1) {
      startPage = Math.max(0, endPage - maxVisiblePages + 1);
    }
    
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    
    return pages;
  };

  if (isLoading) {
    return (
      <div className="space-y-6">
        {/* Loading Header */}
        <div className="flex items-center justify-between">
          <div className="h-6 bg-gray-200 rounded w-48 animate-pulse"></div>
          <div className="h-10 bg-gray-200 rounded w-32 animate-pulse"></div>
        </div>

        {/* Loading Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {Array.from({ length: 12 }).map((_, index) => (
            <div key={index} className="bg-white rounded-lg shadow-md overflow-hidden animate-pulse">
              <div className="aspect-square bg-gray-200"></div>
              <div className="p-4 space-y-3">
                <div className="h-4 bg-gray-200 rounded w-3/4"></div>
                <div className="h-4 bg-gray-200 rounded w-1/2"></div>
                <div className="h-6 bg-gray-200 rounded w-1/4"></div>
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header with Results Count and Controls */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div className="flex items-center space-x-4">
          <p className="text-sm text-gray-700">
            Showing {currentPage * pageSize + 1}-{Math.min((currentPage + 1) * pageSize, totalElements)} of {totalElements} results
          </p>
        </div>

        <div className="flex items-center space-x-4">
          {/* View Mode Toggle */}
          <div className="flex items-center border border-gray-300 rounded-md">
            <button
              onClick={() => setViewMode('grid')}
              className={`p-2 ${viewMode === 'grid' ? 'bg-gray-100 text-gray-900' : 'text-gray-500'}`}
              title="Grid view"
            >
              <Grid className="h-4 w-4" />
            </button>
            <button
              onClick={() => setViewMode('list')}
              className={`p-2 ${viewMode === 'list' ? 'bg-gray-100 text-gray-900' : 'text-gray-500'}`}
              title="List view"
            >
              <List className="h-4 w-4" />
            </button>
          </div>

          {/* Sort Dropdown */}
          <div className="relative">
            <button
              onClick={() => setShowSortDropdown(!showSortDropdown)}
              className="flex items-center space-x-2 px-4 py-2 bg-white border border-gray-300 rounded-md shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <ArrowUpDown className="h-4 w-4" />
              <span className="text-sm">{currentSortOption.label}</span>
            </button>

            {showSortDropdown && (
              <div className="absolute right-0 mt-2 w-56 bg-white border border-gray-300 rounded-md shadow-lg z-10">
                <div className="py-1">
                  {SORT_OPTIONS.map((option) => (
                    <button
                      key={option.value}
                      onClick={() => handleSortChange(option)}
                      className={`w-full text-left px-4 py-2 text-sm hover:bg-gray-50 ${
                        option.value === currentSortValue ? 'bg-blue-50 text-blue-700' : 'text-gray-700'
                      }`}
                    >
                      {option.label}
                    </button>
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Products Grid/List */}
      {products.length > 0 ? (
        <div className={viewMode === 'grid' 
          ? 'grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6'
          : 'space-y-4'
        }>
          {products.map((product) => (
            <ProductCard
              key={product.id}
              product={product}
              onQuickView={onQuickView}
              onAddToCart={onAddToCart}
              onToggleWishlist={onToggleWishlist}
              isInWishlist={wishlistItems.includes(product.id)}
            />
          ))}
        </div>
      ) : (
        <div className="text-center py-12">
          <div className="text-gray-400 mb-4">
            <Grid className="h-24 w-24 mx-auto" />
          </div>
          <h3 className="text-lg font-medium text-gray-900 mb-2">No products found</h3>
          <p className="text-gray-500">Try adjusting your search or filter criteria</p>
        </div>
      )}

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex items-center justify-center space-x-2 mt-8">
          {/* Previous Button */}
          <button
            onClick={() => onPageChange(currentPage - 1)}
            disabled={currentPage === 0}
            className={`px-3 py-2 text-sm font-medium rounded-md ${
              currentPage === 0
                ? 'text-gray-300 cursor-not-allowed'
                : 'text-gray-700 hover:bg-gray-50 border border-gray-300'
            }`}
          >
            Previous
          </button>

          {/* Page Numbers */}
          {generatePageNumbers().map((pageNumber) => (
            <button
              key={pageNumber}
              onClick={() => onPageChange(pageNumber)}
              className={`px-3 py-2 text-sm font-medium rounded-md ${
                pageNumber === currentPage
                  ? 'bg-blue-600 text-white'
                  : 'text-gray-700 hover:bg-gray-50 border border-gray-300'
              }`}
            >
              {pageNumber + 1}
            </button>
          ))}

          {/* Next Button */}
          <button
            onClick={() => onPageChange(currentPage + 1)}
            disabled={currentPage >= totalPages - 1}
            className={`px-3 py-2 text-sm font-medium rounded-md ${
              currentPage >= totalPages - 1
                ? 'text-gray-300 cursor-not-allowed'
                : 'text-gray-700 hover:bg-gray-50 border border-gray-300'
            }`}
          >
            Next
          </button>
        </div>
      )}

      {/* Click outside to close sort dropdown */}
      {showSortDropdown && (
        <div
          className="fixed inset-0 z-5"
          onClick={() => setShowSortDropdown(false)}
        />
      )}
    </div>
  );
}
