'use client';

import { useRouter } from 'next/navigation';
import { X, Plus, Minus, ShoppingBag } from 'lucide-react';
import Image from 'next/image';
import { useCart } from '../lib/cart-context';
import { formatCartTotal, getCartSummary } from '@hooks';

interface CartDrawerProps {
  isOpen: boolean;
  onClose: () => void;
}

export default function CartDrawer({ isOpen, onClose }: CartDrawerProps) {
  const router = useRouter();
  const { items, updateQuantity, removeItem, clearCart } = useCart();
  const summary = getCartSummary(items);

  const handleQuantityChange = (id: number, size: string, newQuantity: number) => {
    if (newQuantity <= 0) {
      removeItem(id, size);
    } else {
      updateQuantity(id, size, newQuantity);
    }
  };

  const handleCheckout = () => {
    onClose();
    router.push('/checkout');
  };

  if (!isOpen) return null;

  return (
    <>
      {/* Backdrop */}
      <div
        className="fixed inset-0 bg-black/40 z-40"
        onClick={onClose}
      />

      {/* Drawer */}
      <div className="fixed right-0 top-0 h-full w-full max-w-md bg-white shadow-xl z-50 flex flex-col text-gray-600 ">
        {/* Header */}
        <div className="flex items-center justify-between p-4 border-b">
          <h2 className="text-lg font-semibold">Shopping Cart</h2>
          <button
            onClick={onClose}
            className="p-2 hover:bg-gray-100 rounded-full transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Cart Items */}
        <div className="flex-1 overflow-y-auto p-4">
          {items.length === 0 ? (
            <div className="flex flex-col items-center justify-center h-full text-gray-500">
              <ShoppingBag className="w-16 h-16 mb-4" />
              <p className="text-lg font-medium">Your cart is empty</p>
              <p className="text-sm">Add some shoes to get started!</p>
            </div>
          ) : (
            <div className="space-y-4">
              {items.map((item) => (
                <div key={`${item.id}-${item.size}`} className="flex gap-3 p-3 border rounded-lg">
                  {/* Product Image */}
                  <div className="relative w-16 h-16 flex-shrink-0 bg-gray-100 rounded overflow-hidden">
                    {item.imageUrl ? (
                      <Image
                        src={item.imageUrl}
                        alt={item.modelName}
                        fill
                        className="object-cover"
                        sizes="64px"
                      />
                    ) : (
                      <div className="w-full h-full flex items-center justify-center bg-gray-200">
                        <ShoppingBag className="w-6 h-6 text-gray-400" />
                      </div>
                    )}
                  </div>

                  {/* Product Info */}
                  <div className="flex-1 min-w-0">
                    <h3 className="font-medium text-sm text-gray-900 truncate">
                      {item.modelName}
                    </h3>
                    <p className="text-xs text-gray-500">{item.brandName}</p>
                    <p className="text-xs text-gray-500">
                      {item.color} • Size {item.size}
                    </p>
                    <p className="text-sm font-semibold text-gray-900 mt-1">
                      {formatCartTotal(item.price)}
                    </p>
                  </div>

                  {/* Quantity Controls */}
                  <div className="flex flex-col items-end gap-2">
                    <button
                      onClick={() => removeItem(item.id, item.size)}
                      className="text-xs text-red-600 hover:text-red-800"
                    >
                      Remove
                    </button>
                    <div className="flex items-center gap-1 text">
                      <button
                        onClick={() => handleQuantityChange(item.id, item.size, item.quantity - 1)}
                        className="p-1 hover:bg-gray-100 rounded"
                        disabled={item.quantity <= 1}
                      >
                        <Minus className="w-3 h-3" />
                      </button>
                      <span className="px-2 py-1 text-xs font-medium min-w-[2rem] text-center">
                        {item.quantity}
                      </span>
                      <button
                        onClick={() => handleQuantityChange(item.id, item.size, item.quantity + 1)}
                        className="p-1 hover:bg-gray-100 rounded"
                      >
                        <Plus className="w-3 h-3" />
                      </button>
                    </div>
                  </div>
                </div>
              ))}

              {/* Clear Cart Button */}
              {items.length > 0 && (
                <button
                  onClick={clearCart}
                  className="w-full text-sm text-red-600 hover:text-red-800 py-2"
                >
                  Clear Cart
                </button>
              )}
            </div>
          )}
        </div>

        {/* Footer with totals and checkout */}
        {items.length > 0 && (
          <div className="border-t p-4 space-y-3">
            {/* Summary */}
            <div className="space-y-1 text-sm">
              <div className="flex justify-between">
                <span>Subtotal:</span>
                <span>{formatCartTotal(summary.subtotal)}</span>
              </div>
              <div className="flex justify-between">
                <span>Shipping:</span>
                <span>{summary.shipping === 0 ? 'Free' : formatCartTotal(summary.shipping)}</span>
              </div>
              <div className="flex justify-between font-semibold text-base border-t pt-1">
                <span>Total:</span>
                <span>{formatCartTotal(summary.total)}</span>
              </div>
            </div>

            {/* Checkout Button */}
            <button
              onClick={handleCheckout}
              className="w-full bg-blue-600 text-white py-3 px-4 rounded-lg font-medium hover:bg-blue-700 transition-colors"
            >
              Checkout ({summary.itemCount} items)
            </button>
          </div>
        )}
      </div>
    </>
  );
}
