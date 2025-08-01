import { ShoeModel } from '@types';

interface ExtendedShoeModel extends ShoeModel {
  totalStock: number;
}

interface InventoryTableRowProps {
  model: ExtendedShoeModel;
  isExpanded: boolean;
  onToggleExpansion: (modelId: number) => void;
  onStockUpdate: (modelId: number, sizeId: number, newQuantity: number) => void;
  onRemoveSize: (modelId: number, sizeId: number) => void;
  onEdit: (model: ExtendedShoeModel) => void;
  onDelete: (modelId: number) => void;
  onAddSize: (model: ExtendedShoeModel) => void;
}

export default function InventoryTableRow({
  model,
  isExpanded,
  onToggleExpansion,
  onStockUpdate,
  onRemoveSize,
  onEdit,
  onDelete,
  onAddSize
}: InventoryTableRowProps) {
  const getStockStatus = (totalStock: number) => {
    if (totalStock === 0) return { label: 'Out of Stock', color: 'text-red-600 bg-red-100' };
    if (totalStock <= 10) return { label: 'Low Stock', color: 'text-yellow-600 bg-yellow-100' };
    return { label: 'In Stock', color: 'text-green-600 bg-green-100' };
  };

  const stockStatus = getStockStatus(model.totalStock);
  const sizesToShow = isExpanded ? model.availableSizes : model.availableSizes.slice(0, 3);
  const hasMoreSizes = model.availableSizes.length > 3;

  return (
    <>
      <tr>
        <td className="px-6 py-4 whitespace-nowrap">
          <div className="flex items-center">
            <div className="flex-shrink-0 h-10 w-10">
              <div className="h-10 w-10 rounded-full bg-gray-200 flex items-center justify-center">
                👟
              </div>
            </div>
            <div className="ml-4">
              <div className="text-sm font-medium text-gray-900">{model.fullDisplayName}</div>
              <div className="text-sm text-gray-500">SKU: {model.sku}</div>
            </div>
          </div>
        </td>
        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
          {model.shoe.category.name}
        </td>
        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
          ${model.price}
        </td>
        <td className="px-6 py-4">
          <div className="space-y-2">
            <div className="flex flex-wrap gap-1">
              {sizesToShow.map((size) => (
                <div
                  key={size.id}
                  className={`inline-flex items-center px-2 py-1 rounded text-xs font-medium ${
                    size.quantityAvailable === 0
                      ? 'bg-red-100 text-red-800'
                      : size.quantityAvailable <= 2
                      ? 'bg-yellow-100 text-yellow-800'
                      : 'bg-green-100 text-green-800'
                  }`}
                >
                  {size.size}: {size.quantityAvailable}
                </div>
              ))}
              {hasMoreSizes && !isExpanded && (
                <button
                  onClick={() => onToggleExpansion(model.id)}
                  className="inline-flex items-center px-2 py-1 rounded text-xs font-medium bg-gray-100 text-gray-600 hover:bg-gray-200"
                >
                  +{model.availableSizes.length - 3} more
                </button>
              )}
            </div>
            
            <div className="flex space-x-2">
              <button
                onClick={() => onAddSize(model)}
                className="text-indigo-600 hover:text-indigo-900 text-xs font-medium"
              >
                + Add Size
              </button>
              
                <button
                  onClick={() => onToggleExpansion(model.id)}
                  className="text-gray-600 hover:text-gray-900 text-xs font-medium"
                >
                  {isExpanded ? 'Show Less' : 'Manage Sizes'}
                </button>
              
            </div>
          </div>
        </td>
        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
          {model.totalStock}
        </td>
        <td className="px-6 py-4 whitespace-nowrap">
          <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${stockStatus.color}`}>
            {stockStatus.label}
          </span>
        </td>
        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium space-x-2">
          <button
            onClick={() => onEdit(model)}
            className="text-indigo-600 hover:text-indigo-900"
          >
            Edit
          </button>
          <button
            onClick={() => onDelete(model.id)}
            className="text-red-600 hover:text-red-900"
          >
            Delete
          </button>
        </td>
      </tr>
      
      {isExpanded && (
        <tr className="bg-gray-50">
          <td colSpan={7} className="px-6 py-4">
            <div className="space-y-3">
              <h4 className="text-sm font-medium text-gray-900">Manage Sizes for {model.fullDisplayName}</h4>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
                {model.availableSizes.map((size) => (
                  <div key={size.id} className="bg-white p-3 rounded border border-gray-200">
                    <div className="flex items-center justify-between mb-2">
                      <span className="text-sm font-medium text-gray-900">Size {size.size}</span>
                      <button
                        onClick={() => onRemoveSize(model.id, size.id)}
                        className="text-red-500 hover:text-red-700 text-xs"
                      >
                        Remove
                      </button>
                    </div>
                    <div className="space-y-1">
                      <div className="flex items-center space-x-2">
                        <label className="text-xs text-gray-600">Quantity:</label>
                        <input
                          type="number"
                          min="0"
                          value={size.quantityAvailable}
                          onChange={(e) => onStockUpdate(model.id, size.id, parseInt(e.target.value) || 0)}
                          className="w-20 border border-gray-300 rounded px-2 py-1 text-xs"
                        />
                      </div>
                      <div className="text-xs text-gray-500">
                        Reserved: {size.quantityReserved} | Available: {size.actualAvailableQuantity}
                      </div>
                      <div className={`text-xs ${size.inStock ? 'text-green-600' : 'text-red-600'}`}>
                        {size.inStock ? 'In Stock' : 'Out of Stock'}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </td>
        </tr>
      )}
    </>
  );
}
