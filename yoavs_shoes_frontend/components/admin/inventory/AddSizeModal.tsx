import { useState } from 'react';
import { ShoeModelInventoryView } from '@types';

interface AddSizeModalProps {
  isOpen: boolean;
  selectedModel: ShoeModelInventoryView;
  onClose: () => void;
  onAddSize: (shoeId: number, shoeModelId: number, size: string, quantity: number) => void;
}

export default function AddSizeModal({ 
  isOpen, 
  selectedModel, 
  onClose, 
  onAddSize 
}: AddSizeModalProps) {
  const [newSize, setNewSize] = useState('');
  const [newSizeQuantity, setNewSizeQuantity] = useState(0);

  const handleSubmit = () => {
    if (newSize) {
      onAddSize(selectedModel.shoe.id, selectedModel.id, newSize, newSizeQuantity);
      setNewSize('');
      setNewSizeQuantity(0);
      onClose();
    }
  };

  const handleClose = () => {
    setNewSize('');
    setNewSizeQuantity(0);
    onClose();
  };

  if (!isOpen || !selectedModel) return null;

  return (
    <div className="fixed inset-0 bg-gray-600/60 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 w-full max-w-sm">
        <h3 className="text-lg font-medium text-gray-900 mb-4">
          Add Size to {selectedModel.fullDisplayName}
        </h3>
        
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Size</label>
            <select
              value={newSize}
              onChange={(e) => setNewSize(e.target.value)}
              className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
            >
              <option value="">Select Size</option>
              {['6', '6.5', '7', '7.5', '8', '8.5', '9', '9.5', '10', '10.5', '11', '11.5', '12'].map(size => (
                <option 
                  key={size} 
                  value={size} 
                  disabled={selectedModel.availableSizes.some(s => s.size === size)}
                >
                  {size} {selectedModel.availableSizes.some(s => s.size === size) ? '(Already exists)' : ''}
                </option>
              ))}
            </select>
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Initial Quantity</label>
            <input
              type="number"
              min="0"
              value={newSizeQuantity}
              onChange={(e) => setNewSizeQuantity(parseInt(e.target.value) || 0)}
              className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>
        </div>
        
        <div className="mt-6 flex justify-end space-x-3">
          <button
            onClick={handleClose}
            className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-200 rounded-md hover:bg-gray-300"
          >
            Cancel
          </button>
          <button
            onClick={handleSubmit}
            disabled={!newSize}
            className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 rounded-md hover:bg-indigo-700 disabled:bg-gray-400"
          >
            Add Size
          </button>
        </div>
      </div>
    </div>
  );
}
