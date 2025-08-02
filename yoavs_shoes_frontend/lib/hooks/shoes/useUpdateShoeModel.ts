import { useToast } from '@/components/Toast';
import { shoeModelsApi } from '@api';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { ShoeModel, UpdateShoeModelRequest } from '@types';

export const useUpdateShoeModel = () => {
  const queryClient = useQueryClient();
  const { showToast } = useToast();

  return useMutation({
    mutationFn: ({
      id,
      shoeModel,
    }: {
      id: number;
      shoeModel: UpdateShoeModelRequest;
    }) => shoeModelsApi.updateShoeModel(id, shoeModel),
    onSuccess: data => {
      // Update the specific shoe model in cache
      queryClient.setQueryData(
        ['shoe-models', data.shoeId],
        (old: ShoeModel[]) => {
          if (!old) return old;
          const index = old.findIndex(model => model.id === data.id);
          if (index === -1) return old;
          const updatedModels = [...old];
          updatedModels[index] = data;
          return updatedModels;
        }
      );

      showToast('Shoe model updated successfully', 'success');
    },
  });
};
