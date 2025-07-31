import { useMutation, useQuery } from '@tanstack/react-query';
import { createOrder, getMyOrders, getOrderById, cancelOrder } from '../../api/orders';
import { StandardResponse } from '../../types/common';
import { 
  CreateOrderRequest,
  OrderResponse,
  OrdersPageResponse,
  Order
} from '../../types/order';

export const useCreateOrder = () => {
  return useMutation<OrderResponse, Error, CreateOrderRequest>({
    mutationFn: createOrder,
    onSuccess: (data) => {
      console.log('Order created successfully:', data);
    },
    onError: (error) => {
      console.error('Failed to create order:', error);
    },
  });
};

export const useMyOrders = (page = 0, size = 20) => {
  return useQuery<OrdersPageResponse>({
    queryKey: ['orders', 'my-orders', page, size],
    queryFn: () => getMyOrders(page, size),
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
};

export const useOrderById = (orderId: number) => {
  return useQuery<StandardResponse<Order>>({
    queryKey: ['orders', orderId],
    queryFn: () => getOrderById(orderId),
    enabled: !!orderId,
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
};

export const useCancelOrder = () => {
  return useMutation<StandardResponse<Order>, Error, number>({
    mutationFn: cancelOrder,
    onSuccess: (data) => {
      console.log('Order cancelled successfully:', data);
    },
    onError: (error) => {
      console.error('Failed to cancel order:', error);
    },
  });
};
