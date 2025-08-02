import { ShoeInventoryView, ExtendedShoeModel, UpdateShoeModelRequest } from '@types';
import { useModal } from '@contexts';
import { useCreateShoeModel, useUpdateShoeModel } from '@hooks';

interface AddModelModalProps {
  selectedShoe: ShoeInventoryView;
}

interface EditModalProps {
  editingModel: ExtendedShoeModel;
  selectedShoe: ShoeInventoryView;
}

type ModelDetailsModalProps = EditModalProps | AddModelModalProps;

function isEditMode(props: ModelDetailsModalProps): props is EditModalProps {
  return 'editingModel' in props;
}

export default function ModelDetailsModal(props: ModelDetailsModalProps) {
  const { onClose } = useModal();
  const { mutate: createShoeModel } = useCreateShoeModel();
  const { mutate: updateShoeModel } = useUpdateShoeModel();

  const isEdit = isEditMode(props);
  const editingModel = isEdit ? props.editingModel : undefined;
  const selectedShoe = isEdit ? props.selectedShoe : props.selectedShoe;

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (!selectedShoe && !editingModel) {
      alert('Please select a shoe');
      return;
    }

    const formData = new FormData(e.target as HTMLFormElement);
    
    if (isEdit) {
      // For updates, only include changed fields
      const updates: UpdateShoeModelRequest = {};
      
      const newModelName = formData.get('modelName') as string;
      if (newModelName !== editingModel!.modelName) {
        updates.modelName = newModelName;
      }
      
      const newColor = formData.get('color') as string;
      if (newColor !== editingModel!.color) {
        updates.color = newColor;
      }
      
      const newMaterial = formData.get('material') as string;
      if (newMaterial !== (editingModel!.material || '')) {
        updates.material = newMaterial;
      }
      
      const newPrice = parseFloat(formData.get('price') as string);
      if (newPrice !== editingModel!.price) {
        updates.price = newPrice;
      }
      
      const newImageUrl = formData.get('imageUrl') as string;
      if (newImageUrl !== (editingModel!.imageUrl || '')) {
        updates.imageUrl = newImageUrl;
      }
      
      const newIsActive = formData.get('isActive') === 'on';
      if (newIsActive !== editingModel!.isActive) {
        updates.isActive = newIsActive;
      }
      
      // Only update if there are changes
      if (Object.keys(updates).length > 0) {
        updateShoeModel({ id: editingModel!.id, shoeModel: updates }, {
          onSettled: () => {
            onClose();
          }
        });
      } else {
        onClose();
      }
    } else {
      // For creates, include all data
      const modelData = {
        shoeId: selectedShoe!.id,
        modelName: formData.get('modelName') as string,
        color: formData.get('color') as string,
        material: formData.get('material') as string,
        sku: formData.get('sku') as string,
        price: parseFloat(formData.get('price') as string),
        imageUrl: formData.get('imageUrl') as string,
        isActive: formData.get('isActive') === 'on',
      };

      createShoeModel(modelData, {
        onSettled: () => {
          onClose();
        }
      });
    }
  };

  return (
    <div className="w-full max-w-md rounded-lg bg-white p-6">
      <h3 className="mb-4 text-lg font-medium text-gray-900">
        {isEdit ? 'Edit Shoe Model' : 'Add New Shoe Model'}
      </h3>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="mb-1 block text-sm font-medium text-gray-700">
            Shoe
          </label>
          <div className="w-full rounded-md border border-gray-200 bg-gray-50 px-3 py-2 text-sm">
            {selectedShoe.brand.name} {selectedShoe.name}
          </div>
        </div>

        <div>
          <label className="mb-1 block text-sm font-medium text-gray-700">
            Model Name
          </label>
          <input
            name="modelName"
            type="text"
            required
            className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:ring-2 focus:ring-indigo-500 focus:outline-none"
            defaultValue={editingModel?.modelName || ''}
            placeholder="e.g., Air Max 90 White"
          />
        </div>

        <div>
          <label className="mb-1 block text-sm font-medium text-gray-700">
            Color
          </label>
          <input
            name="color"
            type="text"
            required
            className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:ring-2 focus:ring-indigo-500 focus:outline-none"
            defaultValue={editingModel?.color || ''}
            placeholder="e.g., White"
          />
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">
              Material
            </label>
            <input
              name="material"
              type="text"
              className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:ring-2 focus:ring-indigo-500 focus:outline-none"
              defaultValue={editingModel?.material || ''}
              placeholder="e.g., Leather"
            />
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">
              SKU
            </label>
            <input
              name="sku"
              type="text"
              required
              className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:ring-2 focus:ring-indigo-500 focus:outline-none"
              defaultValue={editingModel?.sku || ''}
              placeholder="e.g., AM90-WHT-001"
            />
          </div>
        </div>

        <div>
          <label className="mb-1 block text-sm font-medium text-gray-700">
            Price
          </label>
          <input
            name="price"
            type="number"
            min="0"
            step="0.01"
            required
            className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:ring-2 focus:ring-indigo-500 focus:outline-none"
            defaultValue={editingModel?.price || ''}
          />
        </div>

        <div>
          <label className="mb-1 block text-sm font-medium text-gray-700">
            Image URL
          </label>
          <input
            name="imageUrl"
            type="text"
            className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:ring-2 focus:ring-indigo-500 focus:outline-none"
            defaultValue={editingModel?.imageUrl || ''}
            placeholder="https://example.com/image.jpg"
          />
        </div>

        <div className="flex items-center">
          <input
            name="isActive"
            type="checkbox"
            defaultChecked={editingModel?.isActive ?? true}
            className="h-4 w-4 rounded border-gray-300 text-indigo-600 focus:ring-indigo-500"
          />
          <label className="ml-2 block text-sm text-gray-900">Active</label>
        </div>

        <div className="mt-6 flex justify-end space-x-3">
          <button
            type="button"
            onClick={onClose}
            className="rounded-md bg-gray-200 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-300"
          >
            Cancel
          </button>
          <button
            type="submit"
            className="rounded-md bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700"
          >
            {editingModel ? 'Update' : 'Add'} Model
          </button>
        </div>
      </form>
    </div>
  );
}
