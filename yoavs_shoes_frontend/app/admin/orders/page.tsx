'use client';

import { useState } from 'react';
import Link from 'next/link';
import Image from 'next/image';
import {
  useAllOrders,
  useOrdersByStatus,
  useUpdateOrderStatus,
  useProcessOrder
} from '../../../lib/hooks/orders';
import { OrderStatus, Order, OrderItem } from '../../../lib/types/order';

const ORDER_STATUS_CONFIG = {
  PENDING: { label: 'Pending', color: 'bg-yellow-100 text-yellow-800', badgeColor: 'bg-yellow-500' },
  CONFIRMED: { label: 'Confirmed', color: 'bg-blue-100 text-blue-800', badgeColor: 'bg-blue-500' },
  PROCESSING: { label: 'Processing', color: 'bg-purple-100 text-purple-800', badgeColor: 'bg-purple-500' },
  SHIPPED: { label: 'Shipped', color: 'bg-indigo-100 text-indigo-800', badgeColor: 'bg-indigo-500' },
  DELIVERED: { label: 'Delivered', color: 'bg-green-100 text-green-800', badgeColor: 'bg-green-500' },
  CANCELLED: { label: 'Cancelled', color: 'bg-red-100 text-red-800', badgeColor: 'bg-red-500' },
};

