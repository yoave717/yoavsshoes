import apiClient from './client';
import { CartItem } from '../types/cart';
import { StandardResponse } from '../types/common';
import { 
  CreateOrderItemRequest, 
  CreateOrderRequest, 
  OrderResponse, 
  OrderStatus, 
  OrdersPageResponse,
  OrderStatsResponse,
  Order
} from '../types/order';

// Regular user order operations
export const createOrder = async (orderData: CreateOrderRequest) => {
  const response = await apiClient.post<OrderResponse>('/orders', orderData);
  return response.data;
};

export const getMyOrders = async (page = 0, size = 20): Promise<OrdersPageResponse> => {
  const response = await apiClient.get<OrdersPageResponse>(`/orders/my-orders?page=${page}&size=${size}`);
  return response.data;
};

export const getOrderById = async (orderId: number): Promise<StandardResponse<Order>> => {
  const response = await apiClient.get<StandardResponse<Order>>(`/orders/${orderId}`);
  return response.data;
};

export const cancelOrder = async (orderId: number): Promise<StandardResponse<Order>> => {
  const response = await apiClient.put<StandardResponse<Order>>(`/orders/${orderId}/cancel`);
  return response.data;
};

// Admin order operations
export const getAllOrders = async (page = 0, size = 20, sortBy = 'id', sortDir = 'desc') => {
  const response = await apiClient.get<OrdersPageResponse>(`/orders?page=${page}&size=${size}&sortBy=${sortBy}&sortDir=${sortDir}`);
  return response.data;
};

export const getOrdersByStatus = async (status: OrderStatus, page = 0, size = 20, sortBy = 'id', sortDir = 'desc'): Promise<OrdersPageResponse> => {
  const response = await apiClient.get<OrdersPageResponse>(`/orders/status/${status}?page=${page}&size=${size}&sortBy=${sortBy}&sortDir=${sortDir}`);
  return response.data;
};

export const updateOrderStatus = async (orderId: number, status: OrderStatus): Promise<StandardResponse<Order>> => {
  const response = await apiClient.put<StandardResponse<Order>>(`/orders/${orderId}/status?status=${status}`);
  return response.data;
};

export const processOrder = async (orderId: number): Promise<StandardResponse<Order>> => {
  const response = await apiClient.put<StandardResponse<Order>>(`/orders/${orderId}/process`);
  return response.data;
};

export const getUserOrders = async (userId: number, page = 0, size = 20): Promise<OrdersPageResponse> => {
  const response = await apiClient.get<OrdersPageResponse>(`/orders/user/${userId}?page=${page}&size=${size}`);
  return response.data;
};

export const getOrderStats = async (): Promise<OrderStatsResponse> => {
  const response = await apiClient.get<OrderStatsResponse>('/orders/stats');
  return response.data;
};

// Helper function to convert cart items to order items
export const convertCartItemsToOrderItems = (cartItems: CartItem[]): CreateOrderItemRequest[] => {
  return cartItems.map(item => ({
    shoeModelId: item.id,
    size: item.size,
    quantity: item.quantity
  }));
};
