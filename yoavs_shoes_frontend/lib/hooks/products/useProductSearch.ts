import { useQuery } from '@tanstack/react-query';
import { searchProducts } from '@api';
import { ProductFilters } from '@types';

export const useProductSearch = (query: string, filters: Omit<ProductFilters, 'search'> = {}) => {
  return useQuery({
    queryKey: ['products', 'search', query, filters],
    queryFn: () => searchProducts(query, filters),
    enabled: !!query.trim(),
    staleTime: 1000 * 60 * 5,
  });
};
