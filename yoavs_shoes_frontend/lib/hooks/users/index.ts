import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  getAllUsers,
  searchUsers,
  toggleUserStatus,
  getUserStats
} from '../../api/users';

// User Management Hooks
export const useUsers = (page = 0, size = 20, sortBy = 'id', sortDir = 'desc') => {
  return useQuery({
    queryKey: ['users', 'all', page, size, sortBy, sortDir],
    queryFn: () => getAllUsers(page, size, sortBy, sortDir),
  });
};

export const useSearchUsers = (filters: {
  email?: string;
  firstName?: string;
  lastName?: string;
  isAdmin?: boolean;
  isActive?: boolean;
}, page = 0, size = 20) => {
  return useQuery({
    queryKey: ['users', 'search', filters, page, size],
    queryFn: () => searchUsers(filters, page, size),
    enabled: Object.values(filters).some(value => value !== undefined && value !== ''),
  });
};

export const useToggleUserStatus = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (userId: number) => toggleUserStatus(userId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users'] });
    },
  });
};

export const useUserStats = () => {
  return useQuery({
    queryKey: ['users', 'stats'],
    queryFn: getUserStats,
  });
};
