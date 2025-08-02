import { shoesApi } from "@api";
import { UseQueryResult, useQuery } from "@tanstack/react-query";
import { ShoeModelInventoryView } from "@types";

export const useShoeModels = (shoeId: number ): UseQueryResult<ShoeModelInventoryView[], Error> => {
  return useQuery({
    queryKey: ['shoe-models', shoeId],
    queryFn: () => shoesApi.getShoeModels(shoeId),
    enabled: !!shoeId,
    staleTime: 5 * 60 * 1000, // 5 minutes
    refetchOnWindowFocus: false,
    // calculate total stock for each model
    select: (data) => {
      return data.map(model => ({
        ...model,
        totalStock: model.availableSizes.reduce((total, size) => total + size.quantityAvailable, 0)
      }));
    }
  });
};
