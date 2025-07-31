import { useMutation, useQueryClient } from '@tanstack/react-query';
import { addressApi } from '@api';

export const useSetDefaultAddress = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => addressApi.setDefaultAddress(id),
    onSuccess: () => {
      // Invalidate addresses list and default address
      queryClient.invalidateQueries({ queryKey: ['addresses'] });
      queryClient.invalidateQueries({ queryKey: ['addresses', 'default'] });
    },
  });
};
