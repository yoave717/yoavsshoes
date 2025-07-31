import { getProducts } from '@api';
import { useQuery } from '@tanstack/react-query';
import { ProductFilters } from '@types';

export const useProducts = (filters: ProductFilters = {}) => {
  return useQuery({
    queryKey: ['products', filters],
    queryFn: () => getProducts(filters),
    staleTime: 1000 * 60 * 5, // 5 minutes
  });
};
