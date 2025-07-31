import api from './client';
import { ProductFilters, AvailableFilters, PageResponse, StandardResponse, ShoeModel } from '@types';

// Products API functions
export const getProducts = async (filters: ProductFilters = {}) => {
    const params = new URLSearchParams();
    
    Object.entries(filters).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        if (Array.isArray(value)) {
          value.forEach(item => params.append(key, item.toString()));
        } else {
          params.append(key, value.toString());
        }
      }
    });

    const response = await api.get<StandardResponse<PageResponse<ShoeModel>>>(`/products/filtered?${params.toString()}`);

    return response.data;
  }

export const searchProducts = async (query: string, filters: Omit<ProductFilters, 'search'> = {}) => {
    const params = new URLSearchParams({ q: query });
    
    Object.entries(filters).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        if (Array.isArray(value)) {
          value.forEach(item => params.append(key, item.toString()));
        } else {
          params.append(key, value.toString());
        }
      }
    });

    const response = await api.get<StandardResponse<PageResponse<ShoeModel>>>(`/products/search?${params.toString()}`);
    return response.data;
  }

  export const getAvailableFilters = async () => {
    const response = await api.get<StandardResponse<AvailableFilters>>('/products/filters');
    return response.data;
  }
