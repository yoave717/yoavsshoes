'use client';

import Image from 'next/image';
import { useState } from 'react';
import { Heart, ShoppingCart, Eye } from 'lucide-react';
import { useCartActions } from '@hooks';
import { ShoeModel } from '@/lib/types';

interface ProductCardProps {
  product: ShoeModel;
  onQuickView?: (product: ShoeModel) => void;
  onAddToCart?: (product: ShoeModel, size?: string) => void;
  onToggleWishlist?: (product: ShoeModel) => void;
  isInWishlist?: boolean;
}

export default function ProductCard({
  product,
  onQuickView,
  onAddToCart,
  onToggleWishlist,
  isInWishlist = false,
}: ProductCardProps) {
  const [selectedSize, setSelectedSize] = useState<string>('');
  const [imageError, setImageError] = useState(false);
  const cartActions = useCartActions();

  // Calculate stock status based on available sizes
  const isInStock = product.availableSizes && product.availableSizes.length > 0;

  const handleAddToCart = () => {
    if (onAddToCart) {
      onAddToCart(product, selectedSize);
    } else {
      // Default cart behavior
      try {
        cartActions.addProductToCart(product, selectedSize);
        alert(`Added ${product.modelName} to cart!`);
      } catch (error) {
        // Handle error (e.g., no size selected)
        const errorMessage = error instanceof Error ? error.message : 'Please select a size';
        alert(errorMessage);
      }
    }
  };

  const handleQuickView = () => {
    if (onQuickView) {
      onQuickView(product);
    }
  };

  const handleToggleWishlist = () => {
    if (onToggleWishlist) {
      onToggleWishlist(product);
    }
  };

  return (
    <div className="group relative bg-white rounded-lg shadow-md overflow-hidden hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1">
      {/* Product Image */}
      <div className="relative aspect-square overflow-hidden bg-gray-100">
        {product.imageUrl && !imageError ? (
          <Image
            src={product.imageUrl}
            alt={product.modelName}
            fill
            className="object-cover group-hover:scale-105 transition-transform duration-300"
            onError={() => setImageError(true)}
            sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
          />
        ) : (
          <div className="w-full h-full flex items-center justify-center bg-gray-200">
            <div className="text-gray-400 text-center">
              <div className="w-16 h-16 mx-auto mb-2 bg-gray-300 rounded-full flex items-center justify-center">
                <ShoppingCart className="w-8 h-8" />
              </div>
              <span className="text-sm">No Image</span>
            </div>
          </div>
        )}

        {/* Overlay Actions */}
        <div className="absolute inset-0 bg-black/0 group-hover:bg-opacity-20 transition-all duration-300" />
        
        {/* Quick Actions */}
        <div className="absolute top-4 right-4 flex flex-col gap-2 opacity-0 group-hover:opacity-100 transition-opacity duration-300">
          <button
            onClick={handleToggleWishlist}
            className={`p-2 rounded-full backdrop-blur-sm transition-colors ${
              isInWishlist
                ? 'bg-red-500 text-white'
                : 'bg-white/80 text-gray-700 hover:bg-red-500 hover:text-white'
            }`}
            title={isInWishlist ? 'Remove from wishlist' : 'Add to wishlist'}
          >
            <Heart className={`w-4 h-4 ${isInWishlist ? 'fill-current' : ''}`} />
          </button>
          
          <button
            onClick={handleQuickView}
            className="p-2 rounded-full bg-white/80 text-gray-700 hover:bg-blue-500 hover:text-white backdrop-blur-sm transition-colors"
            title="Quick view"
          >
            <Eye className="w-4 h-4" />
          </button>
        </div>

        {/* Stock Status Badge */}
        {!isInStock && (
          <div className="absolute top-4 left-4">
            <span className="bg-red-500 text-white text-xs font-semibold px-2 py-1 rounded">
              Out of Stock
            </span>
          </div>
        )}
      </div>

      {/* Product Info */}
      <div className="p-4">
        {/* Brand */}
        <div className="text-sm text-gray-500 mb-1">{product.shoe?.brand.name}</div>
        
        {/* Product Name */}
        <h3 className="font-semibold text-gray-900 mb-2 line-clamp-2 leading-tight">
          {product.modelName}
        </h3>

        {/* Category & Color */}
        <div className="flex items-center gap-2 mb-3">
          <span className="text-xs bg-gray-100 text-gray-600 px-2 py-1 rounded-full">
            {product.shoe.category.name}
          </span>
          <span className="text-xs text-gray-500">•</span>
          <span className="text-xs text-gray-600">{product.color}</span>
        </div>

        {/* Size Selection */}
        {product.availableSizes && product.availableSizes.length > 0 && (
          <div className="mb-3">
            <div className="text-xs text-gray-500 mb-2">Size:</div>
            <div className="flex flex-wrap gap-1">
              {product.availableSizes.slice(0, 4).map(({size}) => (
                <button
                  key={size}
                  onClick={() => setSelectedSize(size)}
                  className={`text-xs px-2 py-1 border rounded transition-colors ${
                    selectedSize === size
                      ? 'border-blue-500 bg-blue-50 text-blue-700'
                      : 'border-gray-200 text-gray-600 hover:border-gray-300'
                  }`}
                >
                  {size}
                </button>
              ))}
              {product.availableSizes.length > 4 && (
                <span className="text-xs text-gray-400 px-2 py-1">
                  +{product.availableSizes.length - 4} more
                </span>
              )}
            </div>
          </div>
        )}

        {/* Price and Add to Cart */}
        <div className="flex items-center justify-between">
          <div className="text-lg font-bold text-gray-900">
            ${product.price.toFixed(2)}
          </div>
          
          <button
            onClick={handleAddToCart}
            disabled={!isInStock}
            className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
              isInStock
                ? 'bg-blue-600 text-white hover:bg-blue-700 active:bg-blue-800'
                : 'bg-gray-200 text-gray-400 cursor-not-allowed'
            }`}
          >
            {isInStock ? 'Add to Cart' : 'Out of Stock'}
          </button>
        </div>
      </div>
    </div>
  );
}
