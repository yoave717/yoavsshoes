'use client';

import { useParams, useRouter } from 'next/navigation';
import Link from 'next/link';
import Image from 'next/image';
import { useOrderDetails, useCancelOrder } from '../../../lib/hooks';
import { Order } from '../../../lib/types/order';

const ORDER_STATUS_CONFIG = {
  PENDING: { label: 'Pending', color: 'bg-yellow-100 text-yellow-800', description: 'Your order is being processed' },
  CONFIRMED: { label: 'Confirmed', color: 'bg-blue-100 text-blue-800', description: 'Your order has been confirmed and will be prepared soon' },
  PROCESSING: { label: 'Processing', color: 'bg-purple-100 text-purple-800', description: 'Your order is being prepared for shipment' },
  SHIPPED: { label: 'Shipped', color: 'bg-indigo-100 text-indigo-800', description: 'Your order is on its way' },
  DELIVERED: { label: 'Delivered', color: 'bg-green-100 text-green-800', description: 'Your order has been delivered' },
  CANCELLED: { label: 'Cancelled', color: 'bg-red-100 text-red-800', description: 'Your order has been cancelled' },
};

export default function OrderDetailsPage() {
  const params = useParams();
  const router = useRouter();
  const orderId = Number(params.id);
  
  const { data: orderResponse, isLoading, error } = useOrderDetails(orderId);
  const cancelOrderMutation = useCancelOrder();

  const order: Order | undefined = orderResponse?.data;

  const handleCancelOrder = async () => {
    if (confirm('Are you sure you want to cancel this order?')) {
      try {
        await cancelOrderMutation.mutateAsync(orderId);
        router.refresh();
      } catch (error) {
        console.error('Failed to cancel order:', error);
      }
    }
  };

  const canCancelOrder = (status: string) => {
    return status === 'PENDING' || status === 'CONFIRMED';
  };

  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="mx-auto h-12 w-12 animate-spin rounded-full border-b-2 border-indigo-600"></div>
          <p className="mt-4 text-gray-600">Loading order details...</p>
        </div>
      </div>
    );
  }

  if (error || !order) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="mx-auto h-12 w-12 text-red-500 mb-4">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
            </svg>
          </div>
          <p className="mb-4 text-red-600">
            Unable to load order details. The order may not exist or you don&apos;t have permission to view it.
          </p>
          <Link
            href="/profile?tab=orders"
            className="font-medium text-indigo-600 hover:text-indigo-500"
          >
            Back to Orders
          </Link>
        </div>
      </div>
    );
  }

  const statusConfig = ORDER_STATUS_CONFIG[order.status as keyof typeof ORDER_STATUS_CONFIG];

  return (
    <div className="min-h-screen bg-gray-50 px-4 py-8 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-4xl">
        {/* Header */}
        <div className="mb-6">
          <nav className="flex items-center space-x-2 text-sm text-gray-500 mb-4">
            <Link href="/" className="hover:text-gray-700">Home</Link>
            <span>/</span>
            <Link href="/profile?tab=orders" className="hover:text-gray-700">Orders</Link>
            <span>/</span>
            <span className="text-gray-900">Order #{order.orderNumber}</span>
          </nav>
          
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">
                Order #{order.orderNumber}
              </h1>
              <p className="text-gray-600 mt-1">
                Placed on {new Date(order.orderDate).toLocaleDateString('en-US', {
                  year: 'numeric',
                  month: 'long',
                  day: 'numeric',
                  hour: '2-digit',
                  minute: '2-digit'
                })}
              </p>
            </div>
            <div className="flex flex-col items-end space-y-2">
              <span className={`inline-flex items-center px-3 py-1 rounded-full text-sm font-medium ${statusConfig.color}`}>
                {statusConfig.label}
              </span>
              <p className="text-2xl font-bold text-gray-900">
                ${order.totalAmount.toFixed(2)}
              </p>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Main Content */}
          <div className="lg:col-span-2 space-y-6">
            {/* Order Status */}
            <div className="bg-white rounded-lg shadow p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4">Order Status</h2>
              <div className="flex items-center space-x-4">
                <div className={`w-3 h-3 rounded-full ${statusConfig.color.includes('green') ? 'bg-green-500' : statusConfig.color.includes('red') ? 'bg-red-500' : statusConfig.color.includes('yellow') ? 'bg-yellow-500' : statusConfig.color.includes('blue') ? 'bg-blue-500' : statusConfig.color.includes('purple') ? 'bg-purple-500' : 'bg-indigo-500'}`}></div>
                <div>
                  <p className="font-medium text-gray-900">{statusConfig.label}</p>
                  <p className="text-sm text-gray-600">{statusConfig.description}</p>
                </div>
              </div>
              
              {/* Status Timeline */}
              <div className="mt-6">
                <div className="flex items-center justify-between text-sm">
                  <div className="flex flex-col items-center">
                    <div className={`w-4 h-4 rounded-full ${order.status !== 'CANCELLED' ? 'bg-green-500' : 'bg-gray-300'}`}></div>
                    <p className="mt-2 text-center">
                      <span className="font-medium">Placed</span><br />
                      <span className="text-gray-500">{new Date(order.orderDate).toLocaleDateString()}</span>
                    </p>
                  </div>
                  
                  <div className="flex-1 h-0.5 bg-gray-300 mx-4"></div>
                  
                  <div className="flex flex-col items-center">
                    <div className={`w-4 h-4 rounded-full ${['CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED'].includes(order.status) ? 'bg-green-500' : 'bg-gray-300'}`}></div>
                    <p className="mt-2 text-center">
                      <span className="font-medium">Confirmed</span><br />
                      <span className="text-gray-500">
                        {['CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED'].includes(order.status) ? 'Confirmed' : 'Pending'}
                      </span>
                    </p>
                  </div>
                  
                  <div className="flex-1 h-0.5 bg-gray-300 mx-4"></div>
                  
                  <div className="flex flex-col items-center">
                    <div className={`w-4 h-4 rounded-full ${['SHIPPED', 'DELIVERED'].includes(order.status) ? 'bg-green-500' : 'bg-gray-300'}`}></div>
                    <p className="mt-2 text-center">
                      <span className="font-medium">Shipped</span><br />
                      <span className="text-gray-500">
                        {order.shippedDate ? new Date(order.shippedDate).toLocaleDateString() : 'Not shipped'}
                      </span>
                    </p>
                  </div>
                  
                  <div className="flex-1 h-0.5 bg-gray-300 mx-4"></div>
                  
                  <div className="flex flex-col items-center">
                    <div className={`w-4 h-4 rounded-full ${order.status === 'DELIVERED' ? 'bg-green-500' : 'bg-gray-300'}`}></div>
                    <p className="mt-2 text-center">
                      <span className="font-medium">Delivered</span><br />
                      <span className="text-gray-500">
                        {order.deliveredDate ? new Date(order.deliveredDate).toLocaleDateString() : 'Not delivered'}
                      </span>
                    </p>
                  </div>
                </div>
              </div>
            </div>

            {/* Order Items */}
            <div className="bg-white rounded-lg shadow p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4">
                Order Items ({order.orderItems?.length || 0})
              </h2>
              <div className="space-y-4">
                {order.orderItems?.map((item, index) => (
                  <div key={item.id || index} className="flex items-center space-x-4 p-4 border rounded-lg">
                    {item.shoeModel?.imageUrl && (
                      <Image
                        src={item.shoeModel.imageUrl}
                        alt={item.shoeModel.shoe?.name || item.shoeModel.modelName}
                        width={64}
                        height={64}
                        className="w-16 h-16 object-cover rounded-md"
                      />
                    )}
                    <div className="flex-1">
                      <h3 className="font-medium text-gray-900">
                        {item.shoeModel?.shoe?.name || 'Product'} - {item.shoeModel?.modelName}
                      </h3>
                      <p className="text-sm text-gray-600">
                        Brand: {item.shoeModel?.shoe?.brand?.name || 'Unknown'}
                      </p>
                      <p className="text-sm text-gray-600">
                        Color: {item.shoeModel?.color} • Size: {item.size} • Quantity: {item.quantity}
                      </p>
                      {item.shoeModel?.material && (
                        <p className="text-sm text-gray-600">
                          Material: {item.shoeModel.material}
                        </p>
                      )}
                    </div>
                    <div className="text-right">
                      <p className="font-medium text-gray-900">
                        ${(item.unitPrice || 0).toFixed(2)} each
                      </p>
                      <p className="text-sm text-gray-600">
                        Total: ${(item.totalPrice || 0).toFixed(2)}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Special Instructions */}
            {order.specialInstructions && (
              <div className="bg-white rounded-lg shadow p-6">
                <h2 className="text-xl font-semibold text-gray-900 mb-4">Special Instructions</h2>
                <p className="text-gray-700 bg-gray-50 p-4 rounded-md">
                  {order.specialInstructions}
                </p>
              </div>
            )}
          </div>

          {/* Sidebar */}
          <div className="space-y-6">
            {/* Shipping Address */}
            {order.shippingAddress && (
              <div className="bg-white rounded-lg shadow p-6">
                <h2 className="text-xl font-semibold text-gray-900 mb-4">
                  <div className="flex items-center">
                    <svg className="w-5 h-5 mr-2 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                    </svg>
                    Shipping Address
                  </div>
                </h2>
                
                <div className="space-y-4">
                  {/* Contact Information */}
                  <div className="border-b border-gray-200 pb-3">
                    <h3 className="text-sm font-medium text-gray-900 mb-2">Contact Information</h3>
                    <div className="text-sm text-gray-700 space-y-1">
                      <p className="font-medium">
                        {order.shippingAddress.firstName || order.user?.firstName || ''} {order.shippingAddress.lastName || order.user?.lastName || ''}
                      </p>
                      {(order.shippingAddress.email || order.user?.email) && (
                        <div className="flex items-center">
                          <svg className="w-4 h-4 mr-2 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 8l7.89 4.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                          </svg>
                          <span>{order.shippingAddress.email || order.user?.email}</span>
                        </div>
                      )}
                      {(order.shippingAddress.phoneNumber || order.user?.phoneNumber) && (
                        <div className="flex items-center">
                          <svg className="w-4 h-4 mr-2 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                          </svg>
                          <span>{order.shippingAddress.phoneNumber || order.user?.phoneNumber}</span>
                        </div>
                      )}
                    </div>
                  </div>

                  {/* Physical Address */}
                  <div>
                    <h3 className="text-sm font-medium text-gray-900 mb-2">Delivery Address</h3>
                    <div className="text-sm text-gray-700 space-y-1">
                      <p className="font-medium">{order.shippingAddress.addressLine1}</p>
                      {order.shippingAddress.addressLine2 && (
                        <p>{order.shippingAddress.addressLine2}</p>
                      )}
                      <p>
                        {order.shippingAddress.city}
                        {order.shippingAddress.state && `, ${order.shippingAddress.state}`} {order.shippingAddress.postalCode}
                      </p>
                      <p className="font-medium">{order.shippingAddress.country}</p>
                    </div>
                  </div>



                  {/* Delivery Instructions */}
                  {order.shippingAddress.deliveryInstructions && (
                    <div className="bg-blue-50 p-3 rounded-md border-l-4 border-blue-400">
                      <h3 className="text-sm font-medium text-blue-900 mb-1 flex items-center">
                        <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                        Delivery Instructions
                      </h3>
                      <p className="text-sm text-blue-800">{order.shippingAddress.deliveryInstructions}</p>
                    </div>
                  )}
                </div>
              </div>
            )}

            {/* Order Summary */}
            <div className="bg-white rounded-lg shadow p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4">Order Summary</h2>
              <div className="space-y-2 text-sm">
                <div className="flex justify-between">
                  <span className="text-gray-600">Items ({order.orderItems?.length || 0})</span>
                  <span className="text-gray-900">${order.totalAmount.toFixed(2)}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Shipping</span>
                  <span className="text-gray-900">Free</span>
                </div>
                <div className="border-t pt-2 mt-2">
                  <div className="flex justify-between font-semibold">
                    <span className="text-gray-900">Total</span>
                    <span className="text-gray-900">${order.totalAmount.toFixed(2)}</span>
                  </div>
                </div>
              </div>
            </div>

            {/* Actions */}
            <div className="bg-white rounded-lg shadow p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4">Actions</h2>
              <div className="space-y-3">
                {canCancelOrder(order.status) && (
                  <button
                    onClick={handleCancelOrder}
                    className="w-full bg-red-600 text-white py-2 px-4 rounded-md text-sm font-medium hover:bg-red-700 transition-colors"
                    disabled={cancelOrderMutation.isPending}
                  >
                    {cancelOrderMutation.isPending ? 'Cancelling...' : 'Cancel Order'}
                  </button>
                )}
                <Link
                  href="/profile?tab=orders"
                  className="block w-full bg-gray-100 text-gray-700 py-2 px-4 rounded-md text-sm font-medium hover:bg-gray-200 transition-colors text-center"
                >
                  Back to Orders
                </Link>
                <Link
                  href="/shoes"
                  className="block w-full bg-indigo-600 text-white py-2 px-4 rounded-md text-sm font-medium hover:bg-indigo-700 transition-colors text-center"
                >
                  Continue Shopping
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
