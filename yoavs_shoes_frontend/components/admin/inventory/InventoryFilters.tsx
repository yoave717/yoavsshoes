'use client';

import { Brand, Category } from '@/lib/types';
import { useBrands, useCategories } from '@hooks';

interface InventoryFiltersProps {
  searchTerm: string;
  selectedBrand: Brand | null;
  selectedCategory?: Category | null;
  onCategoryChange?: (value: Category | null) => void;
  onSearchChange: (value: string) => void;
  onBrandChange: (value: Brand | null) => void;
  onClearFilters: () => void;
}

export default function InventoryFilters({
  searchTerm,
  selectedBrand,
  selectedCategory,
  onCategoryChange,
  onSearchChange,
  onBrandChange,
  onClearFilters
}: InventoryFiltersProps) {

  const { data: brands } = useBrands();

  const { data: categories } = useCategories();

  return (
    <div className="bg-white shadow rounded-lg p-6">
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Search Shoes
          </label>
          <input
            type="text"
            placeholder="Search by name, brand, or color..."
            value={searchTerm}
            onChange={(e) => onSearchChange(e.target.value)}
            className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Brand
          </label>
            <select
                value={selectedBrand ? selectedBrand.name : ''}
                onChange={(e) => {
                const brand = brands?.content.find(b => b.name === e.target.value) || null;
                onBrandChange(brand);
                }}
                className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
            >
              <option value="">Select a brand</option>
              {brands && brands.content.map(brand => (
                <option key={brand.id} value={brand.name}>{brand.name}</option>
              ))}
            </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Category
          </label>
          <select
            value={selectedCategory ? selectedCategory.name : ''}
            onChange={(e) => {
              const category = categories?.content.find(c => c.name === e.target.value) || null;
              onCategoryChange?.(category);
            }}
            className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value="">Select a category</option>
            {categories && categories.content.map(category => (
              <option key={category.id} value={category.name}>{category.name}</option>
            ))}
          </select>

        </div>

        <div className="flex items-end">
          <button
            onClick={onClearFilters}
            className="w-full bg-gray-200 text-gray-700 px-4 py-2 rounded-md text-sm font-medium hover:bg-gray-300"
          >
            Clear Filters
          </button>
        </div>
      </div>
    </div>
  );
}
