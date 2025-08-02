'use client';

import Link from 'next/link';
import Image from 'next/image';
import { Order, OrderItem, OrderStatus } from '@types';

const ORDER_STATUS_CONFIG = {
  PENDING: { label: 'Pending', color: 'bg-yellow-100 text-yellow-800', badgeColor: 'bg-yellow-500' },
  CONFIRMED: { label: 'Confirmed', color: 'bg-blue-100 text-blue-800', badgeColor: 'bg-blue-500' },
  PROCESSING: { label: 'Processing', color: 'bg-purple-100 text-purple-800', badgeColor: 'bg-purple-500' },
  SHIPPED: { label: 'Shipped', color: 'bg-indigo-100 text-indigo-800', badgeColor: 'bg-indigo-500' },
  DELIVERED: { label: 'Delivered', color: 'bg-green-100 text-green-800', badgeColor: 'bg-green-500' },
  CANCELLED: { label: 'Cancelled', color: 'bg-red-100 text-red-800', badgeColor: 'bg-red-500' },
};

interface OrderCardProps {
  order: Order;
  onStatusChange: (orderId: number, newStatus: OrderStatus) => void;
  onProcessOrder: (orderId: number) => void;
  isUpdatingStatus: boolean;
  isProcessing: boolean;
}

export default function OrderCard({
  order,
  onStatusChange,
  onProcessOrder,
  isUpdatingStatus,
  isProcessing
}: OrderCardProps) {
  return (
    <div className="p-6">
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
              onClick={() => onProcessOrder(order.id)}
              disabled={isProcessing}
              className="bg-green-600 text-white px-3 py-1 rounded text-sm font-medium hover:bg-green-700 disabled:opacity-50"
            >
              {isProcessing ? 'Processing...' : 'Process Order'}
            </button>
          )}

          {/* Status Change Dropdown */}
          <select
            value={order.status}
            onChange={(e) => onStatusChange(order.id, e.target.value as OrderStatus)}
            disabled={isUpdatingStatus}
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
  );
}
