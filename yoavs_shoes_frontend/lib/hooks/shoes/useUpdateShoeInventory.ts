/* eslint-disable @typescript-eslint/no-unused-vars */
import { useToast } from '@/components/Toast';
import { shoeInventoryApi } from '@api';
import { useQueryClient, useMutation } from '@tanstack/react-query';
import {
  PageResponse,
  ShoeInventoryView,
  ShoeModelInventoryView,
  UpdateShoeInventoryRequest,
} from '@types';

export const useUpdateShoeInventory = () => {
  const queryClient = useQueryClient();
  const { showToast } = useToast();

  return useMutation({
    mutationFn: ({
      inventoryId,
      modelId,
      shoeId,
      quantityAvailable,
      quantityReserved,
    }: UpdateShoeInventoryRequest & {
      inventoryId: number;
      modelId: number;
      shoeId: number;
    }) =>
      shoeInventoryApi.updateInventory(inventoryId, {
        quantityAvailable,
        quantityReserved,
      }),
    onSuccess: async (data, variables) => {
      const oldQuantity =
        queryClient
          .getQueryData<ShoeModelInventoryView[]>([
            'shoe-models',
            variables.shoeId,
          ])
          ?.find(model => model.id === variables.modelId)
          ?.availableSizes.find(size => size.id === variables.inventoryId)
          ?.quantityAvailable || 0;

      // Optimistically update cache for the specific shoe
      queryClient.setQueryData(
        ['shoe-models', variables.shoeId],
        (old: ShoeModelInventoryView[]) => {
          if (!old) return old;
          return old.map(model => {
            if (model.id === variables.modelId) {
              return {
                ...model,
                availableSizes: model.availableSizes.map(sizeItem =>
                  sizeItem.id === variables.inventoryId
                    ? {
                        ...sizeItem,
                        quantityAvailable: variables.quantityAvailable,
                      }
                    : sizeItem
                ),
              };
            }
            return model;
          });
        }
      );

      const queries = queryClient.getQueriesData<
        PageResponse<ShoeInventoryView>
      >({ queryKey: ['shoes', 'inventory'] });
      queries.forEach(([queryKey, oldData]) => {
        if (!oldData) return;

        queryClient.setQueryData<PageResponse<ShoeInventoryView>>(queryKey, {
          ...oldData,
          content: oldData.content.map(shoe => {
            if (shoe.id === variables.shoeId) {
              return {
                ...shoe,
                totalStock:
                  shoe.totalStock - oldQuantity + variables.quantityAvailable,
              };
            }
            return shoe;
          }),
        });
        console.log('Updating shoe inventory cache', oldData, variables);
      });

      return queryClient.invalidateQueries({ queryKey: ['shoe-stats'] });
    },
    onError: () => {
      showToast('Error updating shoe inventory', 'error');
    },
  });
};
