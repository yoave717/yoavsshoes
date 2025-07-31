import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  getAllOrders,
  getOrdersByStatus,
  updateOrderStatus,
  processOrder,
  getUserOrders,
  getOrderStats
} from '../../api/orders';
import { StandardResponse } from '../../types/common';
import { 
  OrderStatus, 
  OrderStatsResponse,
  Order
} from '../../types/order';

// Admin Order Management Hooks
export const useAllOrders = (page = 0, size = 20, sortBy = 'id', sortDir = 'desc') => {
  return useQuery({
    queryKey: ['orders', 'admin', 'all', page, size, sortBy, sortDir],
    queryFn: () => getAllOrders(page, size, sortBy, sortDir),
  });
};

export const useOrdersByStatus = (
  status: OrderStatus, 
  page = 0, 
  size = 20, 
  sortBy = 'id', 
  sortDir = 'desc', 
  options?: { enabled?: boolean }
) => {
  return useQuery({
    queryKey: ['orders', 'admin', 'status', status, page, size, sortBy, sortDir],
    queryFn: () => getOrdersByStatus(status, page, size, sortBy, sortDir),
    enabled: options?.enabled !== false,
  });
};

export const useUserOrders = (userId: number, page = 0, size = 20) => {
  return useQuery({
    queryKey: ['orders', 'admin', 'user', userId, page, size],
    queryFn: () => getUserOrders(userId, page, size),
    enabled: !!userId,
  });
};

export const useUpdateOrderStatus = () => {
  const queryClient = useQueryClient();
  
  return useMutation<StandardResponse<Order>, Error, { orderId: number; status: OrderStatus }>({
    mutationFn: ({ orderId, status }) => updateOrderStatus(orderId, status),
    onSuccess: () => {
      // Invalidate all order-related queries to refresh data
      queryClient.invalidateQueries({ queryKey: ['orders'] });
    },
  });
};

export const useProcessOrder = () => {
  const queryClient = useQueryClient();
  
  return useMutation<StandardResponse<Order>, Error, number>({
    mutationFn: (orderId: number) => processOrder(orderId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['orders'] });
    },
  });
};

export const useOrderStats = () => {
  return useQuery<OrderStatsResponse>({
    queryKey: ['orders', 'admin', 'stats'],
    queryFn: getOrderStats,
  });
};
