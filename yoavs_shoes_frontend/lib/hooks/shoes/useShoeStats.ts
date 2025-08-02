import { shoesApi } from "@api";
import { useQuery } from "@tanstack/react-query";

export const useShoeStats = () => {
  return useQuery({
    queryKey: ['shoe-stats'],
    queryFn: () => shoesApi.getShoeStats(),
    staleTime: 5 * 60 * 1000, // 5 minutes
    refetchOnWindowFocus: false,
  });
};