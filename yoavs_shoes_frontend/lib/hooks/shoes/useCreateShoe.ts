import { useMutation, useQueryClient } from '@tanstack/react-query';
import { shoesApi } from '@api';
import { CreateShoeRequest, Shoe } from '@types';

export const useCreateShoe = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (shoe: CreateShoeRequest) => shoesApi.createShoe(shoe),
    onSuccess: () => {
      // Invalidate shoes list to refetch
      queryClient.invalidateQueries({ queryKey: ['shoes', 'inventory'] });
      queryClient.invalidateQueries({ queryKey: ['shoe-stats'] });
    },
  });
};
