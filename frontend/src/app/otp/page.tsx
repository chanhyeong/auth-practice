'use client';

import React, { useState, useEffect, Suspense } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { authAPI } from '@/lib/api';

function OtpPageContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [otp, setOtp] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [timeLeft, setTimeLeft] = useState(60);
  const [currentOtp, setCurrentOtp] = useState('');

  const userId = searchParams.get('userId');
  const otpCode = searchParams.get('otpCode');

  useEffect(() => {
    if (!userId) {
      router.push('/login');
      return;
    }

    if (otpCode) {
      setCurrentOtp(otpCode);
    }

    // 60초 카운트다운
    const timer = setInterval(() => {
      setTimeLeft((prev) => {
        if (prev <= 1) {
          clearInterval(timer);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [userId, otpCode, router]);

  const handleOtpSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (otp.length !== 6) {
      setError('OTP는 6자리 숫자여야 합니다.');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const response = await authAPI.verifyOtp(Number(userId), otp);
      
      if (response.data.success) {
        // 토큰 저장
        localStorage.setItem('accessToken', response.data.accessToken);
        localStorage.setItem('refreshToken', response.data.refreshToken);
        
        // 대시보드로 이동
        router.push('/dashboard');
      } else {
        setError(response.data.message || 'OTP 인증에 실패했습니다.');
      }
    } catch (err) {
      const errorMessage = err instanceof Error && 'response' in err 
        ? (err as { response?: { data?: { message?: string } } }).response?.data?.message 
        : 'OTP 인증 중 오류가 발생했습니다.';
      setError(errorMessage || 'OTP 인증 중 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateNewOtp = async () => {
    if (!userId) return;

    setLoading(true);
    setError('');

    try {
      const response = await authAPI.generateOtp(Number(userId));
      
      if (response.data.success) {
        setCurrentOtp(response.data.otpCode);
        setTimeLeft(60);
        setOtp('');
      } else {
        setError(response.data.message || '새 OTP 생성에 실패했습니다.');
      }
    } catch (err) {
      const errorMessage = err instanceof Error && 'response' in err 
        ? (err as { response?: { data?: { message?: string } } }).response?.data?.message 
        : '새 OTP 생성 중 오류가 발생했습니다.';
      setError(errorMessage || '새 OTP 생성 중 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div className="text-center">
          <div className="flex justify-center mb-8">
            <div className="w-24 h-24 bg-blue-600 rounded-full flex items-center justify-center">
              <span className="text-white text-3xl font-bold">🔐</span>
            </div>
          </div>
          <h2 className="text-2xl font-normal text-gray-900">
            2단계 인증
          </h2>
          <p className="mt-2 text-sm text-gray-600">
            OTP 코드를 입력하여 로그인을 완료하세요
          </p>
        </div>

        <div className="mt-8 space-y-6">
          {/* 개발용 OTP 표시 */}
          {currentOtp && (
            <div className="bg-yellow-50 border border-yellow-200 rounded-md p-4">
              <div className="flex">
                <div className="flex-shrink-0">
                  <span className="text-yellow-400">⚠️</span>
                </div>
                <div className="ml-3">
                  <h3 className="text-sm font-medium text-yellow-800">
                    개발용 OTP 코드
                  </h3>
                  <div className="mt-2 text-sm text-yellow-700">
                    <p className="font-mono text-lg font-bold">{currentOtp}</p>
                    <p className="text-xs mt-1">실제 운영에서는 SMS나 이메일로 전송됩니다.</p>
                  </div>
                </div>
              </div>
            </div>
          )}

          {error && (
            <div className="bg-red-50 border border-red-200 rounded-md p-3">
              <p className="text-sm text-red-600">{error}</p>
            </div>
          )}

          <form onSubmit={handleOtpSubmit} className="space-y-4">
            <div>
              <label htmlFor="otp" className="sr-only">
                OTP 코드
              </label>
              <input
                type="text"
                maxLength={6}
                value={otp}
                onChange={(e) => {
                  const value = e.target.value.replace(/\D/g, ''); // 숫자만 허용
                  setOtp(value);
                }}
                className="appearance-none rounded-lg relative block w-full px-3 py-3 border border-gray-300 placeholder-gray-500 text-gray-900 text-center text-2xl font-mono tracking-widest focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="000000"
              />
            </div>

            <div className="text-center">
              <p className="text-sm text-gray-600">
                남은 시간: <span className="font-mono font-bold text-red-600">{formatTime(timeLeft)}</span>
              </p>
            </div>

            <div className="space-y-3">
              <button
                type="submit"
                disabled={loading || otp.length !== 6}
                className="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-medium rounded-lg text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors"
              >
                {loading ? '인증 중...' : '로그인 완료'}
              </button>

              <button
                type="button"
                onClick={handleGenerateNewOtp}
                disabled={loading || timeLeft > 0}
                className="w-full py-2 px-4 border border-gray-300 text-sm font-medium rounded-lg text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:bg-gray-100 disabled:cursor-not-allowed transition-colors"
              >
                새 OTP 받기
              </button>

              <button
                type="button"
                onClick={() => router.push('/login')}
                className="w-full py-2 px-4 text-sm text-gray-600 hover:text-gray-800"
              >
                이전 단계로 돌아가기
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

export default function OtpPage() {
  return (
    <Suspense fallback={
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600"></div>
      </div>
    }>
      <OtpPageContent />
    </Suspense>
  );
}