import { useMutation, useQueryClient } from '@tanstack/react-query';
import { authApi } from '@api';
import { LoginRequest } from '@types';

export const useLogin = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (credentials: LoginRequest) => authApi.login(credentials),
    onSuccess: (data) => {
      // Store token in localStorage

      localStorage.setItem('authToken', data.token);
      // Invalidate and refetch user data
      queryClient.invalidateQueries({ queryKey: ['user'] });
    },
  });
};
