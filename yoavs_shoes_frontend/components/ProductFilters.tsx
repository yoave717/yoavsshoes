'use client';

import { useState } from 'react';
import { ChevronDown, X, Filter } from 'lucide-react';
import { AvailableFilters } from '@/lib/types';



export interface FilterState {
  brandIds: number[];
  categoryIds: number[];
  colors: string[];
  sizes: string[];
  minPrice?: number;
  maxPrice?: number;
  inStock: boolean;
  search: string;
}

interface ProductFiltersProps {
  filters: AvailableFilters;
  currentFilters: FilterState;
  onFiltersChange: (filters: FilterState) => void;
}

const AVAILABLE_SIZES = ['35', '36', '37', '38', '39', '40', '41', '42', '43', '44', '45', '46', '47'];

export default function ProductFilters({
  filters,
  currentFilters,
  onFiltersChange,
}: ProductFiltersProps) {
  const [showMobileFilters, setShowMobileFilters] = useState(false);
  const [expandedSections, setExpandedSections] = useState({
    brands: true,
    categories: true,
    colors: true,
    sizes: false,
    price: true,
  });

  const toggleSection = (section: keyof typeof expandedSections) => {
    setExpandedSections(prev => ({ ...prev, [section]: !prev[section] }));
  };

  const updateFilters = (updates: Partial<FilterState>) => {
    onFiltersChange({ ...currentFilters, ...updates });
  };

  const clearAllFilters = () => {
    onFiltersChange({
      brandIds: [],
      categoryIds: [],
      colors: [],
      sizes: [],
      minPrice: undefined,
      maxPrice: undefined,
      inStock: false,
      search: '',
    });
  };

  const toggleArrayFilter = <T,>(array: T[], item: T): T[] => {
    return array.includes(item)
      ? array.filter(i => i !== item)
      : [...array, item];
  };

  const getActiveFiltersCount = () => {
    let count = 0;
    if (currentFilters.brandIds.length > 0) count++;
    if (currentFilters.categoryIds.length > 0) count++;
    if (currentFilters.colors.length > 0) count++;
    if (currentFilters.sizes.length > 0) count++;
    if (currentFilters.minPrice || currentFilters.maxPrice) count++;
    if (currentFilters.inStock) count++;
    if (currentFilters.search) count++;
    return count;
  };

  const FilterSection = ({ title, children, expanded, onToggle }: {
    title: string;
    children: React.ReactNode;
    expanded: boolean;
    onToggle: () => void;
  }) => (
    <div className="border-b border-gray-200 pb-4 mb-4">
      <button
        className="flex w-full items-center justify-between text-left"
        onClick={onToggle}
      >
        <span className="font-medium text-gray-900">{title}</span>
        <ChevronDown className={`h-4 w-4 transition-transform ${expanded ? 'rotate-180' : ''}`} />
      </button>
      {expanded && <div className="mt-3">{children}</div>}
    </div>
  );

  const filtersContent = (
    <div className="space-y-6">
      {/* Search */}
      <div>
        <input
          type="text"
          placeholder="Search products..."
          value={currentFilters.search}
          onChange={(e) => updateFilters({ search: e.target.value })}
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>

      {/* Brands */}
      <FilterSection
        title="Brands"
        expanded={expandedSections.brands}
        onToggle={() => toggleSection('brands')}
      >
        <div className="space-y-2 max-h-40 overflow-y-auto">
          {filters.brands.map(brand => (
            <label key={brand.id} className="flex items-center cursor-pointer">
              <input
                type="checkbox"
                checked={currentFilters.brandIds.includes(brand.id)}
                onChange={() => updateFilters({
                  brandIds: toggleArrayFilter(currentFilters.brandIds, brand.id)
                })}
                className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
              />
              <span className="ml-2 text-sm text-gray-700">
                {brand.name} ({brand.productCount})
              </span>
            </label>
          ))}
        </div>
      </FilterSection>

      {/* Categories */}
      <FilterSection
        title="Categories"
        expanded={expandedSections.categories}
        onToggle={() => toggleSection('categories')}
      >
        <div className="space-y-2 max-h-40 overflow-y-auto">
          {filters.categories.map(category => (
            <label key={category.id} className="flex items-center cursor-pointer">
              <input
                type="checkbox"
                checked={currentFilters.categoryIds.includes(category.id)}
                onChange={() => updateFilters({
                  categoryIds: toggleArrayFilter(currentFilters.categoryIds, category.id)
                })}
                className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
              />
              <span className="ml-2 text-sm text-gray-700">
                {category.name} ({category.productCount})
              </span>
            </label>
          ))}
        </div>
      </FilterSection>

      {/* Colors */}
      <FilterSection
        title="Colors"
        expanded={expandedSections.colors}
        onToggle={() => toggleSection('colors')}
      >
        <div className="grid grid-cols-2 gap-2">
          {filters.colors.map(color => (
            <label key={color} className="flex items-center cursor-pointer">
              <input
                type="checkbox"
                checked={currentFilters.colors.includes(color)}
                onChange={() => updateFilters({
                  colors: toggleArrayFilter(currentFilters.colors, color)
                })}
                className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
              />
              <span className="ml-2 text-sm text-gray-700 capitalize">{color}</span>
            </label>
          ))}
        </div>
      </FilterSection>

      {/* Sizes */}
      <FilterSection
        title="Sizes"
        expanded={expandedSections.sizes}
        onToggle={() => toggleSection('sizes')}
      >
        <div className="grid grid-cols-4 gap-2">
          {AVAILABLE_SIZES.map(size => (
            <button
              key={size}
              onClick={() => updateFilters({
                sizes: toggleArrayFilter(currentFilters.sizes, size)
              })}
              className={`px-3 py-1 text-sm border rounded transition-colors ${
                currentFilters.sizes.includes(size)
                  ? 'border-blue-500 bg-blue-50 text-blue-700'
                  : 'border-gray-300 text-gray-700 hover:border-gray-400'
              }`}
            >
              {size}
            </button>
          ))}
        </div>
      </FilterSection>

      {/* Price Range */}
      <FilterSection
        title="Price Range"
        expanded={expandedSections.price}
        onToggle={() => toggleSection('price')}
      >
        <div className="space-y-3">
          <div className="flex items-center space-x-2">
            <input
              type="number"
              placeholder="Min"
              value={currentFilters.minPrice || ''}
              onChange={(e) => updateFilters({
                minPrice: e.target.value ? Number(e.target.value) : undefined
              })}
              className="w-20 px-2 py-1 text-sm border border-gray-300 rounded focus:outline-none focus:ring-1 focus:ring-blue-500"
            />
            <span className="text-gray-500">to</span>
            <input
              type="number"
              placeholder="Max"
              value={currentFilters.maxPrice || ''}
              onChange={(e) => updateFilters({
                maxPrice: e.target.value ? Number(e.target.value) : undefined
              })}
              className="w-20 px-2 py-1 text-sm border border-gray-300 rounded focus:outline-none focus:ring-1 focus:ring-blue-500"
            />
          </div>
          {filters.priceRange && (
            <div className="text-xs text-gray-500">
              Range: ${filters.priceRange.min} - ${filters.priceRange.max}
            </div>
          )}
        </div>
      </FilterSection>

      {/* In Stock */}
      <div>
        <label className="flex items-center cursor-pointer">
          <input
            type="checkbox"
            checked={currentFilters.inStock}
            onChange={(e) => updateFilters({ inStock: e.target.checked })}
            className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
          />
          <span className="ml-2 text-sm text-gray-700">In stock only</span>
        </label>
      </div>

      {/* Clear Filters */}
      {getActiveFiltersCount() > 0 && (
        <button
          onClick={clearAllFilters}
          className="w-full px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 border border-gray-300 rounded-md hover:bg-gray-200 transition-colors"
        >
          Clear All Filters ({getActiveFiltersCount()})
        </button>
      )}
    </div>
  );

  return (
    <>
      {/* Mobile Filter Button */}
      <div className="lg:hidden mb-4">
        <button
          onClick={() => setShowMobileFilters(true)}
          className="flex items-center space-x-2 px-4 py-2 bg-white border border-gray-300 rounded-md shadow-sm hover:bg-gray-50"
        >
          <Filter className="h-4 w-4" />
          <span>Filters</span>
          {getActiveFiltersCount() > 0 && (
            <span className="bg-blue-600 text-white text-xs rounded-full px-2 py-1">
              {getActiveFiltersCount()}
            </span>
          )}
        </button>
      </div>

      {/* Desktop Filters */}
      <div className="hidden lg:block">
        <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-medium text-gray-900">Filters</h3>
            {getActiveFiltersCount() > 0 && (
              <span className="bg-blue-100 text-blue-800 text-xs font-medium px-2 py-1 rounded-full">
                {getActiveFiltersCount()} active
              </span>
            )}
          </div>
          {filtersContent}
        </div>
      </div>

      {/* Mobile Filters Modal */}
      {showMobileFilters && (
        <div className="fixed inset-0 z-50 lg:hidden">
          <div className="fixed inset-0 bg-black/25" onClick={() => setShowMobileFilters(false)} />
          <div className="fixed right-0 top-0 h-full w-full max-w-xs bg-white shadow-xl">
            <div className="flex items-center justify-between p-4 border-b border-gray-200">
              <h3 className="text-lg font-medium text-gray-900">Filters</h3>
              <button
                onClick={() => setShowMobileFilters(false)}
                className="p-2 -m-2 text-gray-400 hover:text-gray-500"
              >
                <X className="h-5 w-5" />
              </button>
            </div>
            <div className="p-4 overflow-y-auto h-full pb-20">
              {filtersContent}
            </div>
          </div>
        </div>
      )}
    </>
  );
}
