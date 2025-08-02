import { ShoeInventoryView, ExtendedShoeModel } from '@types';
import { useModal } from '@contexts';
import { useCreateShoeModel } from '@hooks';

interface AddModelModalProps {
  selectedShoe?: ShoeInventoryView;
  shoes: ShoeInventoryView[];
}

interface EditModalProps {
  editingModel: ExtendedShoeModel;
  selectedShoe: ShoeInventoryView;
}

type ModelDetailsModalProps = EditModalProps | AddModelModalProps;

function isEditMode(
  props: ModelDetailsModalProps
): props is EditModalProps {
  return 'editingModel' in props;
}

export default function ModelDetailsModal(props: ModelDetailsModalProps ) {
  const { onClose } = useModal();
  const { mutate: createShoeModel } = useCreateShoeModel();

  const isEdit = isEditMode(props);
  const editingModel = isEdit ? props.editingModel : undefined;
  const selectedShoe = isEdit ? props.selectedShoe : props.selectedShoe;
  const shoes = isEdit ? [] : props.shoes;

  
  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    
    if (!selectedShoe && !editingModel) {
      alert('Please select a shoe');
      return;
    }
    
    const formData = new FormData(e.target as HTMLFormElement);
    const modelData = {
      shoeId: isEdit ? props.editingModel.shoeId : selectedShoe!.id,
      modelName: formData.get('modelName') as string,
      color: formData.get('color') as string,
      material: formData.get('material') as string,
      sku: formData.get('sku') as string,
      price: parseFloat(formData.get('price') as string),
      imageUrl: formData.get('imageUrl') as string,
      isActive: formData.get('isActive') === 'on'
    };

    createShoeModel(modelData);
  };

  return (
      <div className="bg-white rounded-lg p-6 w-full max-w-md">
        <h3 className="text-lg font-medium text-gray-900 mb-4">
          {isEdit ? 'Edit Shoe Model' : 'Add New Shoe Model'}
        </h3>
        
        <form onSubmit={handleSubmit} className="space-y-4">
          {!isEdit && (
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Select Shoe</label>
              <select
                name="shoeId"
                value={selectedShoe?.id || ''}
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
  );
}
