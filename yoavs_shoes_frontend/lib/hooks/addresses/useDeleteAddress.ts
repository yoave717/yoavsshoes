import { useMutation, useQueryClient } from '@tanstack/react-query';
import { addressApi } from '@api';

export const useDeleteAddress = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => addressApi.deleteAddress(id),
    onSuccess: () => {
      // Invalidate addresses list to refetch
      queryClient.invalidateQueries({ queryKey: ['addresses'] });
      queryClient.invalidateQueries({ queryKey: ['addresses', 'default'] });
    },
  });
};
