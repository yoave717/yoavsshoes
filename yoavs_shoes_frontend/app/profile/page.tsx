'use client';

import { useState, useEffect } from 'react';
import { useProfile, useMyOrders, useUserAddresses, useCancelOrder, useSetDefaultAddress, useDeleteAddress } from '../../lib/hooks';
import Link from 'next/link';
import { Order } from '../../lib/types/order';
import { Address } from '../../lib/types/address';

type TabType = 'profile' | 'orders' | 'addresses';

const ORDER_STATUS_CONFIG = {
  PENDING: { label: 'Pending', color: 'bg-yellow-100 text-yellow-800' },
  CONFIRMED: { label: 'Confirmed', color: 'bg-blue-100 text-blue-800' },
  PROCESSING: { label: 'Processing', color: 'bg-purple-100 text-purple-800' },
  SHIPPED: { label: 'Shipped', color: 'bg-indigo-100 text-indigo-800' },
  DELIVERED: { label: 'Delivered', color: 'bg-green-100 text-green-800' },
  CANCELLED: { label: 'Cancelled', color: 'bg-red-100 text-red-800' },
};

export default function Profile() {
  const [activeTab, setActiveTab] = useState<TabType>('profile');
  const [addressMenuOpen, setAddressMenuOpen] = useState<number | null>(null);
  const { data: user, isLoading: userLoading, error: userError } = useProfile();
  const { data: ordersData, isLoading: ordersLoading } = useMyOrders();
  const { data: addresses, isLoading: addressesLoading, refetch: refetchAddresses } = useUserAddresses();
  const cancelOrderMutation = useCancelOrder();
  const setDefaultAddressMutation = useSetDefaultAddress();
  const deleteAddressMutation = useDeleteAddress();

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = () => {
      if (addressMenuOpen !== null) {
        setAddressMenuOpen(null);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [addressMenuOpen]);

  const handleCancelOrder = async (orderId: number) => {
    if (confirm('Are you sure you want to cancel this order?')) {
      try {
        await cancelOrderMutation.mutateAsync(orderId);
        // Refresh orders data
        window.location.reload();
      } catch (error) {
        console.error('Failed to cancel order:', error);
      }
    }
  };

  const handleSetDefaultAddress = async (addressId: number) => {
    try {
      await setDefaultAddressMutation.mutateAsync(addressId);
      refetchAddresses();
      setAddressMenuOpen(null);
    } catch (error) {
      console.error('Failed to set default address:', error);
    }
  };

  const handleDeleteAddress = async (addressId: number) => {
    if (confirm('Are you sure you want to delete this address?')) {
      try {
        await deleteAddressMutation.mutateAsync(addressId);
        refetchAddresses();
        setAddressMenuOpen(null);
      } catch (error) {
        console.error('Failed to delete address:', error);
      }
    }
  };

  if (userLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="mx-auto h-12 w-12 animate-spin rounded-full border-b-2 border-indigo-600"></div>
          <p className="mt-4 text-gray-600">Loading profile...</p>
        </div>
      </div>
    );
  }

  if (userError || !user) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-gray-50">
        <div className="text-center">
          <p className="mb-4 text-red-600">
            Unable to load profile. Please try logging in again.
          </p>
          <Link
            href="/login"
            className="font-medium text-indigo-600 hover:text-indigo-500"
          >
            Go to Login
          </Link>
        </div>
      </div>
    );
  }

  const orders = ordersData?.data?.content || [];
  const groupedOrders = orders.reduce((acc: Record<string, Order[]>, order: Order) => {
    if (!acc[order.status]) {
      acc[order.status] = [];
    }
    acc[order.status].push(order);
    return acc;
  }, {});

  const canCancelOrder = (status: string) => {
    return status === 'PENDING' || status === 'CONFIRMED';
  };

  return (
    <div className="min-h-screen bg-gray-50 px-4 py-12 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-6xl">
        {/* Header */}
        <div className="mb-8 rounded-lg bg-white shadow">
          <div className="px-6 py-4">
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-4">
                <div className="flex h-16 w-16 items-center justify-center rounded-full bg-indigo-100">
                  <svg
                    className="h-8 w-8 text-indigo-600"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                    />
                  </svg>
                </div>
                <div>
                  <h1 className="text-3xl font-bold text-gray-900">
                    {user.firstName} {user.lastName}
                  </h1>
                  <p className="text-gray-600">{user.email}</p>
                </div>
              </div>
              <div className="flex space-x-6 text-center">
                <div>
                  <p className="text-2xl font-bold text-indigo-600">
                    {ordersLoading ? '...' : orders.length}
                  </p>
                  <p className="text-sm text-gray-500">Total Orders</p>
                </div>
                <div>
                  <p className="text-2xl font-bold text-green-600">
                    {ordersLoading ? '...' : orders.filter((order: Order) => order.status === 'DELIVERED').length}
                  </p>
                  <p className="text-sm text-gray-500">Delivered</p>
                </div>
                <div>
                  <p className="text-2xl font-bold text-purple-600">
                    {addressesLoading ? '...' : addresses?.length || 0}
                  </p>
                  <p className="text-sm text-gray-500">Addresses</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Tabs */}
        <div className="mb-6">
          <nav className="flex space-x-8" aria-label="Tabs">
            {[
              { id: 'profile', name: 'Profile', icon: '👤' },
              { id: 'orders', name: 'Orders', icon: '📦' },
              { id: 'addresses', name: 'Addresses', icon: '📍' },
            ].map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id as TabType)}
                className={`flex items-center space-x-2 py-2 px-1 border-b-2 font-medium text-sm ${
                  activeTab === tab.id
                    ? 'border-indigo-500 text-indigo-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                <span>{tab.icon}</span>
                <span>{tab.name}</span>
              </button>
            ))}
          </nav>
        </div>

        {/* Tab Content */}
        <div className="rounded-lg bg-white shadow">
          {activeTab === 'profile' && (
            <div className="px-6 py-8">
              <h2 className="mb-6 text-xl font-semibold text-gray-900">
                Profile Information
              </h2>
              <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
                <div>
                  <label className="block text-sm font-medium text-gray-700">
                    First Name
                  </label>
                  <p className="mt-1 text-sm text-gray-900">
                    {user.firstName}
                  </p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">
                    Last Name
                  </label>
                  <p className="mt-1 text-sm text-gray-900">
                    {user.lastName}
                  </p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">
                    Email
                  </label>
                  <p className="mt-1 text-sm text-gray-900">
                    {user.email}
                  </p>
                </div>
                {user.phoneNumber && (
                  <div>
                    <label className="block text-sm font-medium text-gray-700">
                      Phone Number
                    </label>
                    <p className="mt-1 text-sm text-gray-900">
                      {user.phoneNumber}
                    </p>
                  </div>
                )}
                <div>
                  <label className="block text-sm font-medium text-gray-700">
                    Member Since
                  </label>
                  <p className="mt-1 text-sm text-gray-900">
                    {new Date(user.createdAt).toLocaleDateString('en-US', {
                      year: 'numeric',
                      month: 'long',
                      day: 'numeric',
                    })}
                  </p>
                </div>
              </div>
              <div className="mt-8 flex space-x-4">
                <Link
                  href="/"
                  className="rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50"
                >
                  Back to Home
                </Link>
                <Link
                  href="/shoes"
                  className="rounded-md border border-transparent bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700"
                >
                  Browse Shoes
                </Link>
              </div>
            </div>
          )}

          {activeTab === 'orders' && (
            <div className="px-6 py-8">
              <h2 className="mb-6 text-xl font-semibold text-gray-900">
                Order History
              </h2>
              {ordersLoading ? (
                <div className="flex justify-center py-12">
                  <div className="h-8 w-8 animate-spin rounded-full border-b-2 border-indigo-600"></div>
                </div>
              ) : orders.length === 0 ? (
                <div className="text-center py-12">
                  <div className="mx-auto h-16 w-16 text-gray-400 mb-4">
                    <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                    </svg>
                  </div>
                  <p className="text-xl font-medium text-gray-900 mb-2">No orders yet</p>
                  <p className="text-gray-500 mb-6">When you place your first order, it will appear here</p>
                  <Link
                    href="/shoes"
                    className="bg-indigo-600 text-white px-6 py-3 rounded-md text-sm font-medium hover:bg-indigo-700 transition-colors inline-flex items-center"
                  >
                    <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                    </svg>
                    Start Shopping
                  </Link>
                </div>
              ) : (
                <div className="space-y-6">
                  {Object.entries(ORDER_STATUS_CONFIG).map(([status, config]) => {
                    const statusOrders = groupedOrders[status] || [];
                    if (statusOrders.length === 0) return null;

                    return (
                      <div key={status} className="border rounded-lg p-4">
                        <h3 className="text-lg font-medium text-gray-900 mb-4 flex items-center">
                          <span
                            className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium mr-3 ${config.color}`}
                          >
                            {config.label}
                          </span>
                          {statusOrders.length} order{statusOrders.length !== 1 ? 's' : ''}
                        </h3>
                        <div className="space-y-3">
                          {statusOrders.map((order: Order) => (
                            <div
                              key={order.id}
                              className="border rounded-md p-4 hover:bg-gray-50 transition-colors"
                            >
                              <div className="flex justify-between items-start mb-3">
                                <div className="flex-1">
                                  <div className="flex items-center space-x-3 mb-2">
                                    <p className="font-medium text-gray-900">
                                      Order #{order.orderNumber}
                                    </p>
                                    <span
                                      className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${config.color}`}
                                    >
                                      {config.label}
                                    </span>
                                  </div>
                                  <p className="text-sm text-gray-500 mb-1">
                                    {new Date(order.orderDate).toLocaleDateString()} • {order.orderItems?.length || 0} item{(order.orderItems?.length || 0) !== 1 ? 's' : ''}
                                  </p>
                                  <p className="text-lg font-semibold text-gray-900">
                                    ${order.totalAmount.toFixed(2)}
                                  </p>
                                  {order.shippedDate && (
                                    <p className="text-sm text-gray-500 mt-1">
                                      Shipped: {new Date(order.shippedDate).toLocaleDateString()}
                                    </p>
                                  )}
                                  {order.deliveredDate && (
                                    <p className="text-sm text-gray-500 mt-1">
                                      Delivered: {new Date(order.deliveredDate).toLocaleDateString()}
                                    </p>
                                  )}
                                </div>
                                <div className="flex flex-col space-y-2">
                                  {canCancelOrder(order.status) && (
                                    <button
                                      onClick={() => handleCancelOrder(order.id)}
                                      className="text-red-600 hover:text-red-800 text-sm font-medium px-3 py-1 border border-red-300 rounded hover:bg-red-50 transition-colors"
                                      disabled={cancelOrderMutation.isPending}
                                    >
                                      {cancelOrderMutation.isPending ? 'Cancelling...' : 'Cancel Order'}
                                    </button>
                                  )}
                                  <Link
                                    href={`/orders/${order.id}`}
                                    className="text-indigo-600 hover:text-indigo-800 text-sm font-medium px-3 py-1 border border-indigo-300 rounded hover:bg-indigo-50 transition-colors inline-block text-center"
                                  >
                                    View Details
                                  </Link>
                                </div>
                              </div>
                            </div>
                          ))}
                        </div>
                      </div>
                    );
                  })}
                </div>
              )}
            </div>
          )}

          {activeTab === 'addresses' && (
            <div className="px-6 py-8">
              <div className="flex justify-between items-center mb-6">
                <h2 className="text-xl font-semibold text-gray-900">
                  Saved Addresses
                </h2>
                <button className="bg-indigo-600 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-indigo-700">
                  Add New Address
                </button>
              </div>
              {addressesLoading ? (
                <div className="flex justify-center py-12">
                  <div className="h-8 w-8 animate-spin rounded-full border-b-2 border-indigo-600"></div>
                </div>
              ) : !addresses || addresses.length === 0 ? (
                <div className="text-center py-12">
                  <div className="mx-auto h-12 w-12 text-gray-400 mb-4">
                    <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                    </svg>
                  </div>
                  <p className="text-gray-500 mb-4">No addresses saved yet</p>
                  <p className="text-gray-400 text-sm mb-4">Add an address to get started with faster checkout</p>
                  <button className="bg-indigo-600 text-white px-6 py-2 rounded-md text-sm font-medium hover:bg-indigo-700 transition-colors">
                    Add Your First Address
                  </button>
                </div>
              ) : (
                <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                  {addresses.map((address: Address) => (
                    <div
                      key={address.id}
                      className={`border rounded-lg p-4 relative ${
                        address.isDefault ? 'border-indigo-500 bg-indigo-50' : 'border-gray-300'
                      }`}
                    >
                      <div className="flex justify-between items-start mb-2">
                        <div className="flex-1">
                          {address.label && (
                            <p className="font-medium text-gray-900 mb-1">{address.label}</p>
                          )}
                          {address.isDefault && (
                            <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-indigo-100 text-indigo-800 mb-2">
                              Default Address
                            </span>
                          )}
                        </div>
                        <div className="relative">
                          <button
                            onClick={() => setAddressMenuOpen(addressMenuOpen === address.id ? null : address.id)}
                            className="text-gray-400 hover:text-gray-600 p-1"
                          >
                            <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                              <path d="M10 6a2 2 0 110-4 2 2 0 010 4zM10 12a2 2 0 110-4 2 2 0 010 4zM10 18a2 2 0 110-4 2 2 0 010 4z" />
                            </svg>
                          </button>
                          {addressMenuOpen === address.id && (
                            <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg z-10 border">
                              <div className="py-1">
                                {!address.isDefault && (
                                  <button
                                    onClick={() => handleSetDefaultAddress(address.id)}
                                    className="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                                    disabled={setDefaultAddressMutation.isPending}
                                  >
                                    Set as Default
                                  </button>
                                )}
                                <button
                                  onClick={() => {/* TODO: Edit address */}}
                                  className="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                                >
                                  Edit Address
                                </button>
                                <button
                                  onClick={() => handleDeleteAddress(address.id)}
                                  className="block w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-gray-100"
                                  disabled={deleteAddressMutation.isPending}
                                >
                                  Delete Address
                                </button>
                              </div>
                            </div>
                          )}
                        </div>
                      </div>
                      <div className="text-sm text-gray-600">
                        <p className="font-medium">{address.firstName} {address.lastName}</p>
                        <p>{address.addressLine1}</p>
                        {address.addressLine2 && <p>{address.addressLine2}</p>}
                        <p>{address.city}, {address.state} {address.postalCode}</p>
                        <p>{address.country}</p>
                        {address.phoneNumber && <p className="mt-1">📞 {address.phoneNumber}</p>}
                        {address.deliveryInstructions && (
                          <p className="mt-2 text-xs text-gray-500">
                            📝 {address.deliveryInstructions}
                          </p>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
