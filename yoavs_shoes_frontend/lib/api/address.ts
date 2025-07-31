import api from './client';
import { Address, AddressRequest, StandardResponse } from '@types';

// Address API functions
export const addressApi = {
  // Get current user's addresses (user-specific endpoint)
  getCurrentUserAddresses: async (page = 0, size = 100): Promise<StandardResponse<Array<Address>>> => {
    const response = await api.get(`/addresses/my-addresses?page=${page}&size=${size}`);
    return response.data;
  },


  getDefaultAddress: async (): Promise<StandardResponse<Address>> => {
    const response = await api.get('/addresses/default');
    return response.data;
  },

  createAddress: async (address: AddressRequest): Promise<StandardResponse<Address>> => {
    const response = await api.post('/addresses', address);
    return response.data;
  },

  updateAddress: async (id: number, address: AddressRequest): Promise<StandardResponse<Address>> => {
    const response = await api.put(`/addresses/${id}`, address);
    return response.data;
  },

  setDefaultAddress: async (id: number): Promise<StandardResponse<Address>> => {
    const response = await api.put(`/addresses/${id}/default`);
    return response.data;
  },

  deleteAddress: async (id: number): Promise<void> => {
    await api.delete(`/addresses/${id}`);
  },
};
