import { ShoppingCart } from "lucide-react";

export const ImageError = () => (
  <div className="w-full h-full flex items-center justify-center bg-gray-200">
    <div className="text-gray-400 text-center">
      <div className="w-16 h-16 mx-auto mb-2 bg-gray-300 rounded-full flex items-center justify-center">
        <ShoppingCart className="w-8 h-8" />
      </div>
      <span className="text-sm">No Image</span>
    </div>
  </div>
);