export default function AdminOrdersPage() {
  const [selectedStatus, setSelectedStatus] = useState<OrderStatus | 'ALL'>('ALL');
  const [page, setPage] = useState(0);
  const [size] = useState(20);

  const {
    data: allOrdersData,
    isLoading: allOrdersLoading,
    error: allOrdersError
  } = useAllOrders(page, size, 'id', 'desc');

  const {
    data: statusOrdersData,
    isLoading: statusOrdersLoading,
    error: statusOrdersError
  } = useOrdersByStatus(
    selectedStatus as OrderStatus,
    page,
    size,
    'id',
    'desc',
    { enabled: selectedStatus !== 'ALL' }
  );

  const updateOrderStatusMutation = useUpdateOrderStatus();
  const processOrderMutation = useProcessOrder();

  const ordersData = selectedStatus === 'ALL' ? allOrdersData : statusOrdersData;
  const isLoading = selectedStatus === 'ALL' ? allOrdersLoading : statusOrdersLoading;
  const error = selectedStatus === 'ALL' ? allOrdersError : statusOrdersError;

  const handleStatusChange = async (orderId: number, newStatus: OrderStatus) => {
    try {
      await updateOrderStatusMutation.mutateAsync({ orderId, status: newStatus });
    } catch (error) {
      console.error('Failed to update order status:', error);
    }
  };

  const handleProcessOrder = async (orderId: number) => {
    try {
      await processOrderMutation.mutateAsync(orderId);
    } catch (error) {
      console.error('Failed to process order:', error);
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="text-center">
          <div className="mx-auto h-12 w-12 animate-spin rounded-full border-b-2 border-indigo-600"></div>
          <p className="mt-4 text-gray-600">Loading orders...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center py-12">
        <p className="text-red-600">Failed to load orders. Please try again.</p>
      </div>
    );
  }

  const orders = ordersData?.data?.content || [];
  const totalPages = ordersData?.data?.totalPages || 0;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Order Management</h1>
          <p className="mt-1 text-gray-600">
            Manage customer orders, update statuses, and process shipments
          </p>
        </div>
        <Link
          href="/admin"
          className="bg-gray-600 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-gray-700"
        >
          Back to Dashboard
        </Link>
      </div>

      {/* Status Filter */}
      <div className="bg-white shadow rounded-lg p-6">
        <h3 className="text-lg font-medium text-gray-900 mb-4">Filter by Status</h3>
        <div className="flex flex-wrap gap-2">
          <button
            onClick={() => setSelectedStatus('ALL')}
            className={`px-3 py-1 rounded-full text-sm font-medium ${
              selectedStatus === 'ALL'
                ? 'bg-gray-900 text-white'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            }`}
          >
            All Orders
          </button>
          {Object.entries(ORDER_STATUS_CONFIG).map(([status, config]) => (
            <button
              key={status}
              onClick={() => setSelectedStatus(status as OrderStatus)}
              className={`px-3 py-1 rounded-full text-sm font-medium ${
                selectedStatus === status
                  ? config.color
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            >
              {config.label}
            </button>
          ))}
        </div>
      </div>

      {/* Orders List */}
      <div className="bg-white shadow rounded-lg overflow-hidden">
        <div className="px-6 py-4 border-b border-gray-200">
          <h3 className="text-lg font-medium text-gray-900">
            Orders {selectedStatus !== 'ALL' && `(${ORDER_STATUS_CONFIG[selectedStatus as keyof typeof ORDER_STATUS_CONFIG]?.label})`}
          </h3>
        </div>

        {orders.length === 0 ? (
          <div className="text-center py-12">
            <p className="text-gray-500">No orders found</p>
          </div>
        ) : (
          <div className="divide-y divide-gray-200">
            {orders.map((order: Order) => (
              <div key={order.id} className="p-6">
                <div className="flex items-center justify-between mb-4">
                  <div className="flex items-center space-x-4">
                    <div>
                      <h4 className="text-lg font-medium text-gray-900">
                        Order #{order.orderNumber}
                      </h4>
                      <p className="text-sm text-gray-600">
                        {new Date(order.orderDate).toLocaleDateString('en-US', {
                          year: 'numeric',
                          month: 'long',
                          day: 'numeric',
                          hour: '2-digit',
                          minute: '2-digit'
                        })}
                      </p>
                    </div>
                  </div>
                  <div className="flex items-center space-x-4">
                    <span className={`inline-flex items-center px-3 py-1 rounded-full text-sm font-medium ${ORDER_STATUS_CONFIG[order.status as keyof typeof ORDER_STATUS_CONFIG]?.color}`}>
                      {ORDER_STATUS_CONFIG[order.status as keyof typeof ORDER_STATUS_CONFIG]?.label}
                    </span>
                    <span className="text-lg font-bold text-gray-900">
                      ${order.totalAmount.toFixed(2)}
                    </span>
                  </div>
                </div>

                {/* Customer Info */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                  <div>
                    <h5 className="text-sm font-medium text-gray-900 mb-1">Customer</h5>
                    <p className="text-sm text-gray-600">
                      {order.user?.firstName} {order.user?.lastName}
                    </p>
                    <p className="text-sm text-gray-600">{order.user?.email}</p>
                  </div>
                  <div>
                    <h5 className="text-sm font-medium text-gray-900 mb-1">Shipping Address</h5>
                    <p className="text-sm text-gray-600">
                      {order.shippingAddress?.addressLine1}
                      {order.shippingAddress?.addressLine2 && (
                        <>, {order.shippingAddress.addressLine2}</>
                      )}
                    </p>
                    <p className="text-sm text-gray-600">
                      {order.shippingAddress?.city}, {order.shippingAddress?.state} {order.shippingAddress?.postalCode}
                    </p>
                  </div>
                </div>

                {/* Order Items */}
                <div className="mb-4">
                  <h5 className="text-sm font-medium text-gray-900 mb-2">Items ({order.orderItems?.length || 0})</h5>
                  <div className="space-y-2">
                    {order.orderItems?.slice(0, 3).map((item: OrderItem, index: number) => (
                      <div key={item.id || index} className="flex items-center space-x-3">
                        {item.shoeModel?.imageUrl && (
                          <Image
                            src={item.shoeModel.imageUrl}
                            alt={item.shoeModel.modelName}
                            width={40}
                            height={40}
                            className="w-10 h-10 object-cover rounded"
                          />
                        )}
                        <div className="flex-1">
                          <p className="text-sm font-medium text-gray-900">
                            {item.shoeModel?.shoe?.name} - {item.shoeModel?.modelName}
                          </p>
                          <p className="text-sm text-gray-600">
                            Size: {item.size}, Qty: {item.quantity} × ${item.unitPrice?.toFixed(2)}
                          </p>
                        </div>
                      </div>
                    ))}
                    {(order.orderItems?.length || 0) > 3 && (
                      <p className="text-sm text-gray-500">
                        +{(order.orderItems?.length || 0) - 3} more items
                      </p>
                    )}
                  </div>
                </div>

                {/* Actions */}
                <div className="flex items-center justify-between pt-4 border-t border-gray-200">
                  <div className="flex items-center space-x-2">
                    <Link
                      href={`/orders/${order.id}`}
                      className="text-indigo-600 hover:text-indigo-900 text-sm font-medium"
                    >
                      View Details
                    </Link>
                  </div>
                  <div className="flex items-center space-x-3">
                    {/* Process Order Button */}
                    {order.status === 'PENDING' && (
                      <button
                        onClick={() => handleProcessOrder(order.id)}
                        disabled={processOrderMutation.isPending}
                        className="bg-green-600 text-white px-3 py-1 rounded text-sm font-medium hover:bg-green-700 disabled:opacity-50"
                      >
                        {processOrderMutation.isPending ? 'Processing...' : 'Process Order'}
                      </button>
                    )}

                    {/* Status Change Dropdown */}
                    <select
                      value={order.status}
                      onChange={(e) => handleStatusChange(order.id, e.target.value as OrderStatus)}
                      disabled={updateOrderStatusMutation.isPending}
                      className="border border-gray-300 rounded-md px-3 py-1 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                    >
                      {Object.entries(ORDER_STATUS_CONFIG).map(([status, config]) => (
                        <option key={status} value={status}>
                          {config.label}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="bg-white px-4 py-3 border-t border-gray-200 sm:px-6">
            <div className="flex items-center justify-between">
              <button
                onClick={() => setPage(Math.max(0, page - 1))}
                disabled={page === 0}
                className="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
              >
                Previous
              </button>
              <span className="text-sm text-gray-700">
                Page {page + 1} of {totalPages}
              </span>
              <button
                onClick={() => setPage(Math.min(totalPages - 1, page + 1))}
                disabled={page >= totalPages - 1}
                className="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
              >
                Next
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
