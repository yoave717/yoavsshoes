import api from './client';
import { PageResponse, Shoe, ShoeFilters, ShoeInventoryView, StandardResponse } from '@types';

// Shoes API functions
export const shoesApi = {
  getShoes: async (filters: ShoeFilters = {}): Promise<PageResponse<Shoe>> => {
    const response = await api.get<StandardResponse<PageResponse<Shoe>>>('/shoes/filtered', { params: filters });
    return response.data.data;
  },

  getAllShoes: async (filters: ShoeFilters = {}): Promise<PageResponse<Shoe>> => {
    const response = await api.get<StandardResponse<PageResponse<Shoe>>>('/shoes', { params: filters });
    return response.data.data;
  },

  getShoe: async (id: number): Promise<Shoe> => {
    const response = await api.get<StandardResponse<Shoe>>(`/shoes/${id}`);
    return response.data.data;
  },

  createShoe: async (shoe: Omit<Shoe, 'id'>): Promise<Shoe> => {
    const response = await api.post<StandardResponse<Shoe>>('/shoes', shoe);
    return response.data.data;
  },

  updateShoe: async (id: number, shoe: Partial<Shoe>): Promise<Shoe> => {
    const response = await api.put<StandardResponse<Shoe>>(`/shoes/${id}`, shoe);
    return response.data.data;
  },

  deleteShoe: async (id: number): Promise<void> => {
    await api.delete(`/shoes/${id}`);
  },

  // For inventory management - get shoes with their models and stock
  getShoesForInventory: async (filters: ShoeFilters = {}) => {
    const response = await api.get<StandardResponse<PageResponse<ShoeInventoryView>>>('/shoes/with-model-count', { params: filters });
    return response.data;
  },
};
