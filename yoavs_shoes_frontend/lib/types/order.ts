import { ShoeModel } from './product';
import { OrderUser, ShippingAddress } from './user';
import { StandardResponse, PageResponse } from './common';

export interface OrderItem {
  id: number;
  shoeModelId?: number;
  size: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
  shoeModel: ShoeModel;
}

export interface CreateOrderItemRequest {
  shoeModelId: number;
  size: string;
  quantity: number;
}

export interface CreateOrderRequest {
  userId: number;
  items: CreateOrderItemRequest[];
  shippingAddressId: number;
  specialInstructions?: string;
}

export type OrderStatus = 'PENDING' | 'CONFIRMED' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';

export interface Order {
  id: number;
  orderNumber: string;
  status: OrderStatus;
  totalAmount: number;
  orderDate: string;
  shippedDate?: string;
  deliveredDate?: string;
  orderItems: OrderItem[];
  createdAt: string;
  updatedAt: string;
  user?: OrderUser;
  shippingAddress?: ShippingAddress;
}

// Typed API responses
export interface OrderResponse {
  success: boolean;
  data: Order;
  message?: string;
}

export type OrdersPageResponse = StandardResponse<PageResponse<Order>>;

export type OrderStatsResponse = StandardResponse<{
  totalOrders: number;
  pendingOrders: number;
  totalRevenue: number;
  averageOrderValue: number;
}>;
