import { useMutation, useQueryClient } from '@tanstack/react-query';
import { shoesApi } from '@api';
import { Shoe } from '@types';

export const useUpdateShoe = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, shoe }: { id: number; shoe: Partial<Shoe> }) =>
      shoesApi.updateShoe(id, shoe),
    onSuccess: (data, variables) => {
      // Update the specific shoe in cache
      queryClient.setQueryData(['shoes', variables.id], data);
      // Invalidate shoes list
      queryClient.invalidateQueries({ queryKey: ['shoes'] });
    },
  });
};
