import { ShoeInventoryView } from '@/lib/types';
import { ExtendedShoeModel } from '@/lib/types/product';

interface AddModelModalProps {
  isOpen: boolean;
  editingModel: ExtendedShoeModel | null;
  selectedShoe: ShoeInventoryView | null;
  shoes: ShoeInventoryView[];
  onClose: () => void;
  onSelectShoe: (shoe: ShoeInventoryView | null) => void;
  onSave: (modelData: any) => void;
}

export default function AddModelModal({
  isOpen,
  editingModel,
  selectedShoe,
  shoes,
  onClose,
  onSelectShoe,
  onSave
}: AddModelModalProps) {
  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const formData = new FormData(e.target as HTMLFormElement);
    const modelData = {
      shoeId: selectedShoe?.id,
      modelName: formData.get('modelName'),
      color: formData.get('color'),
      material: formData.get('material'),
      sku: formData.get('sku'),
      price: formData.get('price'),
      imageUrl: formData.get('imageUrl'),
      isActive: formData.get('isActive') === 'on'
    };
    onSave(modelData);
  };

  return (
  <div className="fixed inset-0 bg-gray-600/60 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 w-full max-w-md">
        <h3 className="text-lg font-medium text-gray-900 mb-4">
          {editingModel ? 'Edit Shoe Model' : 'Add New Shoe Model'}
        </h3>
        
        <form onSubmit={handleSubmit} className="space-y-4">
          {!editingModel && (
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Select Shoe</label>
              <select
                name="shoeId"
                value={selectedShoe?.id || ''}
                onChange={(e) => {
                  const shoe = shoes.find(s => s.id === parseInt(e.target.value));
                  onSelectShoe(shoe || null);
                }}
                required
                className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
              >
                <option value="">Select a shoe</option>
                {shoes.map(shoe => (
                  <option key={shoe.id} value={shoe.id}>
                    {shoe.brand.name} {shoe.name}
                  </option>
                ))}
              </select>
            </div>
          )}
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Model Name</label>
            <input
              name="modelName"
              type="text"
              required
              className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
              defaultValue={editingModel?.modelName || ''}
              placeholder="e.g., Air Max 90 White"
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Color</label>
            <input
              name="color"
              type="text"
              required
              className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
              defaultValue={editingModel?.color || ''}
              placeholder="e.g., White"
            />
          </div>
          
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Material</label>
              <input
                name="material"
                type="text"
                className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                defaultValue={editingModel?.material || ''}
                placeholder="e.g., Leather"
              />
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">SKU</label>
              <input
                name="sku"
                type="text"
                required
                className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                defaultValue={editingModel?.sku || ''}
                placeholder="e.g., AM90-WHT-001"
              />
            </div>
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Price</label>
            <input
              name="price"
              type="number"
              min="0"
              step="0.01"
              required
              className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
              defaultValue={editingModel?.price || ''}
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Image URL</label>
            <input
              name="imageUrl"
              type="text"
              className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
              defaultValue={editingModel?.imageUrl || ''}
              placeholder="https://example.com/image.jpg"
            />
          </div>
          
          <div className="flex items-center">
            <input
              name="isActive"
              type="checkbox"
              defaultChecked={editingModel?.isActive ?? true}
              className="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
            />
            <label className="ml-2 block text-sm text-gray-900">
              Active
            </label>
          </div>
          
          <div className="mt-6 flex justify-end space-x-3">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-200 rounded-md hover:bg-gray-300"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 rounded-md hover:bg-indigo-700"
            >
              {editingModel ? 'Update' : 'Add'} Model
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
