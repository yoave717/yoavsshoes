// Utility functions for cart
export const formatCartTotal = (amount: number) => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(amount);
};

export const getCartSummary = (items: { price: number; quantity: number }[]) => {
  const itemCount = items.reduce((sum, item) => sum + item.quantity, 0);
  const subtotal = items.reduce((sum, item) => sum + item.price * item.quantity, 0);
  

  
  // Simple shipping calculation
  const shipping = subtotal > 100 ? 0 : 9.99;
  
  const total = subtotal + shipping;

  return {
    itemCount,
    subtotal,
    shipping,
    total,
  };
};
