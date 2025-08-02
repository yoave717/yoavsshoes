import { shoeInventoryApi } from "@api";
import { useQueryClient, useMutation } from "@tanstack/react-query";
import { ShoeModel } from "@types";

export const useUpdateShoeInventory = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({modelId, size, quantityAvailable, quantityReserved, shoeId }: { modelId: number; size: string; quantityAvailable: number; quantityReserved?: number; shoeId: number }) =>
      shoeInventoryApi.updateInventory(modelId, size, quantityAvailable, quantityReserved),
    onMutate: async (variables) => {
      // Cancel outgoing refetches for the specific shoe
      await queryClient.cancelQueries({ queryKey: ['shoe-models', variables.shoeId] });
      
      // Snapshot previous value
      const previousData = queryClient.getQueryData(['shoe-models', variables.shoeId]);
      
      // Optimistically update cache for the specific shoe
      queryClient.setQueryData(['shoe-models', variables.shoeId], (old: ShoeModel[]) => {
        if (!old) return old;
        return old.map((model) => {
          if (model.id === variables.modelId) {
            return {
              ...model,
              availableSizes: model.availableSizes.map((sizeItem) => 
                sizeItem.size === variables.size 
                  ? { ...sizeItem, quantityAvailable: variables.quantityAvailable }
                  : sizeItem
              )
            };
          }
          return model;
        });
      });
      queryClient.invalidateQueries({ queryKey: ['shoe-stats'] });

      return { previousData, shoeId: variables.shoeId };
    },
    onError: (err, variables, context) => {
      // Rollback on error
      if (context?.previousData && context?.shoeId) {
        queryClient.setQueryData(['shoe-models', context.shoeId], context.previousData);
      }
    },
    onSettled: (data, error, variables) => {
      // Always refetch after error or success
      queryClient.invalidateQueries({ queryKey: ['shoe-models', variables.shoeId] });
      queryClient.invalidateQueries({ queryKey: ['shoes', 'inventory'] });
      queryClient.invalidateQueries({ queryKey: ['shoe-stats'] });
    }
  });
}