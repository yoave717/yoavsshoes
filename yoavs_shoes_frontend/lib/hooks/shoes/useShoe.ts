import { useQuery } from '@tanstack/react-query';
import { shoesApi } from '@api';

export const useShoe = (id: number) => {
  return useQuery({
    queryKey: ['shoes', id],
    queryFn: () => shoesApi.getShoe(id),
    enabled: !!id,
  });
};
