import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  getAllOrders,
  getOrdersByStatus,
  updateOrderStatus,
  processOrder,
  getUserOrders,
  getOrderStats
} from '../../api/orders';
import { 
  OrderStatus, 
  OrderStatsResponse,
  Order,
  OrdersPageResponse
} from '../../types/order';
import { useToast } from '@/components/Toast';

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
  const {showToast} = useToast();

  return useMutation<Order, Error, { orderId: number; status: OrderStatus }>({
    mutationFn: ({ orderId, status }) => updateOrderStatus(orderId, status),
    onSuccess: (data, variables) => {
      showToast('Order status updated successfully');
      // Invalidate all order-related queries to refresh data

      const queries = queryClient.getQueriesData<
              OrdersPageResponse
            >({ queryKey: ['orders', 'admin'] });
            queries.forEach(([queryKey, oldData]) => {
              if (!oldData) return;

              queryClient.setQueryData<OrdersPageResponse>(queryKey, {
                ...oldData,
                content: oldData.content.map(order => {
                  if (order.id === variables.orderId) {
                    return {
                      ...order,
                      status: variables.status,
                    };
                  }
                  return order;
                }),
              });
            });

      queryClient.invalidateQueries({ queryKey: ['orders'] });
    },
    onError: (error) => {
      console.log('Error updating order status:', error);
      
      showToast(`Error updating order status: ${error.message}`, 'error');
    },
  });
};

export const useProcessOrder = () => {
  const queryClient = useQueryClient();
  const { showToast } = useToast();

  return useMutation<Order, Error, number>({
    mutationFn: (orderId: number) => processOrder(orderId),
    onSuccess: () => {
      showToast('Order status updated successfully');
      // Invalidate all order-related queries to refresh data
      queryClient.invalidateQueries({ queryKey: ['orders'] });
    },
    onError: (error) => {
      console.log('Error updating order status:', error);
      
      showToast(`Error updating order status: ${error.message}`, 'error');
    },
  });
};

export const useOrderStats = () => {
  return useQuery<OrderStatsResponse>({
    queryKey: ['orders', 'admin', 'stats'],
    queryFn: getOrderStats,
  });
};
