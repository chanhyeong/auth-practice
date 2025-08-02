'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { userAPI } from '@/lib/api';

interface User {
  id: number;
  email: string;
  name: string;
  profileImageUrl?: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
  roles: string[];
}

export function useAuth() {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const router = useRouter();

  useEffect(() => {
    checkAuthStatus();
  }, []);

  const checkAuthStatus = async () => {
    const token = localStorage.getItem('accessToken');
    
    if (!token) {
      setLoading(false);
      setIsAuthenticated(false);
      return;
    }

    try {
      const response = await userAPI.getProfile();
      setUser(response.data);
      setIsAuthenticated(true);
    } catch {
      // 토큰이 유효하지 않은 경우
      logout();
    } finally {
      setLoading(false);
    }
  };

  const login = (accessToken: string, refreshToken: string) => {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    setIsAuthenticated(true);
    checkAuthStatus(); // 사용자 정보 다시 로드
  };

  const logout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    setUser(null);
    setIsAuthenticated(false);
    router.push('/login');
  };

  const updateUser = (updatedUser: User) => {
    setUser(updatedUser);
  };

  return {
    user,
    loading,
    isAuthenticated,
    login,
    logout,
    updateUser,
    checkAuthStatus
  };
}