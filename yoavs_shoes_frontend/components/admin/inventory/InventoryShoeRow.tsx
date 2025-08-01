import { useState } from 'react';
import { ExtendedShoe, ExtendedShoeModel } from '@/lib/types/product';
import SizeManagementCard from './SizeManagementCard';
import React from 'react';
import { ShoeInventoryView } from '@/lib/types';

interface InventoryShoeRowProps {
  shoe: ShoeInventoryView;
  onStockUpdate: (modelId: number, sizeId: number, newQuantity: number) => void;
  onEditModel: (model: ExtendedShoeModel) => void;
  onDeleteModel: (modelId: number) => void;
  onAddSize: (model: ExtendedShoeModel) => void;
  onAddModel: (shoe: ShoeInventoryView) => void;
}

export default function InventoryShoeRow({
  shoe,
  onStockUpdate,
  onEditModel,
  onDeleteModel,
  onAddSize,
  onAddModel
}: InventoryShoeRowProps) {
  const [isExpanded, setIsExpanded] = useState(false);
  const [expandedModels, setExpandedModels] = useState<Set<number>>(new Set());

  const getStockStatus = (totalStock: number) => {
    if (totalStock === 0) return { label: 'Out of Stock', color: 'text-red-600 bg-red-100' };
    if (totalStock <= 10) return { label: 'Low Stock', color: 'text-yellow-600 bg-yellow-100' };
    return { label: 'In Stock', color: 'text-green-600 bg-green-100' };
  };

  const toggleModelExpansion = (modelId: number) => {
    setExpandedModels(prev => {
      const newSet = new Set(prev);
      if (newSet.has(modelId)) {
        newSet.delete(modelId);
      } else {
        newSet.add(modelId);
      }
      return newSet;
    });
  };

  const stockStatus = getStockStatus(shoe.totalStock);

  return (
    <>
      {/* Main Shoe Row */}
      <tr className="bg-blue-50 border-t-2 border-blue-200">
        <td className="px-6 py-4 whitespace-nowrap">
          <div className="flex items-center">
            <button
              onClick={() => setIsExpanded(!isExpanded)}
              className="mr-3 text-gray-400 hover:text-gray-600"
            >
              {isExpanded ? '▼' : '▶'}
            </button>
            <div className="flex items-center">
              <div className="flex-shrink-0 h-12 w-12">
                <div className="h-12 w-12 rounded-lg bg-blue-200 flex items-center justify-center">
                  👟
                </div>
              </div>
              <div className="ml-4">
                <div className="text-lg font-bold text-gray-900">{shoe.name}</div>
                <div className="text-sm text-gray-600">{shoe.brand.name} • {shoe.category.name}</div>
              </div>
            </div>
          </div>
        </td>
        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
          {shoe.modelCount} models
        </td>
        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
          ${shoe.basePrice}
        </td>
        <td className="px-6 py-4">
          <div className="text-sm text-gray-600">
            {shoe.modelCount} color variants
          </div>
        </td>
        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900 font-medium">
          {shoe.totalStock}
        </td>
        <td className="px-6 py-4 whitespace-nowrap">
          <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${stockStatus.color}`}>
            {stockStatus.label}
          </span>
        </td>
        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
          <button
            onClick={() => onAddModel(shoe)}
            className="text-blue-600 hover:text-blue-900"
          >
            + Add Model
          </button>
        </td>
      </tr>

      {/* Expanded Models */}
      {isExpanded && shoe.models.map((model) => {
        const isModelExpanded = expandedModels.has(model.id);
        const modelStockStatus = getStockStatus(model.totalStock);
        const sizesToShow = isModelExpanded ? model.availableSizes : model.availableSizes.slice(0, 3);
        const hasMoreSizes = model.availableSizes.length > 3;

        return (
          <React.Fragment key={model.id}>
            {/* Model Row */}
            <tr className="bg-gray-50">
              <td className="px-6 py-3 whitespace-nowrap">
                <div className="flex items-center ml-8">
                  <div className="flex-shrink-0 h-8 w-8">
                    <div className="h-8 w-8 rounded bg-gray-300 flex items-center justify-center">
                      🎨
                    </div>
                  </div>
                  <div className="ml-3">
                    <div className="text-sm font-medium text-gray-900">{model.modelName}</div>
                    <div className="text-xs text-gray-500">SKU: {model.sku}</div>
                  </div>
                </div>
              </td>
              <td className="px-6 py-3 whitespace-nowrap text-sm text-gray-700">
                {model.color}
              </td>
              <td className="px-6 py-3 whitespace-nowrap text-sm text-gray-700">
                ${model.price}
              </td>
              <td className="px-6 py-3">
                <div className="space-y-1">
                  <div className="flex flex-wrap gap-1">
                    {sizesToShow.map((size) => (
                      <span
                        key={size.id}
                        className={`inline-flex items-center px-1.5 py-0.5 rounded text-xs font-medium ${
                          size.quantityAvailable === 0
                            ? 'bg-red-100 text-red-800'
                            : size.quantityAvailable <= 2
                            ? 'bg-yellow-100 text-yellow-800'
                            : 'bg-green-100 text-green-800'
                        }`}
                      >
                        {size.size}:{size.quantityAvailable}
                      </span>
                    ))}
                    {hasMoreSizes && !isModelExpanded && (
                      <button
                        onClick={() => toggleModelExpansion(model.id)}
                        className="inline-flex items-center px-1.5 py-0.5 rounded text-xs font-medium bg-gray-100 text-gray-600 hover:bg-gray-200"
                      >
                        +{model.availableSizes.length - 3}
                      </button>
                    )}
                  </div>
                  <div className="flex space-x-2 text-xs">
                    <button
                      onClick={() => onAddSize(model)}
                      className="text-indigo-600 hover:text-indigo-900 font-medium"
                    >
                      + Size
                    </button>
                    <button
                      onClick={() => toggleModelExpansion(model.id)}
                      className="text-gray-600 hover:text-gray-900 font-medium"
                    >
                      {isModelExpanded ? 'Less' : 'Manage'}
                    </button>
                  </div>
                </div>
              </td>
              <td className="px-6 py-3 whitespace-nowrap text-sm text-gray-700">
                {model.totalStock}
              </td>
              <td className="px-6 py-3 whitespace-nowrap">
                <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${modelStockStatus.color}`}>
                  {modelStockStatus.label}
                </span>
              </td>
              <td className="px-6 py-3 whitespace-nowrap text-sm font-medium space-x-1">
                <button
                  onClick={() => onEditModel(model)}
                  className="text-indigo-600 hover:text-indigo-900 text-xs"
                >
                  Edit
                </button>
                <button
                  onClick={() => onDeleteModel(model.id)}
                  className="text-red-600 hover:text-red-900 text-xs"
                >
                  Delete
                </button>
              </td>
            </tr>

            {/* Expanded Size Management */}
            {isModelExpanded && (
              <tr className="bg-gray-100">
                <td colSpan={7} className="px-6 py-3">
                  <div className="ml-16 space-y-2">
                    <h5 className="text-xs font-medium text-gray-900">Size Management</h5>
                    <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-2">
                      {model.availableSizes.map((size) => (
                        <SizeManagementCard
                          key={size.id}
                          size={size}
                          onStockUpdate={onStockUpdate}
                        />
                      ))}
                    </div>
                  </div>
                </td>
              </tr>
            )}
          </React.Fragment>
        );
      })}
    </>
  );
}
