import { useQuery } from '@tanstack/react-query';
import { authApi } from '../../api';

export const useAuth = () => {
  const token = typeof window !== 'undefined' ? localStorage.getItem('authToken') : null;

  const { data, isLoading, error } = useQuery({
    queryKey: ['user', 'profile', token],
    queryFn: () => authApi.getProfile(),
    enabled: !!token,
    retry: false,
    staleTime: 0,
  });

  return {
    user: data,
    isLoading,
    error,
    isAuthenticated: !!data,
  };
};
