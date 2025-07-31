import apiClient from './client';
import { StandardResponse, PageResponse } from '../types/common';
import { User, UserSearchFilters, UserStats } from '../types/user';

// User Management APIs
export const getAllUsers = async (
  page = 0, 
  size = 20, 
  sortBy = 'id', 
  sortDir = 'desc'
): Promise<StandardResponse<PageResponse<User>>> => {
  const response = await apiClient.get(`/users/all?page=${page}&size=${size}&sortBy=${sortBy}&sortDir=${sortDir}`);
  return response.data;
};

export const searchUsers = async (
  filters: UserSearchFilters,
  page = 0,
  size = 20
): Promise<StandardResponse<PageResponse<User>>> => {
  const params = new URLSearchParams({
    page: page.toString(),
    size: size.toString(),
    ...Object.fromEntries(
      Object.entries(filters).filter(([, value]) => value !== undefined).map(([key, value]) => [key, value.toString()])
    )
  });
  
  const response = await apiClient.get(`/users/search?${params}`);
  return response.data;
};

export const toggleUserStatus = async (userId: number): Promise<StandardResponse<User>> => {
  const response = await apiClient.put(`/users/${userId}/toggle-status`);
  return response.data;
};

export const getUserStats = async (): Promise<StandardResponse<UserStats>> => {
  const response = await apiClient.get('/users/stats');
  return response.data;
};
