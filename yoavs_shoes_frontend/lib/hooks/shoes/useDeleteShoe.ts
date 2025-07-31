import { useMutation, useQueryClient } from '@tanstack/react-query';
import { shoesApi } from '@api';

export const useDeleteShoe = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => shoesApi.deleteShoe(id),
    onSuccess: () => {
      // Invalidate shoes list to refetch
      queryClient.invalidateQueries({ queryKey: ['shoes'] });
    },
  });
};
