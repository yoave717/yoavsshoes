import { useMutation, useQueryClient } from '@tanstack/react-query';
import { addressApi } from '@api';
import { AddressRequest } from '@types';

export const useCreateAddress = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (address: AddressRequest) => addressApi.createAddress(address),
    onSuccess: (response) => {
      // Invalidate addresses list to refetch
      queryClient.invalidateQueries({ queryKey: ['addresses'] });
      
      // If this address was set as default, invalidate default address
      if (response.data.isDefault) {
        queryClient.invalidateQueries({ queryKey: ['addresses', 'default'] });
      }
    },
  });
};
