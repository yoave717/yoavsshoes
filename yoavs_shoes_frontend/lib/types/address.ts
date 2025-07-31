export interface Address {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  addressLine1: string;
  addressLine2?: string;
  city: string;
  state?: string;
  postalCode: string;
  country: string;
  isDefault: boolean;
  label?: string;
  deliveryInstructions?: string;
  formattedAddress?: string;
  shortFormattedAddress?: string;
  displayLabel?: string;
  createdAt: string;
  updatedAt: string;
  isComplete: boolean;
}

export interface AddressRequest {
  addressLine1: string;
  addressLine2?: string;
  city: string;
  state?: string;
  postalCode: string;
  country: string;
  isDefault?: boolean;
  label?: string;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  deliveryInstructions?: string;
}
