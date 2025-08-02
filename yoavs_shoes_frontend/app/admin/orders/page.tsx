'use client';

import { useState, useMemo } from 'react';
import {
  useAllOrders,
  useOrdersByStatus,
} from '@hooks';
import { OrderStatus, Order } from '@types';
import OrderCard from '../../../components/admin/orders/OrderCard';

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

  const ordersData = selectedStatus === 'ALL' ? allOrdersData : statusOrdersData;
  const isLoading = selectedStatus === 'ALL' ? allOrdersLoading : statusOrdersLoading;
  const error = selectedStatus === 'ALL' ? allOrdersError : statusOrdersError;

  
  const orders = useMemo(() => ordersData?.content || [], [ordersData]);
  const totalPages = useMemo(() => ordersData?.totalPages || 0, [ordersData]);

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


  return (
    <div className="space-y-6">
      {/* Header */}
      <div >
          <h1 className="text-2xl font-bold text-gray-900">Order Management</h1>
          <p className="mt-1 text-gray-600">
            Manage customer orders, update statuses, and process shipments
          </p>
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
              <OrderCard
                key={order.id}
                order={order}
              />
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
                