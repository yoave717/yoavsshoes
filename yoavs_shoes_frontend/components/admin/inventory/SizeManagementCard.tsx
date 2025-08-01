import { ShoeInventory } from '@types';

interface SizeManagementCardProps {
  size: ShoeInventory;
  onStockUpdate: (modelId: number, sizeId: number, newQuantity: number) => void;
}

export default function SizeManagementCard({ 
  size, 
  onStockUpdate, 
}: SizeManagementCardProps) {
  return (
    <div className="bg-white p-2 rounded border text-xs">
      <div className="flex justify-between items-center mb-1">
        <span className="font-medium">Size {size.size}</span>
      </div>
      <input
        type="number"
        min="0"
        value={size.quantityAvailable}
        onChange={(e) => onStockUpdate(size.shoeModelId, size.id, parseInt(e.target.value) || 0)}
        className="w-full border border-gray-300 rounded px-1 py-0.5 text-xs"
      />
      <div className="text-xs text-gray-500 mt-1">
        Res: {size.quantityReserved} | Avail: {size.actualAvailableQuantity}
      </div>
    </div>
  );
}
