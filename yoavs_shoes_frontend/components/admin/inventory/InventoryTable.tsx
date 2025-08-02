'use client';

import InventoryShoeRow from '@/components/admin/inventory/InventoryShoeRow';
import { useDeleteShoeModel } from '@/lib/hooks/shoes/useDeleteShoeModel';
import { useUpdateShoeInventory } from '@/lib/hooks/shoes/useShoes';
import { ShoeInventoryView, ExtendedShoeModel, ShoeFilters } from '@types';

interface InventoryTableProps {
  shoes: ShoeInventoryView[];
  shoesResponse: any;
  filters: ShoeFilters;
  page: number;
  onPageChange: (newPage: number) => void;
  onEditModel: (model: ExtendedShoeModel) => void;
  onAddModel: (shoe: ShoeInventoryView) => void;
}

export default function InventoryTable({
  shoes,
  shoesResponse,
  filters,
  page,
  onPageChange,
  onEditModel,
  onAddModel
}: InventoryTableProps) {
    const { mutate: updateInventory } = useUpdateShoeInventory();
    const { mutate: deleteShoeModel } = useDeleteShoeModel();
    const handleStockUpdate = (shoeId: number, modelId: number, size: string, newQuantity: number) => {
        updateInventory({ shoeId, modelId, size, quantityAvailable: newQuantity });
    };

    const handleModelDeletion = (shoeId: number, modelId: number) => {
        const confirmed = window.confirm('Are you sure you want to delete this shoe model? This action cannot be undone.');
        if (confirmed) {
            deleteShoeModel({ shoeId, shoeModelId: modelId });
        }
    };

  return (
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
                onStockUpdate={(modelId, size, newQuantity) => handleStockUpdate(shoe.id, modelId, size, newQuantity)}
                onRemoveSize={() => {}} // TODO: Implement with mutations
                onEditModel={onEditModel}
                onDeleteModel={(modelId) => handleModelDeletion(shoe.id, modelId)}
                onAddModel={onAddModel}
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
              onClick={() => onPageChange(page - 1)}
              disabled={page === 0}
              className="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
            >
              Previous
            </button>
            <button
              onClick={() => onPageChange(page + 1)}
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
                  onClick={() => onPageChange(page - 1)}
                  disabled={page === 0}
                  className="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 disabled:opacity-50"
                >
                  Previous
                </button>
                <button
                  onClick={() => onPageChange(page + 1)}
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
  );
}
