'use client';

import { useState, useEffect, useMemo } from 'react';
import StatCard from '@/components/admin/StatCard';
import InventoryFilters from '@/components/admin/inventory/InventoryFilters';
import AddShoeModal from '@/components/admin/inventory/AddShoeModal';
import InventoryTable from '@/components/admin/inventory/InventoryTable';
import { useShoesForInventory } from '@hooks';
import { ShoeFilters, Brand, Category } from '@types';
import { useShoeStats } from '@hooks';
import { ModalDialog } from '@/components/ModalDialog';

export default function InventoryPage() {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<Category | null>(
    null
  );
  const [selectedBrand, setSelectedBrand] = useState<Brand | null>(null);

  const [page, setPage] = useState(0);

  // Create filters object for API call
  const filters: ShoeFilters = useMemo(
    () => ({
      page,
      size: 20,
      searchTerm: searchTerm || undefined,
      categoryIds: selectedCategory ? [selectedCategory.id] : undefined,
      brandIds: selectedBrand ? [selectedBrand.id] : undefined,
      sortBy: 'name',
      sortDirection: 'asc' as const,
    }),
    [page, searchTerm, selectedCategory, selectedBrand]
  );

  // Fetch shoes with pagination
  const {
    data: shoesResponse,
    isLoading,
    error,
  } = useShoesForInventory(filters);

  const shoes = useMemo(
    () => shoesResponse?.content || [],
    [shoesResponse]
  );

  const {
    data: stats,
    isLoading: isStatsLoading,
    error: statsError,
  } = useShoeStats();

  // Handle pagination
  const handlePageChange = (newPage: number) => {
    setPage(newPage);
  };

  // Handle search with debouncing
  useEffect(() => {
    const timeoutId = setTimeout(() => {
      setPage(0); // Reset to first page when search changes
    }, 300);

    return () => clearTimeout(timeoutId);
  }, [searchTerm, selectedCategory]);

  // Handle clear filters
  const handleClearFilters = () => {
    setSearchTerm('');
    setSelectedBrand(null);
    setSelectedCategory(null);
  };

  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="h-32 w-32 animate-spin rounded-full border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="text-red-600">Error loading shoes: {error.message}</div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">
            Inventory Management
          </h1>
          <p className="text-gray-600">
            Manage shoes, models, and inventory by size
          </p>
        </div>
        <div className="flex space-x-3">
          <ModalDialog
            trigger={
              <button className="rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700">
                Add New Shoe
              </button>
            }
          >
            <AddShoeModal />
          </ModalDialog>
        </div>
      </div>

      {/* Stats Cards */}
      {stats && !isStatsLoading && !statsError && (
        <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
          <StatCard
            title="Total Shoes"
            value={stats.totalShoes}
            icon="👟"
            color="blue"
          />
          <StatCard
            title="Total Models"
            value={stats.totalModels}
            icon="🎨"
            color="green"
          />
          <StatCard
            title="Total Stock"
            value={stats.totalStock}
            icon="📦"
            color="yellow"
          />
          <StatCard
            title="Low Stock Shoes"
            value={stats.lowStockShoes}
            icon="⚠️"
            color="red"
          />
        </div>
      )}

      {/* Filters */}
      <InventoryFilters
        searchTerm={searchTerm}
        selectedBrand={selectedBrand}
        selectedCategory={selectedCategory}
        onCategoryChange={setSelectedCategory}
        onSearchChange={setSearchTerm}
        onBrandChange={setSelectedBrand}
        onClearFilters={handleClearFilters}
      />

      {/* Shoes Table */}
      <InventoryTable
        shoes={shoes}
        shoesResponse={shoesResponse}
        filters={filters}
        page={page}
        onPageChange={handlePageChange}
      />
    </div>
  );
}
