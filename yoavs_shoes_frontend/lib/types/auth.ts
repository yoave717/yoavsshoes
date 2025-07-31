import { StandardResponse } from './common';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  confirmPassword: string;
  phoneNumber?: string;
}

// User type matching backend AuthResponse.UserInfo
export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  fullName: string;
  phoneNumber?: string;
  isAdmin: boolean;
  isActive: boolean;
  createdAt: string;
  lastLogin: string;
}

// Auth response data matching backend AuthResponse
export interface AuthResponseData {
  token: string;
  tokenType: string;
  expiresAt: string;
  user: User;
  message: string;
}

export type AuthResponse = StandardResponse<AuthResponseData>;