import { useQuery } from '@tanstack/react-query';
import { addressApi } from '@api';

export const useDefaultAddress = () => {
  return useQuery({
    queryKey: ['addresses', 'default'],
    queryFn: async () => {
      const response = await addressApi.getDefaultAddress();
      return response.data;
    },
    staleTime: 1000 * 60 * 5,
    retry: false, // Don't retry if no default address exists
  });
};
