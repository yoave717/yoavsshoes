import { useState } from 'react';
import { ExtendedShoeModel } from '@/lib/types/product';
import React from 'react';
import { ShoeInventoryView, ShoeModelInventoryView } from '@/lib/types';
import { useShoeModels } from '@/lib/hooks/shoes/useShoes';
import ModelTable from './ModelTable';

interface InventoryShoeRowProps {
  shoe: ShoeInventoryView;
  onStockUpdate: (modelId: number, size: string, newQuantity: number) => void;
  onEditModel: (model: ExtendedShoeModel) => void;
  onDeleteModel: (modelId: number) => void;
  onAddSize: (model: ShoeModelInventoryView) => void;
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

  const { data: models, isLoading: isModelsLoading } = useShoeModels(shoe.id);

  const getStockStatus = (totalStock: number) => {
    if (totalStock === 0) return { label: 'Out of Stock', color: 'text-red-600 bg-red-100' };
    if (totalStock <= 10) return { label: 'Low Stock', color: 'text-yellow-600 bg-yellow-100' };
    return { label: 'In Stock', color: 'text-green-600 bg-green-100' };
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

      {/* Expanded Models Table */}
      {isExpanded && (
        <tr>
          <td colSpan={7} className="px-6 py-4 bg-gray-50">
            <div className="space-y-2">
              <h4 className="text-sm font-medium text-gray-900">Models for {shoe.name}</h4>
              {!isModelsLoading && models ? (
                <ModelTable
                  models={models}
                  onStockUpdate={onStockUpdate}
                  onEditModel={onEditModel}
                  onDeleteModel={onDeleteModel}
                  onAddSize={onAddSize}
                />
              ) : 
                <div className="text-gray-500">Loading models...</div>}
            </div>
          </td>
        </tr>
      )}
    </>
  );
}
