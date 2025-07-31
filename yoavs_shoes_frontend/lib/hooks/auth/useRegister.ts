import { useMutation, useQueryClient } from '@tanstack/react-query';
import { authApi } from '@api';
import { RegisterRequest } from '@types';

export const useRegister = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (userData: RegisterRequest) => authApi.register(userData),
    onSuccess: (data) => {
      // Store token in localStorage

      localStorage.setItem('authToken', data.token);
      // Invalidate and refetch user data
      queryClient.invalidateQueries({ queryKey: ['user'] });
    },
  });
};
