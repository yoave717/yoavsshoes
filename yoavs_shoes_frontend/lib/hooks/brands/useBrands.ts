import { brandsApi } from "@api";
import { useQuery } from "@tanstack/react-query";

export const useBrands = () => {
  return useQuery({
    queryKey: ['brands'],
    queryFn: () => brandsApi.getBrands(),
    staleTime: 5 * 60 * 1000, // 5 minutes
    refetchOnWindowFocus: false,
  });
}