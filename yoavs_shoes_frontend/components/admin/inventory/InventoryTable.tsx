import InventoryShoeRow from '@/components/admin/inventory/InventoryShoeRow';
import { ShoeInventoryView, ShoeFilters, PageResponse } from '@types';

interface InventoryTableProps {
  shoes: ShoeInventoryView[];
  shoesResponse: PageResponse<ShoeInventoryView> | undefined;
  filters: ShoeFilters;
  page: number;
  onPageChange: (newPage: number) => void;
}

export default function InventoryTable({
  shoes,
  shoesResponse,
  filters,
  page,
  onPageChange,
}: InventoryTableProps) {
  return (
    <div className="overflow-hidden rounded-lg bg-white shadow">
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">
                Shoe / Model
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">
                Variants / Color
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">
                Price
              </th>

              <th className="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">
                Total Stock
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">
                Status
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">
                Actions
              </th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200 bg-white">
            {shoes.map(shoe => (
              <InventoryShoeRow key={shoe.id} shoe={shoe} />
            ))}
          </tbody>
        </table>
      </div>

      {shoes.length === 0 && (
        <div className="py-12 text-center">
          <div className="text-gray-500">No shoes found</div>
        </div>
      )}

      {/* Pagination */}
      {shoesResponse && shoesResponse?.totalPages > 1 && (
        <div className="flex items-center justify-between border-t border-gray-200 px-6 py-3">
          <div className="flex flex-1 justify-between sm:hidden">
            <button
              onClick={() => onPageChange(page - 1)}
              disabled={page === 0}
              className="relative inline-flex items-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50 disabled:opacity-50"
            >
              Previous
            </button>
            <button
              onClick={() => onPageChange(page + 1)}
              disabled={page >= shoesResponse?.totalPages - 1}
              className="relative ml-3 inline-flex items-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50 disabled:opacity-50"
            >
              Next
            </button>
          </div>
          <div className="hidden sm:flex sm:flex-1 sm:items-center sm:justify-between">
            <div>
              <p className="text-sm text-gray-700">
                Showing{' '}
                <span className="font-medium">{page * filters.size! + 1}</span>{' '}
                to{' '}
                <span className="font-medium">
                  {Math.min(
                    (page + 1) * filters.size!,
                    shoesResponse.totalElements
                  )}
                </span>{' '}
                of{' '}
                <span className="font-medium">
                  {shoesResponse.totalElements}
                </span>{' '}
                results
              </p>
            </div>
            <div>
              <nav className="relative z-0 inline-flex -space-x-px rounded-md shadow-sm">
                <button
                  onClick={() => onPageChange(page - 1)}
                  disabled={page === 0}
                  className="relative inline-flex items-center rounded-l-md border border-gray-300 bg-white px-2 py-2 text-sm font-medium text-gray-500 hover:bg-gray-50 disabled:opacity-50"
                >
                  Previous
                </button>
                <button
                  onClick={() => onPageChange(page + 1)}
                  disabled={page >= shoesResponse.totalPages - 1}
                  className="relative inline-flex items-center rounded-r-md border border-gray-300 bg-white px-2 py-2 text-sm font-medium text-gray-500 hover:bg-gray-50 disabled:opacity-50"
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
