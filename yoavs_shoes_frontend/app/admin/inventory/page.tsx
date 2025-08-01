'use client';

import { useState, useEffect, useMemo } from 'react';
import StatCard from '@/components/admin/StatCard';
import InventoryShoeRow from '@/components/admin/inventory/InventoryShoeRow';
import AddSizeModal from '@/components/admin/inventory/AddSizeModal';
import AddShoeModal from '@/components/admin/inventory/AddShoeModal';
import AddModelModal from '@/components/admin/inventory/AddModelModal';
import { useShoesForInventory } from '@hooks';
import { ShoeInventoryView, ExtendedShoe, ExtendedShoeModel, ShoeFilters} from '@types';
import { useShoeStats } from '@/lib/hooks/shoes/useShoes';

export default function InventoryPage() {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
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
    categoryIds: selectedCategory ? [parseInt(selectedCategory)] : undefined,
    sortBy: 'name',
    sortDirection: 'asc' as const,
  }), [page, searchTerm, selectedCategory]);

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
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>

          {/* <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Category
            </label>
            <select
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value)}
              className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
            >
              <option value="">All Categories</option>
              {categories.map(category => (
                <option key={category} value={category}>{category}</option>
              ))}
            </select>
          </div> */}

          <div className="flex items-end">
            <button
              onClick={() => {
                setSearchTerm('');
                setSelectedCategory('');
              }}
              className="w-full bg-gray-200 text-gray-700 px-4 py-2 rounded-md text-sm font-medium hover:bg-gray-300"
            >
              Clear Filters
            </button>
          </div>
        </div>
      </div>

      {/* Shoes Table */}
      <div className="bg-white shadow rounded-lg overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Shoe / Model
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Variants / Color
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Price
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Sizes & Stock
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Total Stock
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {shoes.map((shoe) => (
                <InventoryShoeRow
                  key={shoe.id}
                  shoe={shoe}
                  onStockUpdate={() => {}} // TODO: Implement with mutations
                  onRemoveSize={() => {}} // TODO: Implement with mutations
                  onEditModel={setEditingShoeModel}
                  onDeleteModel={() => {}} // TODO: Implement with mutations
                  onAddSize={(model) => {
                    setSelectedModelForSizes(model);
                    setShowSizeModal(true);
                  }}
                  onAddModel={(shoe) => {
                    setSelectedShoeForModel(shoe);
                    setShowAddModelModal(true);
                  }}
                />
              ))}
            </tbody>
          </table>
        </div>

        {shoes.length === 0 && (
          <div className="text-center py-12">
            <div className="text-gray-500">No shoes found</div>
          </div>
        )}

        {/* Pagination */}
        {shoesResponse && shoesResponse.data?.totalPages > 1 && (
          <div className="px-6 py-3 flex items-center justify-between border-t border-gray-200">
            <div className="flex-1 flex justify-between sm:hidden">
              <button
                onClick={() => handlePageChange(page - 1)}
                disabled={page === 0}
                className="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
              >
                Previous
              </button>
              <button
                onClick={() => handlePageChange(page + 1)}
                disabled={page >= shoesResponse.data?.totalPages - 1}
                className="ml-3 relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
              >
                Next
              </button>
            </div>
            <div className="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
              <div>
                <p className="text-sm text-gray-700">
                  Showing <span className="font-medium">{page * filters.size! + 1}</span> to{' '}
                  <span className="font-medium">
                    {Math.min((page + 1) * filters.size!, shoesResponse.data.totalElements)}
                  </span>{' '}
                  of <span className="font-medium">{shoesResponse.data.totalElements}</span> results
                </p>
              </div>
              <div>
                <nav className="relative z-0 inline-flex rounded-md shadow-sm -space-x-px">
                  <button
                    onClick={() => handlePageChange(page - 1)}
                    disabled={page === 0}
                    className="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 disabled:opacity-50"
                  >
                    Previous
                  </button>
                  <button
                    onClick={() => handlePageChange(page + 1)}
                    disabled={page >= shoesResponse.data.totalPages - 1}
                    className="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 disabled:opacity-50"
                  >
                    Next
                  </button>
                </nav>
              </div>
            </div>
          </div>
        )}
      </div>

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