import { shoesApi } from "@api";
import { useQuery, keepPreviousData } from "@tanstack/react-query";
import { ShoeFilters } from "@types";

export const useShoesForInventory = (filters: ShoeFilters = {}) => {
  return useQuery({
    queryKey: ['shoes', 'inventory', filters],
    queryFn: () => shoesApi.getShoesForInventory(filters),
    staleTime: 2 * 60 * 1000, // 2 minutes for inventory data
    refetchOnWindowFocus: false,
    placeholderData: keepPreviousData
  });
};