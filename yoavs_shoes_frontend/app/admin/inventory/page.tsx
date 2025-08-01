'use client';

import { useState, useEffect, useMemo } from 'react';
import StatCard from '@/components/admin/StatCard';
import InventoryFilters from '@/components/admin/inventory/InventoryFilters';
import AddSizeModal from '@/components/admin/inventory/AddSizeModal';
import AddShoeModal from '@/components/admin/inventory/AddShoeModal';
import AddModelModal from '@/components/admin/inventory/AddModelModal';
import InventoryTable from '@/components/admin/inventory/InventoryTable';
import { useShoesForInventory } from '@hooks';
import { ShoeInventoryView, ExtendedShoe, ExtendedShoeModel, ShoeFilters, Brand, Category} from '@types';
import { useShoeStats } from '@/lib/hooks/shoes/useShoes';

export default function InventoryPage() {

  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<Category | null>(null);
  const [selectedBrand, setSelectedBrand] = useState<Brand | null>(null);

  const [page, setPage] = useState(0);
  const [showAddShoeModal, setShowAddShoeModal] = useState(false);
  const [showAddModelModal, setShowAddModelModal] = useState(false);
  const [editingShoeModel, setEditingShoeModel] = useState<ExtendedShoeModel | null>(null);
  const [showSizeModal, setShowSizeModal] = useState(false);
  const [selectedModelForSizes, setSelectedModelForSizes] = useState<ExtendedShoeModel | null>(null);
  const [selectedShoeForModel, setSelectedShoeForModel] = useState<ShoeInventoryView | null>(null);

  // Create filters object for API call
  const filters: ShoeFilters = useMemo(() => ({
    page,
    size: 20,
    searchTerm: searchTerm || undefined,
    categoryIds: selectedCategory ? selectedCategory.id : undefined,
    brandIds: selectedBrand ? selectedBrand.id : undefined,
    sortBy: 'name',
    sortDirection: 'asc' as const,
  }), [page, searchTerm, selectedCategory, selectedBrand]);

  // Fetch shoes with pagination
  const { data: shoesResponse, isLoading, error } = useShoesForInventory(filters);

  const shoes = useMemo(() => shoesResponse?.data?.content || [], [shoesResponse]);

  const { data: stats, isLoading: isStatsLoading, error: statsError } = useShoeStats();



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

  const handleAddShoe = (shoeData: any) => {
    const newShoe: ExtendedShoe = {
      id: Date.now(),
      name: shoeData.name,
      basePrice: parseFloat(shoeData.basePrice),
      gender: shoeData.gender,
      brand: { id: parseInt(shoeData.brandId), name: shoeData.brandName },
      category: { id: parseInt(shoeData.categoryId), name: shoeData.categoryName },
      models: [],
      totalStock: 0,
      totalModels: 0
    };

    // In a real app, this would trigger a mutation and invalidate queries
    setShowAddShoeModal(false);
  };

  const handleSaveModel = (modelData: any) => {
    console.log('Saving model:', modelData);
    setShowAddModelModal(false);
    setEditingShoeModel(null);
    setSelectedShoeForModel(null);
  };

  // Handle clear filters
  const handleClearFilters = () => {
    setSearchTerm('');
    setSelectedBrand(null);
    setSelectedCategory(null);
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-red-600">Error loading shoes: {error.message}</div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Inventory Management</h1>
          <p className="text-gray-600">Manage shoes, models, and inventory by size</p>
        </div>
        <div className="flex space-x-3">
          <button
            onClick={() => setShowAddShoeModal(true)}
            className="bg-blue-600 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-blue-700"
          >
            Add New Shoe
          </button>
        </div>
      </div>

      {/* Stats Cards */}
      {(stats && !isStatsLoading && !statsError) && (
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
        onEditModel={setEditingShoeModel}
        onAddSize={(model) => {
          setSelectedModelForSizes(model);
          setShowSizeModal(true);
        }}
        onAddModel={(shoe) => {
          setSelectedShoeForModel(shoe);
          setShowAddModelModal(true);
        }}
      />

      {/* Modals */}
      <AddSizeModal
        isOpen={showSizeModal}
        selectedModel={selectedModelForSizes}
        onClose={() => {
          setShowSizeModal(false);
          setSelectedModelForSizes(null);
        }}
        onAddSize={() => {}} // TODO: Implement with mutations
      />

      <AddShoeModal
        isOpen={showAddShoeModal}
        onClose={() => setShowAddShoeModal(false)}
        onAddShoe={handleAddShoe}
      />

      <AddModelModal
        isOpen={showAddModelModal || !!editingShoeModel}
        editingModel={editingShoeModel}
        selectedShoe={selectedShoeForModel}
        shoes={shoes}
        onClose={() => {
          setShowAddModelModal(false);
          setEditingShoeModel(null);
          setSelectedShoeForModel(null);
        }}
        onSelectShoe={setSelectedShoeForModel}
        onSave={handleSaveModel}
      />
    </div>
  );
}