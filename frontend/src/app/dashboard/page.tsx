'use client';

import React, { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { userAPI, authAPI } from '@/lib/api';
import { UserProfile } from '@/types/api';
import { handleApiError } from '@/utils/errorHandler';
import { ErrorMessage } from '@/components/ErrorMessage';

export default function DashboardPage() {
  const router = useRouter();
  const [user, setUser] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      router.push('/login');
      return;
    }

    fetchUserProfile();
  }, [router]);

  const fetchUserProfile = async () => {
    try {
      const response = await userAPI.getProfile();
      setUser(response.data);
    } catch (err) {
      const errorInfo = handleApiError(err);
      setError(errorInfo.message);
      console.error('Profile fetch error:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = async () => {
    try {
      if (user?.id) {
        await authAPI.logout(user.id);
      }
    } catch {
      console.warn('Logout API failed');
      // 로그아웃 API 실패는 무시하고 로컬 로그아웃 진행
    } finally {
      // 로컬 스토리지에서 토큰 제거
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      router.push('/login');
    }
  };

  const getTokenInfo = () => {
    const accessToken = localStorage.getItem('accessToken');
    const refreshToken = localStorage.getItem('refreshToken');
    
    if (!accessToken) return null;

    try {
      // JWT 페이로드 디코딩 (간단한 base64 디코딩)
      const payload = JSON.parse(atob(accessToken.split('.')[1]));
      return {
        issuedAt: new Date(payload.iat * 1000).toLocaleString('ko-KR'),
        expiresAt: new Date(payload.exp * 1000).toLocaleString('ko-KR'),
        hasRefreshToken: !!refreshToken
      };
    } catch {
      return null;
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  const tokenInfo = getTokenInfo();

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-6">
            <div className="flex items-center">
              <div className="w-10 h-10 bg-blue-600 rounded-full flex items-center justify-center">
                <span className="text-white font-bold">A</span>
              </div>
              <h1 className="ml-3 text-2xl font-bold text-gray-900">
                Auth Practice
              </h1>
            </div>
            <button
              onClick={handleLogout}
              className="bg-red-600 hover:bg-red-700 text-white font-medium py-2 px-4 rounded-lg transition-colors"
            >
              로그아웃
            </button>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* User Profile Card */}
            <div className="bg-white overflow-hidden shadow rounded-lg">
              <div className="px-4 py-5 sm:p-6">
                <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
                  사용자 정보
                </h3>
                {error && (
                  <ErrorMessage error={error} className="mb-4" />
                )}
                {user && (
                  <dl className="grid grid-cols-1 gap-x-4 gap-y-6 sm:grid-cols-2">
                    <div>
                      <dt className="text-sm font-medium text-gray-500">이름</dt>
                      <dd className="mt-1 text-sm text-gray-900">{user.name}</dd>
                    </div>
                    <div>
                      <dt className="text-sm font-medium text-gray-500">이메일</dt>
                      <dd className="mt-1 text-sm text-gray-900">{user.email}</dd>
                    </div>
                    <div>
                      <dt className="text-sm font-medium text-gray-500">사용자 ID</dt>
                      <dd className="mt-1 text-sm text-gray-900">{user.id}</dd>
                    </div>
                    <div>
                      <dt className="text-sm font-medium text-gray-500">계정 상태</dt>
                      <dd className="mt-1">
                        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                          user.isActive 
                            ? 'bg-green-100 text-green-800' 
                            : 'bg-red-100 text-red-800'
                        }`}>
                          {user.isActive ? '활성' : '비활성'}
                        </span>
                      </dd>
                    </div>
                    <div>
                      <dt className="text-sm font-medium text-gray-500">권한</dt>
                      <dd className="mt-1">
                        <div className="flex flex-wrap gap-1">
                          {user.roles.map((role, index) => (
                            <span
                              key={index}
                              className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800"
                            >
                              {role}
                            </span>
                          ))}
                        </div>
                      </dd>
                    </div>
                    <div>
                      <dt className="text-sm font-medium text-gray-500">가입일</dt>
                      <dd className="mt-1 text-sm text-gray-900">
                        {new Date(user.createdAt).toLocaleDateString('ko-KR')}
                      </dd>
                    </div>
                  </dl>
                )}
              </div>
            </div>

            {/* Token Status Card */}
            <div className="bg-white overflow-hidden shadow rounded-lg">
              <div className="px-4 py-5 sm:p-6">
                <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
                  토큰 상태
                </h3>
                {tokenInfo ? (
                  <dl className="grid grid-cols-1 gap-x-4 gap-y-6">
                    <div>
                      <dt className="text-sm font-medium text-gray-500">Access Token 발급일</dt>
                      <dd className="mt-1 text-sm text-gray-900 font-mono">{tokenInfo.issuedAt}</dd>
                    </div>
                    <div>
                      <dt className="text-sm font-medium text-gray-500">Access Token 만료일</dt>
                      <dd className="mt-1 text-sm text-gray-900 font-mono">{tokenInfo.expiresAt}</dd>
                    </div>
                    <div>
                      <dt className="text-sm font-medium text-gray-500">Refresh Token</dt>
                      <dd className="mt-1">
                        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                          tokenInfo.hasRefreshToken 
                            ? 'bg-green-100 text-green-800' 
                            : 'bg-red-100 text-red-800'
                        }`}>
                          {tokenInfo.hasRefreshToken ? '보유' : '없음'}
                        </span>
                      </dd>
                    </div>
                  </dl>
                ) : (
                  <p className="text-sm text-gray-500">토큰 정보를 불러올 수 없습니다.</p>
                )}
              </div>
            </div>
          </div>

          {/* Actions */}
          <div className="mt-6">
            <div className="bg-white shadow rounded-lg">
              <div className="px-4 py-5 sm:p-6">
                <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
                  계정 관리
                </h3>
                <div className="flex flex-wrap gap-3">
                  <button
                    onClick={() => router.push('/profile')}
                    className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-lg transition-colors"
                  >
                    프로필 수정
                  </button>
                  <button
                    onClick={() => router.push('/change-password')}
                    className="bg-green-600 hover:bg-green-700 text-white font-medium py-2 px-4 rounded-lg transition-colors"
                  >
                    비밀번호 변경
                  </button>
                  <button
                    onClick={() => {
                      if (confirm('정말로 계정을 비활성화하시겠습니까?')) {
                        // 계정 비활성화 로직 추가
                      }
                    }}
                    className="bg-gray-600 hover:bg-gray-700 text-white font-medium py-2 px-4 rounded-lg transition-colors"
                  >
                    계정 비활성화
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}