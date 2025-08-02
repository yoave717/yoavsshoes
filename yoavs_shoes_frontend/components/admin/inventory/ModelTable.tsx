import { useState } from 'react';
import SizeManagementCard from './SizeManagementCard';
import React from 'react';
import { ShoeInventoryView, ShoeModelInventoryView } from '@types';
import AddSizeModal from './AddSizeModal';
import { ModalDialog } from '@/components/ModalDialog';
import ModelDetailsModal from './ModelDetailsModal';

interface ModelTableProps {
  models: ShoeModelInventoryView[];
  shoe: ShoeInventoryView;
  onStockUpdate: (modelId: number, size: string, newQuantity: number) => void;
  onDeleteModel: (modelId: number) => void;
}

export default function ModelTable({
  models,
  shoe,
  onStockUpdate,
  onDeleteModel,
}: ModelTableProps) {
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

  return (
    <div className="mt-4 bg-white rounded-lg shadow overflow-hidden">
      <table className="min-w-full divide-y divide-gray-200">
        <thead className="bg-gray-50">
          <tr>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Model / SKU
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Color
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
          {models.map((model) => {
            const isModelExpanded = expandedModels.has(model.id);
            const modelStockStatus = getStockStatus(model.totalStock);
            const sizesToShow = isModelExpanded ? model.availableSizes : model.availableSizes.slice(0, 3);
            const hasMoreSizes = model.availableSizes.length > 3;

            return (
              <React.Fragment key={model.id}>
                {/* Model Row */}
                <tr>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
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
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                    {model.color}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                    ${model.price}
                  </td>
                  <td className="px-6 py-4">
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
                        <ModalDialog trigger={
                          <button className="text-indigo-600 hover:text-indigo-900 font-medium">+ Size</button>
                        }>
                          <AddSizeModal
                            selectedModel={model}
                            selectedShoe={shoe}
                          />
                        </ModalDialog>
                        <button
                          onClick={() => toggleModelExpansion(model.id)}
                          className="text-gray-600 hover:text-gray-900 font-medium"
                        >
                          {isModelExpanded ? 'Less' : 'Manage'}
                        </button>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                    {model.totalStock}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${modelStockStatus.color}`}>
                      {modelStockStatus.label}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium space-x-1">
                    <ModalDialog trigger={
                      <button className="text-indigo-600 hover:text-indigo-900 text-xs">
                        Edit
                      </button>
                    }>
                      <ModelDetailsModal
                        selectedShoe={shoe}
                        editingModel={model}
                      />
                    </ModalDialog>
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
                      <div className="space-y-2">
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
        </tbody>
      </table>
    </div>
  );
}
