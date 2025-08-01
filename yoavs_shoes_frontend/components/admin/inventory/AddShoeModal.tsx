interface AddShoeModalProps {
  isOpen: boolean;
  onClose: () => void;
  onAddShoe: (shoeData: any) => void;
}

export default function AddShoeModal({ isOpen, onClose, onAddShoe }: AddShoeModalProps) {
  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const formData = new FormData(e.target as HTMLFormElement);
    const shoeData = {
      name: formData.get('name'),
      basePrice: formData.get('basePrice'),
      gender: formData.get('gender'),
      brandId: formData.get('brandId'),
      brandName: formData.get('brandName'),
      categoryId: formData.get('categoryId'),
      categoryName: formData.get('categoryName')
    };
    onAddShoe(shoeData);
  };

  return (
    <div className="fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 w-full max-w-md">
        <h3 className="text-lg font-medium text-gray-900 mb-4">
          Add New Shoe
        </h3>
        
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Shoe Name</label>
            <input
              name="name"
              type="text"
              required
              className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="e.g., Air Max 90"
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Base Price</label>
            <input
              name="basePrice"
              type="number"
              min="0"
              step="0.01"
              required
              className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Gender</label>
              <select
                name="gender"
                required
                className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">Select Gender</option>
                <option value="Men">Men</option>
                <option value="Women">Women</option>
                <option value="Unisex">Unisex</option>
                <option value="Kids">Kids</option>
              </select>
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Brand</label>
              <select
                name="brandId"
                required
                onChange={(e) => {
                  const selectedOption = e.target.options[e.target.selectedIndex];
                  const brandNameInput = document.querySelector('input[name="brandName"]') as HTMLInputElement;
                  if (brandNameInput) {
                    brandNameInput.value = selectedOption.text;
                  }
                }}
                className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">Select Brand</option>
                <option value="1">Nike</option>
                <option value="2">Adidas</option>
                <option value="3">Puma</option>
                <option value="4">New Balance</option>
                <option value="5">Converse</option>
              </select>
              <input name="brandName" type="hidden" />
            </div>
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Category</label>
            <select
              name="categoryId"
              required
              onChange={(e) => {
                const selectedOption = e.target.options[e.target.selectedIndex];
                const categoryNameInput = document.querySelector('input[name="categoryName"]') as HTMLInputElement;
                if (categoryNameInput) {
                  categoryNameInput.value = selectedOption.text;
                }
              }}
              className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Select Category</option>
              <option value="1">Running</option>
              <option value="2">Casual</option>
              <option value="3">Sports</option>
              <option value="4">Formal</option>
              <option value="5">Basketball</option>
            </select>
            <input name="categoryName" type="hidden" />
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
              className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700"
            >
              Add Shoe
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
