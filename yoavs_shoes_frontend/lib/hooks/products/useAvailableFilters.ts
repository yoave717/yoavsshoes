import { getAvailableFilters } from '@api';
import { useQuery } from '@tanstack/react-query';

export const useAvailableFilters = () => {
  return useQuery({
    queryKey: ['products', 'filters'],
    queryFn: () => getAvailableFilters(),
    staleTime: 1000 * 60 * 10, // 10 minutes
  });
};
