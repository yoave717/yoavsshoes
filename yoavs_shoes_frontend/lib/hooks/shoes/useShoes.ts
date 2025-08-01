import { keepPreviousData, useQuery, UseQueryResult } from '@tanstack/react-query';
import { shoesApi } from '@api';
import { ShoeFilters, PageResponse, Shoe } from '@/lib/types';

export const useShoes = (filters: ShoeFilters = {}): UseQueryResult<PageResponse<Shoe>, Error> => {
  return useQuery({
    queryKey: ['shoes', filters],
    queryFn: () => shoesApi.getShoes(filters),
    staleTime: 5 * 60 * 1000, // 5 minutes
    refetchOnWindowFocus: false,
  });
};

export const useShoesForInventory = (filters: ShoeFilters = {}) => {
  return useQuery({
    queryKey: ['shoes', 'inventory', filters],
    queryFn: () => shoesApi.getShoesForInventory(filters),
    staleTime: 2 * 60 * 1000, // 2 minutes for inventory data
    refetchOnWindowFocus: false,
    placeholderData: keepPreviousData
  });
};

export const useShoe = (id: number): UseQueryResult<Shoe, Error> => {
  return useQuery({
    queryKey: ['shoes', id],
    queryFn: () => shoesApi.getShoe(id),
    enabled: !!id,
    staleTime: 10 * 60 * 1000, // 10 minutes
  });
};
