import api from './client';
import { LoginRequest, RegisterRequest, User, AuthResponse } from '@types';
import { StandardResponse } from '@types';

// Auth API functions
export const authApi = {
  login: async (credentials: LoginRequest) => {
    const response = await api.post<AuthResponse>('/auth/login', credentials);
    return response.data.data;
  },

  register: async (userData: RegisterRequest) => {
    const response = await api.post<AuthResponse>('/auth/register', userData);
    return response.data.data;
  },

  logout: async (): Promise<void> => {
    await api.post('/auth/logout');
  },

  getProfile: async () => {
    const response = await api.get<StandardResponse<User>>('/auth/me');
    return response.data.data;
  },
};
