import api from './client';
import { Shoe } from '@types';

// Shoes API functions
export const shoesApi = {
  getShoes: async (): Promise<Shoe[]> => {
    const response = await api.get('/shoes');
    return response.data;
  },

  getShoe: async (id: number): Promise<Shoe> => {
    const response = await api.get(`/shoes/${id}`);
    return response.data;
  },

  createShoe: async (shoe: Omit<Shoe, 'id'>): Promise<Shoe> => {
    const response = await api.post('/shoes', shoe);
    return response.data;
  },

  updateShoe: async (id: number, shoe: Partial<Shoe>): Promise<Shoe> => {
    const response = await api.put(`/shoes/${id}`, shoe);
    return response.data;
  },

  deleteShoe: async (id: number): Promise<void> => {
    await api.delete(`/shoes/${id}`);
  },
};
