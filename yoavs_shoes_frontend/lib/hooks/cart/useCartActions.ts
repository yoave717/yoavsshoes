'use client';

import { ShoeModel } from '@/lib/types';
import { useCart } from '../../cart-context';

export const useCartActions = () => {
  const cart = useCart();

  const addProductToCart = (product: ShoeModel, size?: string) => {
    if (!size) {
      // If no size selected, use the first available size
      size = product.availableSizes?.[0].size;
    }

    if (!size) {
      throw new Error('Please select a size');
    }

    const cartItem = {
      id: product.id,
      modelName: product.modelName,
      brandName: product.shoe.brand.name,
      color: product.color,
      size: size,
      price: product.price,
      imageUrl: product.imageUrl,
      sku: `${product.shoe.brand.name}-${product.id}-${size}`, // Generate SKU if not available
    };

    cart.addItem(cartItem);
  };

  const removeProductFromCart = (productId: number, size: string) => {
    cart.removeItem(productId, size);
  };

  const updateProductQuantity = (productId: number, size: string, quantity: number) => {
    cart.updateQuantity(productId, size, quantity);
  };

  const isProductInCart = (productId: number, size: string) => {
    return cart.getItemQuantity(productId, size) > 0;
  };

  const getProductQuantity = (productId: number, size: string) => {
    return cart.getItemQuantity(productId, size);
  };

  return {
    ...cart,
    addProductToCart,
    removeProductFromCart,
    updateProductQuantity,
    isProductInCart,
    getProductQuantity,
  };
}
