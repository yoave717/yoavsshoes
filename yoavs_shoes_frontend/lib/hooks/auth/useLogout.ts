import { useMutation, useQueryClient } from '@tanstack/react-query';
import { authApi } from '@api';

export const useLogout = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => authApi.logout(),
    onSuccess: () => {
      // Remove token from localStorage
      localStorage.removeItem('authToken');
      // Invalidate user profile query specifically to trigger re-renders
      queryClient.invalidateQueries({ queryKey: ['user', 'profile'] });
      // Remove user data from cache
      queryClient.removeQueries({ queryKey: ['user'] });
    },
  });
};
