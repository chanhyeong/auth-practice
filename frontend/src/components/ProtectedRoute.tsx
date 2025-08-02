'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';

interface ProtectedRouteProps {
  children: React.ReactNode;
  requireAuth?: boolean;
}

export default function ProtectedRoute({ 
  children, 
  requireAuth = true 
}: ProtectedRouteProps) {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    const checkAuth = () => {
      const token = localStorage.getItem('accessToken');
      const isLoggedIn = !!token;

      setIsAuthenticated(isLoggedIn);

      if (requireAuth && !isLoggedIn) {
        router.push('/login');
        return;
      }

      if (!requireAuth && isLoggedIn) {
        router.push('/dashboard');
        return;
      }

      setIsLoading(false);
    };

    checkAuth();
  }, [router, requireAuth]);

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  // requireAuth가 true인데 인증되지 않은 경우, 또는
  // requireAuth가 false인데 인증된 경우는 리다이렉트 처리됨
  if ((requireAuth && !isAuthenticated) || (!requireAuth && isAuthenticated)) {
    return null;
  }

  return <>{children}</>;
}