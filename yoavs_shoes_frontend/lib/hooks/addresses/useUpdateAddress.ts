import { useMutation, useQueryClient } from '@tanstack/react-query';
import { addressApi } from '@api';
import { AddressRequest } from '@types';

export const useUpdateAddress = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, address }: { id: number; address: AddressRequest }) =>
      addressApi.updateAddress(id, address),
    onSuccess: (response, variables) => {
      // Update the specific address in cache
      queryClient.setQueryData(['addresses', variables.id], response.data);
      // Invalidate addresses list
      queryClient.invalidateQueries({ queryKey: ['addresses'] });
      
      // If this address was set as default, invalidate default address
      if (response.data.isDefault) {
        queryClient.invalidateQueries({ queryKey: ['addresses', 'default'] });
      }
    },
  });
};
