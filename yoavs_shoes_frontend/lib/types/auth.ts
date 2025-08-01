import { StandardResponse } from './common';
import { User } from './user';

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



// Auth response data matching backend AuthResponse
export interface AuthResponseData {
  token: string;
  tokenType: string;
  expiresAt: string;
  user: User;
  message: string;
}

export type AuthResponse = StandardResponse<AuthResponseData>;