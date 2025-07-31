import { useQuery } from '@tanstack/react-query';
import { shoesApi } from '@api';

export const useShoes = () => {
  return useQuery({
    queryKey: ['shoes'],
    queryFn: () => shoesApi.getShoes(),
  });
};
