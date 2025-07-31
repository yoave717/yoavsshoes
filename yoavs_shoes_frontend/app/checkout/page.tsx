'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { ArrowLeft, Truck, Shield, Check, MapPin } from 'lucide-react';
import Image from 'next/image';
import { useCart } from '../../lib/cart-context';
import { useUserAddresses, useCreateAddress, useSetDefaultAddress, useCreateOrder, formatCartTotal, getCartSummary } from '@hooks';
import { Address, AddressRequest, CreateOrderRequest } from '@types';
import { convertCartItemsToOrderItems } from '../../lib/api/orders';
import { useUser } from '@/lib/contexts/UserContext';

interface ShippingInfo {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  addressLine1: string;
  addressLine2?: string;
  city: string;
  state?: string;
  postalCode: string;
  country: string;
  label?: string;
  deliveryInstructions?: string;
}

export default function CheckoutPage() {
  const router = useRouter();
  const { items, clearCart } = useCart();
  const summary = getCartSummary(items);

  // Get userId from user context or profile hook
  const { user } = useUser();

  // React Query hooks
  const { data: savedAddresses = [], isLoading: addressesLoading } = useUserAddresses();
  const createAddressMutation = useCreateAddress();
  const setDefaultAddressMutation = useSetDefaultAddress();
  const createOrderMutation = useCreateOrder();

  const [isProcessing, setIsProcessing] = useState(false);
  const [orderComplete, setOrderComplete] = useState(false);
  const [orderDetails, setOrderDetails] = useState<{ orderNumber?: string; id?: number } | null>(null);
  const [orderError, setOrderError] = useState<string | null>(null);
  const [selectedAddressId, setSelectedAddressId] = useState<number | null>(null);
  const [showNewAddressForm, setShowNewAddressForm] = useState(false);
  const [useSelectedAsDefault, setUseSelectedAsDefault] = useState(false);
  const [showAllAddresses, setShowAllAddresses] = useState(false);

  const [shippingInfo, setShippingInfo] = useState<ShippingInfo>({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    addressLine1: '',
    addressLine2: '',
    city: '',
    state: '',
    postalCode: '',
    country: 'United States',
    label: '',
    deliveryInstructions: '',
  });

  // Initialize addresses on load
  useEffect(() => {
    if (!addressesLoading && savedAddresses.length > 0) {
      // Find and select default address
      const defaultAddress = savedAddresses.find((addr: Address) => addr.isDefault);
      if (defaultAddress) {
        setSelectedAddressId(defaultAddress.id);
        populateShippingFromAddress(defaultAddress);
      } else {
        // If no default, select first address
        setSelectedAddressId(savedAddresses[0].id);
        populateShippingFromAddress(savedAddresses[0]);
      }
    } else if (!addressesLoading && savedAddresses.length === 0) {
      // No saved addresses, show new address form
      setShowNewAddressForm(true);
    }
  }, [savedAddresses, addressesLoading]);

  // Handle redirect when cart is empty
  useEffect(() => {
    if (items.length === 0 && !orderComplete) {
      router.push('/shoes');
    }
  }, [items.length, orderComplete, router]);

  // Populate shipping info from selected address
  const populateShippingFromAddress = (address: Address) => {
    setShippingInfo(prev => ({
      ...prev,
      firstName: address.firstName,
      lastName: address.lastName,
      email: address.email,
      phone: address.phoneNumber,
      addressLine1: address.addressLine1,
      addressLine2: address.addressLine2 || '',
      city: address.city,
      state: address.state || '',
      postalCode: address.postalCode,
      country: address.country,
      label: address.label || '',
      deliveryInstructions: address.deliveryInstructions || '',
    }));
  };

  // Handle address selection
  const handleAddressSelect = (addressId: number) => {
    setSelectedAddressId(addressId);
    const address = savedAddresses.find(addr => addr.id === addressId);
    if (address) {
      populateShippingFromAddress(address);
    }
    setShowNewAddressForm(false);
  };

  // If cart is empty, redirect to products
  useEffect(() => {
    if (items.length === 0 && !orderComplete) {
      router.push('/shoes');
    }
  }, [items.length, orderComplete, router]);

  if (items.length === 0 && !orderComplete) {
    return null;
  }

  const handleOrderSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsProcessing(true);
    setOrderError(null); // Clear any previous errors

    try {
      // Validate cart has items
      if (!items || items.length === 0) {
        throw new Error('Your cart is empty');
      }

      // Validate address selection when using saved addresses
      if (!showNewAddressForm && !selectedAddressId) {
        throw new Error('Please select a shipping address');
      }

      let addressId = selectedAddressId;

      // If using a new address, save it first
      if (showNewAddressForm) {
        const addressData: AddressRequest = {
          firstName: shippingInfo.firstName,
          lastName: shippingInfo.lastName,
          email: shippingInfo.email,
          phoneNumber: shippingInfo.phone,
          addressLine1: shippingInfo.addressLine1,
          addressLine2: shippingInfo.addressLine2,
          city: shippingInfo.city,
          state: shippingInfo.state,
          postalCode: shippingInfo.postalCode,
          country: shippingInfo.country,
          deliveryInstructions: shippingInfo.deliveryInstructions,
          isDefault: useSelectedAsDefault,
        };

        try {
          const response = await createAddressMutation.mutateAsync(addressData);
          console.log('New address saved:', response.data);
          addressId = response.data.id;
        } catch (error) {
          console.error('Failed to save new address:', error);
          throw new Error('Failed to save shipping address. Please try again.');
        }
      } else {
        // Using an existing saved address - only update default if needed
        if (selectedAddressId && useSelectedAsDefault) {
          const selectedAddress = savedAddresses.find((addr: Address) => addr.id === selectedAddressId);
          if (selectedAddress && !selectedAddress.isDefault) {
            try {
              await setDefaultAddressMutation.mutateAsync(selectedAddressId);
              console.log('Updated default address');
            } catch (error) {
              console.error('Failed to set default address:', error);
            }
          }
        }
      }

      // Ensure we have an address ID for the order
      if (!addressId) {
        throw new Error('Shipping address not found. Please select a shipping address.');
      }

      // Prepare order data - backend expects a simple structure
      if (!user || !user.id) {
        throw new Error('User not found. Please log in again.');
      }
      const orderData: CreateOrderRequest = {
        userId: user.id,
        items: convertCartItemsToOrderItems(items),
        shippingAddressId: addressId,
        specialInstructions: shippingInfo.deliveryInstructions || undefined
      };

      console.log('Preparing to create order with data:', orderData);

      // Create the order
      const orderResponse = await createOrderMutation.mutateAsync(orderData);
      console.log('Order created successfully:', orderResponse);

      setOrderError(null); // Clear any previous errors
      setOrderDetails({
        orderNumber: orderResponse.data.orderNumber,
        id: orderResponse.data.id
      });
      setIsProcessing(false);
      setOrderComplete(true);
      clearCart();
    } catch (error: unknown) {
      console.error('Order submission failed:', error);
      setIsProcessing(false);
      
      // Set error message based on the error response
      let errorMessage = 'Failed to place order. Please try again.';
      if (error && typeof error === 'object' && 'response' in error) {
        const axiosError = error as { response?: { data?: { message?: string } } };
        if (axiosError.response?.data?.message) {
          errorMessage = axiosError.response.data.message;
        }
      } else if (error instanceof Error) {
        errorMessage = error.message;
      }
      setOrderError(errorMessage);
    }
  };

  if (orderComplete) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
        <div className="max-w-md w-full bg-white rounded-lg shadow-lg p-8 text-center">
          <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <Check className="w-8 h-8 text-green-600" />
          </div>
          <h1 className="text-2xl font-bold text-gray-900 mb-2">Order Complete!</h1>
          {orderDetails?.orderNumber && (
            <p className="text-sm font-medium text-blue-600 mb-3">
              Order #{orderDetails.orderNumber}
            </p>
          )}
          <p className="text-gray-600 mb-6">
            Thank you for your purchase. You&apos;ll receive a confirmation email shortly.
          </p>
          <button
            onClick={() => router.push('/shoes')}
            className="w-full bg-blue-600 text-white py-3 px-4 rounded-lg font-medium hover:bg-blue-700 transition-colors"
          >
            Continue Shopping
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 text-gray-600">
      {/* Header */}
      <div className="bg-white border-b">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex items-center gap-4">
            <button
              onClick={() => router.back()}
              className="p-2 hover:bg-gray-100 rounded-full transition-colors"
            >
              <ArrowLeft className="w-5 h-5" />
            </button>
            <h1 className="text-2xl font-bold text-gray-900">Checkout</h1>
          </div>
        </div>
      </div>

      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Main Content */}
          <div className="lg:col-span-2">
            {/* Shipping Information */}
            <div className="bg-white rounded-lg shadow-sm border p-6">
              <h2 className="text-xl font-semibold mb-6 flex items-center gap-2">
                <Truck className="w-5 h-5" />
                Shipping Information
              </h2>

              {/* Loading state */}
              {addressesLoading && (
                <div className="mb-6">
                  <div className="flex items-center justify-center py-8">
                    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                    <span className="ml-3 text-gray-600">Loading addresses...</span>
                  </div>
                </div>
              )}

              {/* Address Selection */}
              {!addressesLoading && savedAddresses.length > 0 && (
                <div className="mb-6">
                  <h3 className="text-lg font-medium mb-4">Shipping Address</h3>
                  
                  {/* Address Selection Tabs */}
                  <div className="flex border-b border-gray-200 mb-6">
                    <button
                      type="button"
                      onClick={() => {
                        setShowNewAddressForm(false);
                        // Reset to default address if available
                        if (savedAddresses.length > 0 && !selectedAddressId) {
                          const defaultAddress = savedAddresses.find((addr: Address) => addr.isDefault);
                          if (defaultAddress) {
                            setSelectedAddressId(defaultAddress.id);
                            populateShippingFromAddress(defaultAddress);
                          }
                        }
                      }}
                      className={`px-4 py-2 text-sm font-medium border-b-2 transition-colors ${
                        !showNewAddressForm
                          ? 'border-blue-500 text-blue-600'
                          : 'border-transparent text-gray-500 hover:text-gray-700'
                      }`}
                    >
                      Saved Addresses ({savedAddresses.length})
                    </button>
                    <button
                      type="button"
                      onClick={() => {
                        setShowNewAddressForm(true);
                        setSelectedAddressId(null);
                        // Clear address fields but keep personal info if we have some
                        setShippingInfo(prev => ({
                          ...prev,
                          addressLine1: '',
                          addressLine2: '',
                          city: '',
                          state: '',
                          postalCode: '',
                          country: 'United States',
                          deliveryInstructions: '',
                        }));
                      }}
                      className={`px-4 py-2 text-sm font-medium border-b-2 transition-colors ${
                        showNewAddressForm
                          ? 'border-blue-500 text-blue-600'
                          : 'border-transparent text-gray-500 hover:text-gray-700'
                      }`}
                    >
                      Add New Address
                    </button>
                  </div>
                  
                  {/* Saved Addresses Tab */}
                  {!showNewAddressForm && (
                    <div className="space-y-4">
                      {/* Address Cards */}
                      <div className="grid gap-3">
                        {(() => {
                          // Show only first 3 addresses unless "show all" is enabled
                          const addressesToShow = showAllAddresses ? savedAddresses : savedAddresses.slice(0, 3);
                          const hasMore = savedAddresses.length > 3;
                          
                          return (
                            <>
                              {addressesToShow.map((address) => (
                                <div
                                  key={address.id}
                                  className={`border rounded-lg p-4 cursor-pointer transition-all hover:shadow-md ${
                                    selectedAddressId === address.id
                                      ? 'border-blue-500 bg-blue-50 shadow-sm'
                                      : 'border-gray-200 hover:border-gray-300'
                                  }`}
                                  onClick={() => handleAddressSelect(address.id)}
                                >
                                  <div className="flex items-start justify-between">
                                    <div className="flex-1">
                                      {/* Header with name and badges */}
                                      <div className="flex items-center gap-2 mb-2">
                                        <span className="font-semibold text-gray-900">
                                          {address.firstName} {address.lastName}
                                        </span>
                                        {address.isDefault && (
                                          <span className="text-xs bg-green-100 text-green-800 px-2 py-1 rounded-full font-medium">
                                            Default
                                          </span>
                                        )}
                                        {selectedAddressId === address.id && (
                                          <span className="text-xs bg-blue-100 text-blue-800 px-2 py-1 rounded-full font-medium">
                                            Selected
                                          </span>
                                        )}
                                      </div>
                                      
                                      {/* Address details in two columns */}
                                      <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                        {/* Contact info */}
                                        <div className="space-y-1">
                                          <div className="text-sm text-gray-600">
                                            <span className="font-medium">Email:</span> {address.email}
                                          </div>
                                          <div className="text-sm text-gray-600">
                                            <span className="font-medium">Phone:</span> {address.phoneNumber}
                                          </div>
                                        </div>
                                        
                                        {/* Address */}
                                        <div className="space-y-1">
                                          <div className="text-sm text-gray-700">
                                            <div className="font-medium">{address.addressLine1}</div>
                                            {address.addressLine2 && <div>{address.addressLine2}</div>}
                                            <div>
                                              {address.city}{address.state && `, ${address.state}`} {address.postalCode}
                                            </div>
                                            <div className="text-gray-600">{address.country}</div>
                                          </div>
                                        </div>
                                      </div>

                                      {/* Delivery instructions */}
                                      {address.deliveryInstructions && (
                                        <div className="mt-3 text-xs text-gray-600 bg-gray-50 p-2 rounded">
                                          <span className="font-medium">Delivery Instructions:</span>
                                          <span className="ml-1">{address.deliveryInstructions}</span>
                                        </div>
                                      )}
                                    </div>
                                    
                                    {/* Radio button */}
                                    <div className="ml-4 flex-shrink-0">
                                      <input
                                        type="radio"
                                        name="selectedAddress"
                                        checked={selectedAddressId === address.id}
                                        onChange={() => handleAddressSelect(address.id)}
                                        className="w-4 h-4 text-blue-600 border-gray-300 focus:ring-blue-500"
                                      />
                                    </div>
                                  </div>
                                </div>
                              ))}
                              
                              {/* Show more/less button */}
                              {hasMore && (
                                <div className="text-center py-3">
                                  <button
                                    type="button"
                                    onClick={() => setShowAllAddresses(!showAllAddresses)}
                                    className="text-sm text-blue-600 hover:text-blue-700 font-medium px-4 py-2 rounded-lg hover:bg-blue-50 transition-colors"
                                  >
                                    {showAllAddresses 
                                      ? `Show Less` 
                                      : `Show ${savedAddresses.length - 3} More Address${savedAddresses.length - 3 > 1 ? 'es' : ''}`
                                    }
                                  </button>
                                </div>
                              )}
                            </>
                          );
                        })()}
                      </div>

                      {/* Quick add new address link */}
                      <div className="text-center pt-4 border-t border-gray-100">
                        <button
                          type="button"
                          onClick={() => setShowNewAddressForm(true)}
                          className="text-sm text-blue-600 hover:text-blue-700 font-medium flex items-center justify-center gap-1"
                        >
                          <MapPin className="w-4 h-4" />
                          Don&apos;t see your address? Add a new one
                        </button>
                      </div>
                    </div>
                  )}
                  
                  {/* New Address Tab */}
                  {showNewAddressForm && (
                    <div className="bg-blue-50 rounded-lg p-4 mb-4">
                      <div className="flex items-center gap-2 mb-2">
                        <MapPin className="w-5 h-5 text-blue-600" />
                        <span className="font-medium text-blue-900">Add New Shipping Address</span>
                      </div>
                      <p className="text-sm text-blue-700">
                        Fill in the details below to add a new shipping address to your account.
                      </p>
                    </div>
                  )}
                </div>
              )}

              <form onSubmit={handleOrderSubmit} className="space-y-4">
                {/* Personal Information - only show when using new address */}
                {(showNewAddressForm || savedAddresses.length === 0) && (
                  <>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          First Name *
                        </label>
                        <input
                          type="text"
                          required
                          value={shippingInfo.firstName}
                          onChange={(e) => setShippingInfo(prev => ({ ...prev, firstName: e.target.value }))}
                          className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Last Name *
                        </label>
                        <input
                          type="text"
                          required
                          value={shippingInfo.lastName}
                          onChange={(e) => setShippingInfo(prev => ({ ...prev, lastName: e.target.value }))}
                          className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                      </div>
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Email Address *
                      </label>
                      <input
                        type="email"
                        required
                        value={shippingInfo.email}
                        onChange={(e) => setShippingInfo(prev => ({ ...prev, email: e.target.value }))}
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                      />
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Phone Number *
                      </label>
                      <input
                        type="tel"
                        required
                        value={shippingInfo.phone}
                        onChange={(e) => setShippingInfo(prev => ({ ...prev, phone: e.target.value }))}
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                      />
                    </div>
                  </>
                )}

                {/* Address Fields (shown when using new address or no saved addresses) */}
                {(showNewAddressForm || savedAddresses.length === 0) && (
                  <>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Address Line 1 *
                      </label>
                      <input
                        type="text"
                        required
                        value={shippingInfo.addressLine1}
                        onChange={(e) => setShippingInfo(prev => ({ ...prev, addressLine1: e.target.value }))}
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="Street address"
                      />
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Address Line 2
                      </label>
                      <input
                        type="text"
                        value={shippingInfo.addressLine2}
                        onChange={(e) => setShippingInfo(prev => ({ ...prev, addressLine2: e.target.value }))}
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="Apartment, suite, etc."
                      />
                    </div>

                    <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          City *
                        </label>
                        <input
                          type="text"
                          required
                          value={shippingInfo.city}
                          onChange={(e) => setShippingInfo(prev => ({ ...prev, city: e.target.value }))}
                          className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          State/Province
                        </label>
                        <input
                          type="text"
                          value={shippingInfo.state}
                          onChange={(e) => setShippingInfo(prev => ({ ...prev, state: e.target.value }))}
                          className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Postal Code *
                        </label>
                        <input
                          type="text"
                          required
                          value={shippingInfo.postalCode}
                          onChange={(e) => setShippingInfo(prev => ({ ...prev, postalCode: e.target.value }))}
                          className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                      </div>
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Country *
                      </label>
                      <select
                        required
                        value={shippingInfo.country}
                        onChange={(e) => setShippingInfo(prev => ({ ...prev, country: e.target.value }))}
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                      >
                        <option value="United States">United States</option>
                        <option value="Canada">Canada</option>
                        <option value="United Kingdom">United Kingdom</option>
                        <option value="Israel">Israel</option>
                        <option value="Germany">Germany</option>
                        <option value="France">France</option>
                      </select>
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Delivery Instructions
                      </label>
                      <textarea
                        value={shippingInfo.deliveryInstructions}
                        onChange={(e) => setShippingInfo(prev => ({ ...prev, deliveryInstructions: e.target.value }))}
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        rows={3}
                        placeholder="Special delivery instructions (optional)"
                      />
                    </div>

                    {/* Save as default address option */}
                    <div className="flex items-center gap-2">
                      <input
                        type="checkbox"
                        id="saveAsDefault"
                        checked={useSelectedAsDefault}
                        onChange={(e) => setUseSelectedAsDefault(e.target.checked)}
                        className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                      />
                      <label htmlFor="saveAsDefault" className="text-sm text-gray-700">
                        Save this address and set as default
                      </label>
                    </div>
                  </>
                )}

                {/* Set as default option for existing addresses */}
                {!showNewAddressForm && selectedAddressId && (
                  <>
                    {(() => {
                      const selectedAddress = savedAddresses.find(addr => addr.id === selectedAddressId);
                      return selectedAddress && !selectedAddress.isDefault ? (
                        <div className="flex items-center gap-2 bg-blue-50 p-3 rounded-lg">
                          <input
                            type="checkbox"
                            id="setAsDefault"
                            checked={useSelectedAsDefault}
                            onChange={(e) => setUseSelectedAsDefault(e.target.checked)}
                            className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                          />
                          <label htmlFor="setAsDefault" className="text-sm text-gray-700">
                            Set this address as my default address
                          </label>
                        </div>
                      ) : null;
                    })()}
                  </>
                )}

                <div className="flex items-center gap-2 text-sm text-gray-600 bg-gray-50 p-3 rounded-lg">
                  <Shield className="w-4 h-4" />
                  <span>Your order will be processed securely</span>
                </div>

                {/* Error Message */}
                {orderError && (
                  <div className="bg-red-50 border border-red-200 rounded-lg p-4">
                    <div className="flex items-center gap-2 mb-3">
                      <div className="w-5 h-5 bg-red-500 rounded-full flex items-center justify-center">
                        <span className="text-white text-xs font-bold">!</span>
                      </div>
                      <p className="text-red-700 text-sm font-medium">
                        {orderError}
                      </p>
                    </div>
                    <button
                      type="button"
                      onClick={() => setOrderError(null)}
                      className="text-sm text-red-600 hover:text-red-700 font-medium underline"
                    >
                      Dismiss
                    </button>
                  </div>
                )}

                <button
                  type="submit"
                  disabled={isProcessing || (!showNewAddressForm && !selectedAddressId)}
                  className="w-full bg-blue-600 text-white py-3 px-4 rounded-lg font-medium hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {isProcessing ? 'Processing Order...' : `Place Order - ${formatCartTotal(summary.total)}`}
                </button>
              </form>
            </div>
          </div>

          {/* Order Summary Sidebar */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-lg shadow-sm border p-6 sticky top-4">
              <h3 className="text-lg font-semibold mb-4">Order Summary</h3>
              
              {/* Cart Items */}
              <div className="space-y-3 mb-4">
                {items.map((item) => (
                  <div key={`${item.id}-${item.size}`} className="flex gap-3">
                    <div className="relative w-12 h-12 flex-shrink-0 bg-gray-100 rounded overflow-hidden">
                      {item.imageUrl ? (
                        <Image
                          src={item.imageUrl}
                          alt={item.modelName}
                          fill
                          className="object-cover"
                          sizes="48px"
                        />
                      ) : (
                        <div className="w-full h-full flex items-center justify-center bg-gray-200">
                          <Truck className="w-4 h-4 text-gray-400" />
                        </div>
                      )}
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium text-gray-900 truncate">
                        {item.modelName}
                      </p>
                      <p className="text-xs text-gray-500">
                        {item.color} • Size {item.size} • Qty {item.quantity}
                      </p>
                      <p className="text-sm font-semibold text-gray-900">
                        {formatCartTotal(item.price * item.quantity)}
                      </p>
                    </div>
                  </div>
                ))}
              </div>

              {/* Totals */}
              <div className="border-t pt-4 space-y-2">
                <div className="flex justify-between text-sm">
                  <span>Subtotal:</span>
                  <span>{formatCartTotal(summary.subtotal)}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span>Shipping:</span>
                  <span>{summary.shipping === 0 ? 'Free' : formatCartTotal(summary.shipping)}</span>
                </div>
                <div className="flex justify-between font-semibold text-base border-t pt-2">
                  <span>Total:</span>
                  <span>{formatCartTotal(summary.total)}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
