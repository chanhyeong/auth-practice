'use client';

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useForm } from 'react-hook-form';
import { authAPI } from '@/lib/api';
import { handleApiError } from '@/utils/errorHandler';
import { ErrorMessage } from '@/components/ErrorMessage';

interface LoginForm {
  email: string;
  password: string;
}

export default function LoginPage() {
  const router = useRouter();
  const [currentStep, setCurrentStep] = useState<'email' | 'password'>('email');
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const {
    register,
    handleSubmit,
    formState: { errors },
    watch
  } = useForm<LoginForm>();

  const watchEmail = watch('email');

  const handleEmailNext = () => {
    if (watchEmail && /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(watchEmail)) {
      setEmail(watchEmail);
      setCurrentStep('password');
      setError('');
    }
  };

  const handlePasswordSubmit = async (data: LoginForm) => {
    setLoading(true);
    setError('');

    try {
      const response = await authAPI.login(email, data.password);
      
      if (response.data.success && response.data.requiresOtp) {
        // OTP 인증이 필요한 경우
        router.push(`/otp?userId=${response.data.userId}&otpCode=${response.data.otpCode}`);
      } else if (!response.data.success) {
        setError(response.data.message || '로그인에 실패했습니다.');
      }
    } catch (err) {
      const errorInfo = handleApiError(err);
      setError(errorInfo.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div className="text-center">
          <div className="flex justify-center mb-8">
            <div className="w-24 h-24 bg-blue-600 rounded-full flex items-center justify-center">
              <span className="text-white text-3xl font-bold">A</span>
            </div>
          </div>
          <h2 className="text-2xl font-normal text-gray-900">
            {currentStep === 'email' ? '이메일을 입력하세요' : '비밀번호를 입력하세요'}
          </h2>
          {currentStep === 'password' && (
            <p className="mt-2 text-sm text-gray-600">{email}</p>
          )}
        </div>

        <div className="mt-8 space-y-6">
          {error && (
            <ErrorMessage error={error} />
          )}

          {currentStep === 'email' ? (
            <form onSubmit={(e) => { e.preventDefault(); handleEmailNext(); }} className="space-y-4">
              <div>
                <label htmlFor="email" className="sr-only">
                  이메일
                </label>
                <input
                  {...register('email', {
                    required: '이메일을 입력해주세요',
                    pattern: {
                      value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                      message: '올바른 이메일 형식을 입력해주세요'
                    }
                  })}
                  type="email"
                  autoComplete="email"
                  className="appearance-none rounded-lg relative block w-full px-3 py-3 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="이메일 주소"
                />
                {errors.email && (
                  <p className="mt-1 text-sm text-red-600">{errors.email.message}</p>
                )}
              </div>

              <div>
                <button
                  type="submit"
                  disabled={!watchEmail || !!errors.email}
                  className="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-medium rounded-lg text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors"
                >
                  다음
                </button>
              </div>
            </form>
          ) : (
            <form onSubmit={handleSubmit(handlePasswordSubmit)} className="space-y-4">
              <div>
                <label htmlFor="password" className="sr-only">
                  비밀번호
                </label>
                <input
                  {...register('password', {
                    required: '비밀번호를 입력해주세요',
                    minLength: {
                      value: 8,
                      message: '비밀번호는 8자 이상이어야 합니다'
                    }
                  })}
                  type="password"
                  autoComplete="current-password"
                  className="appearance-none rounded-lg relative block w-full px-3 py-3 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="비밀번호"
                />
                {errors.password && (
                  <p className="mt-1 text-sm text-red-600">{errors.password.message}</p>
                )}
              </div>

              <div className="flex space-x-3">
                <button
                  type="button"
                  onClick={() => setCurrentStep('email')}
                  className="flex-1 py-3 px-4 border border-gray-300 text-sm font-medium rounded-lg text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors"
                >
                  이전
                </button>
                <button
                  type="submit"
                  disabled={loading}
                  className="flex-1 py-3 px-4 border border-transparent text-sm font-medium rounded-lg text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:bg-blue-400 transition-colors"
                >
                  {loading ? '로그인 중...' : '로그인'}
                </button>
              </div>
            </form>
          )}

          <div className="text-center space-y-2">
            <div>
              <button
                onClick={() => router.push('/register')}
                className="text-sm text-blue-600 hover:text-blue-500"
              >
                계정 만들기
              </button>
            </div>
            <div>
              <button className="text-sm text-blue-600 hover:text-blue-500">
                비밀번호를 잊으셨나요?
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}