'use client'

import React, { createContext, useContext, ReactNode } from 'react';
import { User } from '@types';
import { useProfile } from '@hooks';

interface UserContextType {
  user: User | undefined;
  isLoading: boolean;
  isError: boolean;
  refetch: () => void;
}

const UserContext = createContext<UserContextType | undefined>(undefined);

interface UserProviderProps {
  children: ReactNode;
}

export const UserProvider: React.FC<UserProviderProps> = ({ children }) => {
  const { data: user, isLoading, isError, refetch } = useProfile();

  const value: UserContextType = {
    user,
    isLoading,
    isError,
    refetch,
  };

  return <UserContext.Provider value={value}>{children}</UserContext.Provider>;
};

export const useUser = (): UserContextType => {
  const context = useContext(UserContext);
  if (context === undefined) {
    throw new Error('useUser must be used within a UserProvider');
  }
  return context;
};
