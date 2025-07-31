import { useQuery } from '@tanstack/react-query';
import { addressApi } from '@api';

export const useUserAddresses = () => {
  return useQuery({
    queryKey: ['user-addresses'],
    queryFn: async () => {
      const response = await addressApi.getCurrentUserAddresses(0, 100);
      return response.data;
    },
    staleTime: 1000 * 60 * 5, // 5 minutes
  });
};
