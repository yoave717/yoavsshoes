import { useQuery, UseQueryResult } from '@tanstack/react-query';
import { shoesApi } from '@api';
import { ShoeFilters, PageResponse, Shoe } from '@types';

export const useShoes = (filters: ShoeFilters = {}): UseQueryResult<PageResponse<Shoe>, Error> => {
  return useQuery({
    queryKey: ['shoes', filters],
    queryFn: () => shoesApi.getShoes(filters),
    staleTime: 5 * 60 * 1000, // 5 minutes
    refetchOnWindowFocus: false,
  });
};
