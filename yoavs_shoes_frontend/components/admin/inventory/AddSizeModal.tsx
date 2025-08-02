import { useState } from 'react';
import { ShoeInventoryView, ShoeModelInventoryView } from '@types';
import { useCreateShoeInventory } from '@/lib/hooks/shoes/useShoes';

interface AddSizeModalProps {
  trigger: React.ReactNode;
  selectedModel: ShoeModelInventoryView;
  selectedShoe: ShoeInventoryView;
}

export default function AddSizeModal({
  trigger,
  selectedModel,
  selectedShoe,
}: AddSizeModalProps) {
  const [isOpen, setIsOpen] = useState(false);

  const [newSize, setNewSize] = useState('');
  const [newSizeQuantity, setNewSizeQuantity] = useState(0);

  const { mutate: createShoeInventory } = useCreateShoeInventory();


  const handleSubmit = () => {
    if (!newSize) return;
    createShoeInventory(
      {
        shoeId: selectedModel.shoeId,
        shoeModelId: selectedModel.id,
        size: newSize,
        quantityAvailable: newSizeQuantity,
      },
      {
        onSuccess: () => {
          setNewSize('');
          setNewSizeQuantity(0);
          setIsOpen(false);
        },
      }
    );
  };

  const handleClose = () => {
    setNewSize('');
    setNewSizeQuantity(0);
    setIsOpen(false);};

  return (
    <>
      <span onClick={() => setIsOpen(true)}>{trigger}</span>

      {/* Modal */}
      {isOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-gray-600/60">
          <div className="w-full max-w-sm rounded-lg bg-white p-6">
            <h3 className="mb-4 text-lg font-medium text-gray-900">
              Add Size to {selectedShoe.name} {selectedModel.displayName}
            </h3>

            <div className="space-y-4">
              <div>
                <label className="mb-1 block text-sm font-medium text-gray-700">
                  Size
                </label>
                <select
                  value={newSize}
                  onChange={e => setNewSize(e.target.value)}
                  className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                >
                  <option value="">Select Size</option>
                  {[
                    '6',
                    '6.5',
                    '7',
                    '7.5',
                    '8',
                    '8.5',
                    '9',
                    '9.5',
                    '10',
                    '10.5',
                    '11',
                    '11.5',
                    '12',
                  ].map(size => (
                    <option
                      key={size}
                      value={size}
                      disabled={selectedModel.availableSizes?.some(
                        s => s.size === size
                      )}
                    >
                      {size}{' '}
                      {selectedModel.availableSizes?.some(s => s.size === size)
                        ? '(Already exists)'
                        : ''}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="mb-1 block text-sm font-medium text-gray-700">
                  Initial Quantity
                </label>
                <input
                  type="number"
                  min="0"
                  value={newSizeQuantity}
                  onChange={e =>
                    setNewSizeQuantity(parseInt(e.target.value) || 0)
                  }
                  className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                />
              </div>
            </div>

            <div className="mt-6 flex justify-end space-x-3">
              <button
                onClick={handleClose}
                className="rounded-md bg-gray-200 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-300"
              >
                Cancel
              </button>
              <button
                onClick={handleSubmit}
                disabled={!newSize}
                className="rounded-md bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700 disabled:bg-gray-400"
              >
                Add Size
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
