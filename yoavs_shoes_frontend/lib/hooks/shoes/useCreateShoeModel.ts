import { shoeModelsApi } from '@/lib/api';
import { CreateShoeModelRequest } from '@/lib/types';
import { useMutation, useQueryClient } from '@tanstack/react-query';

export const useCreateShoeModel = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (newShoeModel: CreateShoeModelRequest) =>
      shoeModelsApi.createShoeModel(newShoeModel),
    onSuccess: data => {
      queryClient.invalidateQueries({ queryKey: ['shoe-models', data.id] });
      queryClient.invalidateQueries({ queryKey: ['shoes'] });
      queryClient.invalidateQueries({ queryKey: ['shoes', 'inventory'] });
      queryClient.invalidateQueries({ queryKey: ['shoe-stats'] });
    },
    onError: error => {
      console.error('Error creating shoe model:', error);
    },
  });
};
