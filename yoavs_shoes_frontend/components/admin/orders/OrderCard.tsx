'use client';

import Link from 'next/link';
import Image from 'next/image';
import { Order, OrderItem, OrderStatus } from '@types';
import { useUpdateOrderStatus } from '@hooks';

const ORDER_STATUS_CONFIG = {
  PENDING: {
    label: 'Pending',
    color: 'bg-yellow-100 text-yellow-800',
    badgeColor: 'bg-yellow-500',
  },
  CONFIRMED: {
    label: 'Confirmed',
    color: 'bg-blue-100 text-blue-800',
    badgeColor: 'bg-blue-500',
  },
  PROCESSING: {
    label: 'Processing',
    color: 'bg-purple-100 text-purple-800',
    badgeColor: 'bg-purple-500',
  },
  SHIPPED: {
    label: 'Shipped',
    color: 'bg-indigo-100 text-indigo-800',
    badgeColor: 'bg-indigo-500',
  },
  DELIVERED: {
    label: 'Delivered',
    color: 'bg-green-100 text-green-800',
    badgeColor: 'bg-green-500',
  },
  CANCELLED: {
    label: 'Cancelled',
    color: 'bg-red-100 text-red-800',
    badgeColor: 'bg-red-500',
  },
};

interface OrderCardProps {
  order: Order;
}

export default function OrderCard({
  order,
}: OrderCardProps) {
  const { mutate: updateOrderStatus, isPending } = useUpdateOrderStatus();

  const handleStatusChange = (orderId: number, newStatus: OrderStatus) => {
    updateOrderStatus({ orderId, status: newStatus });
  };

  return (
    <div className="p-6">
      <div className="mb-4 flex items-center justify-between">
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
                minute: '2-digit',
              })}
            </p>
          </div>
        </div>
        <div className="flex items-center space-x-4">
          <span
            className={`inline-flex items-center rounded-full px-3 py-1 text-sm font-medium ${ORDER_STATUS_CONFIG[order.status as keyof typeof ORDER_STATUS_CONFIG]?.color}`}
          >
            {
              ORDER_STATUS_CONFIG[
                order.status as keyof typeof ORDER_STATUS_CONFIG
              ]?.label
            }
          </span>
          <span className="text-lg font-bold text-gray-900">
            ${order.totalAmount.toFixed(2)}
          </span>
        </div>
      </div>

      {/* Customer Info */}
      <div className="mb-4 grid grid-cols-1 gap-4 md:grid-cols-2">
        <div>
          <h5 className="mb-1 text-sm font-medium text-gray-900">Customer</h5>
          <p className="text-sm text-gray-600">
            {order.user?.firstName} {order.user?.lastName}
          </p>
          <p className="text-sm text-gray-600">{order.user?.email}</p>
        </div>
        <div>
          <h5 className="mb-1 text-sm font-medium text-gray-900">
            Shipping Address
          </h5>
          <p className="text-sm text-gray-600">
            {order.shippingAddress?.addressLine1}
            {order.shippingAddress?.addressLine2 && (
              <>, {order.shippingAddress.addressLine2}</>
            )}
          </p>
          <p className="text-sm text-gray-600">
            {order.shippingAddress?.city}, {order.shippingAddress?.state}{' '}
            {order.shippingAddress?.postalCode}
          </p>
        </div>
      </div>

      {/* Order Items */}
      <div className="mb-4">
        <h5 className="mb-2 text-sm font-medium text-gray-900">
          Items ({order.orderItems?.length || 0})
        </h5>
        <div className="space-y-2">
          {order.orderItems
            ?.slice(0, 3)
            .map((item: OrderItem, index: number) => (
              <div
                key={item.id || index}
                className="flex items-center space-x-3"
              >
                {item.shoeModel?.imageUrl && (
                  <Image
                    src={item.shoeModel.imageUrl}
                    alt={item.shoeModel.modelName}
                    width={40}
                    height={40}
                    className="h-10 w-10 rounded object-cover"
                  />
                )}
                <div className="flex-1">
                  <p className="text-sm font-medium text-gray-900">
                    {item.shoeModel?.shoe?.name} - {item.shoeModel?.modelName}
                  </p>
                  <p className="text-sm text-gray-600">
                    Size: {item.size}, Qty: {item.quantity} × $
                    {item.unitPrice?.toFixed(2)}
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
      <div className="flex items-center justify-between border-t border-gray-200 pt-4">
        <div className="flex items-center space-x-2">
          <Link
            href={`/orders/${order.id}`}
            className="text-sm font-medium text-indigo-600 hover:text-indigo-900"
          >
            View Details
          </Link>
        </div>
        <div className="flex items-center space-x-3">
          {/* Process Order Button */}
          {order.status === 'PENDING' && (
            <button
              onClick={() => handleStatusChange(order.id, "CONFIRMED" as OrderStatus)}
              disabled={order.status !== 'PENDING'}
              className="rounded bg-green-600 px-3 py-1 text-sm font-medium text-white hover:bg-green-700 disabled:opacity-50"
            >
              {isPending ? 'Processing...' : 'Process Order'}
            </button>
          )}

          {/* Status Change Dropdown */}
          <select
            value={order.status}
            onChange={e =>
              handleStatusChange(order.id, e.target.value as OrderStatus)
            }
            disabled={isPending}
            className="rounded-md border border-gray-300 px-3 py-1 text-sm focus:border-transparent focus:ring-2 focus:ring-indigo-500 focus:outline-none"
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
