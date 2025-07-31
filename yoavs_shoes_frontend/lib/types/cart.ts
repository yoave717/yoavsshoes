export interface CartItem {
  id: number; // shoe model id
  modelName: string;
  brandName: string;
  color: string;
  size: string;
  price: number;
  quantity: number;
  imageUrl?: string;
  sku: string;
}

export interface CartState {
  items: CartItem[];
  totalItems: number;
  totalAmount: number;
}

export type CartAction =
  | { type: 'ADD_ITEM'; payload: Omit<CartItem, 'quantity'> }
  | { type: 'REMOVE_ITEM'; payload: { id: number; size: string } }
  | { type: 'UPDATE_QUANTITY'; payload: { id: number; size: string; quantity: number } }
  | { type: 'CLEAR_CART' }
  | { type: 'LOAD_CART'; payload: CartState };

export interface CartContextType extends CartState {
  addItem: (item: Omit<CartItem, 'quantity'>) => void;
  removeItem: (id: number, size: string) => void;
  updateQuantity: (id: number, size: string, quantity: number) => void;
  clearCart: () => void;
  getItemQuantity: (id: number, size: string) => number;
}
