import { useState } from 'react';
import { ShoeInventory, ShoeModelInventoryView } from '@types';
import { useUpdateShoeInventory } from '@hooks';

interface SizeManagementCardProps {
  size: ShoeInventory;
  model: ShoeModelInventoryView;
}

export default function SizeManagementCard({
  size,
  model,
}: SizeManagementCardProps) {
  const { mutate: updateInventory } = useUpdateShoeInventory();

  const handleStockUpdate = () => {
    updateInventory({
      modelId: model.id,
      shoeId: model.shoeId,
      inventoryId: size.id,
      quantityAvailable: quantity,
    });
    setHasChanges(false);
  };

  const [quantity, setQuantity] = useState(size.quantityAvailable);
  const [hasChanges, setHasChanges] = useState(false);

  const handleQuantityChange = (newQuantity: number) => {
    setQuantity(newQuantity);
    setHasChanges(newQuantity !== size.quantityAvailable);
  };

  const handleReset = () => {
    setQuantity(size.quantityAvailable);
    setHasChanges(false);
  };

  return (
    <div className="rounded border bg-white p-2 text-xs">
      <div className="mb-1 flex items-center justify-between">
        <span className="font-medium">Size {size.size}</span>
      </div>
      <input
        type="number"
        min="0"
        value={quantity}
        onChange={e => handleQuantityChange(parseInt(e.target.value) || 0)}
        className="w-full rounded border border-gray-300 px-1 py-0.5 text-xs"
      />
      <div className="mt-1 text-xs text-gray-500">
        Res: {size.quantityReserved} | Avail: {size.actualAvailableQuantity}
      </div>

      <div className="mt-2 flex gap-1">
        <button
          disabled={!hasChanges}
          onClick={handleStockUpdate}
          className="flex-1 rounded px-2 py-1 text-xs enabled:bg-blue-500 enabled:text-white enabled:hover:bg-blue-600 disabled:cursor-not-allowed disabled:bg-gray-200 disabled:text-gray-400 disabled:opacity-50"
        >
          Save
        </button>
        <button
          disabled={!hasChanges}
          onClick={handleReset}
          className="flex-1 rounded px-2 py-1 text-xs enabled:bg-gray-300 enabled:text-gray-700 enabled:hover:bg-gray-400 disabled:cursor-not-allowed disabled:bg-gray-100 disabled:text-gray-400 disabled:opacity-50"
        >
          Reset
        </button>
      </div>
    </div>
  );
}
