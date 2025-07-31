import { useMutation, useQueryClient } from '@tanstack/react-query';
import { shoesApi } from '@api';
import { Shoe } from '@types';

export const useCreateShoe = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (shoe: Omit<Shoe, 'id'>) => shoesApi.createShoe(shoe),
    onSuccess: () => {
      // Invalidate shoes list to refetch
      queryClient.invalidateQueries({ queryKey: ['shoes'] });
    },
  });
};
