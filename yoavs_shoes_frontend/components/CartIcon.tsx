'use client';

import { ShoppingCart } from 'lucide-react';
import { useCart } from '@contexts';

interface CartIconProps {
  onClick?: () => void;
  className?: string;
}

export default function CartIcon({ onClick, className = '' }: CartIconProps) {
  const { totalItems } = useCart();

  return (
    <button
      onClick={onClick}
      className={`relative p-2 text-gray-600 hover:text-gray-900 transition-colors ${className}`}
      title="Shopping Cart"
    >
      <ShoppingCart className="w-6 h-6" />
      {totalItems > 0 && (
        <span className="absolute -top-1 -right-1 bg-blue-600 text-white text-xs font-bold rounded-full h-5 w-5 flex items-center justify-center min-w-[20px]">
          {totalItems > 99 ? '99+' : totalItems}
        </span>
      )}
    </button>
  );
}
