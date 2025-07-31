import { useQuery } from '@tanstack/react-query';
import { authApi } from '@api';
import { User } from '@types';

export const useProfile = () => {
  const token =
    typeof window !== 'undefined' ? localStorage.getItem('authToken') : null;

  return useQuery<User>({
    queryKey: ['user', 'profile'],
    queryFn: () => authApi.getProfile(),
    enabled: !!token,
    retry: false,
    staleTime: 5 * 60 * 1000, // 5 minutes - cache the user data
    gcTime: 10 * 60 * 1000, // 10 minutes - keep in cache
  });
};
