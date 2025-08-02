import { shoeModelsApi } from '@/lib/api';
import { CreateShoeModelRequest, ShoeInventoryView, ShoeModel } from '@/lib/types';
import { useMutation, useQueryClient } from '@tanstack/react-query';

export const useCreateShoeModel = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (newShoeModel: CreateShoeModelRequest) =>
      shoeModelsApi.createShoeModel(newShoeModel),
    onSuccess: data => {
      queryClient.setQueryData(['shoe-models', data.shoeId], (old: ShoeModel[]) => {
        return [...(old || []), data];
      });
      queryClient.setQueryData(['shoes', 'inventory'], (old: ShoeInventoryView[]) => {
        return old?.map(shoe => {
          if (shoe.id === data.shoeId) {
            return { ...shoe, modelCount: (shoe.modelCount || 0) + 1 };
          }
          return shoe;
        });
      });

      return queryClient.invalidateQueries({ queryKey: ['shoe-stats'] });
    },
    onError: error => {
      console.error('Error creating shoe model:', error);
    },
  });
};
