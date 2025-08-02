'use client';

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useForm } from 'react-hook-form';
import { authAPI } from '@/lib/api';

interface RegisterForm {
  name: string;
  email: string;
  password: string;
  confirmPassword: string;
  agreeTerms: boolean;
}

export default function RegisterPage() {
  const router = useRouter();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
    watch
  } = useForm<RegisterForm>();

  const password = watch('password');

  const onSubmit = async (data: RegisterForm) => {
    if (data.password !== data.confirmPassword) {
      setError('비밀번호가 일치하지 않습니다.');
      return;
    }

    if (!data.agreeTerms) {
      setError('이용약관 및 개인정보처리방침에 동의해주세요.');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const response = await authAPI.register(data.email, data.password, data.name);
      
      if (response.data.success) {
        setSuccess(true);
        setTimeout(() => {
          router.push('/login');
        }, 2000);
      } else {
        setError(response.data.message || '회원가입에 실패했습니다.');
      }
    } catch (err) {
      const errorMessage = err instanceof Error && 'response' in err 
        ? (err as { response?: { data?: { message?: string } } }).response?.data?.message 
        : '회원가입 중 오류가 발생했습니다.';
      setError(errorMessage || '회원가입 중 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="max-w-md w-full text-center">
          <div className="bg-white rounded-lg shadow-md p-8">
            <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <span className="text-green-600 text-2xl">✓</span>
            </div>
            <h2 className="text-2xl font-bold text-gray-900 mb-2">
              회원가입 완료!
            </h2>
            <p className="text-gray-600 mb-4">
              계정이 성공적으로 생성되었습니다.
            </p>
            <p className="text-sm text-gray-500">
              잠시 후 로그인 페이지로 이동합니다...
            </p>
          </div>
        </div>
      </div>
    );
  }

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
            계정 만들기
          </h2>
          <p className="mt-2 text-sm text-gray-600">
            새 계정을 만들어 서비스를 이용하세요
          </p>
        </div>

        <div className="mt-8 space-y-6">
          {error && (
            <div className="bg-red-50 border border-red-200 rounded-md p-3">
              <p className="text-sm text-red-600">{error}</p>
            </div>
          )}

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div>
              <label htmlFor="name" className="block text-sm font-medium text-gray-700 mb-1">
                이름
              </label>
              <input
                {...register('name', {
                  required: '이름을 입력해주세요',
                  minLength: {
                    value: 2,
                    message: '이름은 2자 이상이어야 합니다'
                  },
                  maxLength: {
                    value: 50,
                    message: '이름은 50자 이하여야 합니다'
                  }
                })}
                type="text"
                className="appearance-none rounded-lg relative block w-full px-3 py-3 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="홍길동"
              />
              {errors.name && (
                <p className="mt-1 text-sm text-red-600">{errors.name.message}</p>
              )}
            </div>

            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
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
                placeholder="example@email.com"
              />
              {errors.email && (
                <p className="mt-1 text-sm text-red-600">{errors.email.message}</p>
              )}
            </div>

            <div>
              <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-1">
                비밀번호
              </label>
              <input
                {...register('password', {
                  required: '비밀번호를 입력해주세요',
                  minLength: {
                    value: 8,
                    message: '비밀번호는 8자 이상이어야 합니다'
                  },
                  pattern: {
                    value: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]/,
                    message: '비밀번호는 대소문자, 숫자, 특수문자를 포함해야 합니다'
                  }
                })}
                type="password"
                className="appearance-none rounded-lg relative block w-full px-3 py-3 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="비밀번호 (8자 이상)"
              />
              {errors.password && (
                <p className="mt-1 text-sm text-red-600">{errors.password.message}</p>
              )}
            </div>

            <div>
              <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700 mb-1">
                비밀번호 확인
              </label>
              <input
                {...register('confirmPassword', {
                  required: '비밀번호 확인을 입력해주세요',
                  validate: (value) => {
                    if (value !== password) {
                      return '비밀번호가 일치하지 않습니다';
                    }
                  }
                })}
                type="password"
                className="appearance-none rounded-lg relative block w-full px-3 py-3 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="비밀번호 확인"
              />
              {errors.confirmPassword && (
                <p className="mt-1 text-sm text-red-600">{errors.confirmPassword.message}</p>
              )}
            </div>

            <div className="flex items-start">
              <div className="flex items-center h-5">
                <input
                  {...register('agreeTerms', {
                    required: '이용약관 및 개인정보처리방침에 동의해주세요'
                  })}
                  type="checkbox"
                  className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                />
              </div>
              <div className="ml-3 text-sm">
                <label htmlFor="agreeTerms" className="text-gray-700">
                  <span className="text-blue-600 hover:text-blue-500 cursor-pointer">이용약관</span> 및{' '}
                  <span className="text-blue-600 hover:text-blue-500 cursor-pointer">개인정보처리방침</span>에 동의합니다
                </label>
                {errors.agreeTerms && (
                  <p className="mt-1 text-sm text-red-600">{errors.agreeTerms.message}</p>
                )}
              </div>
            </div>

            <div>
              <button
                type="submit"
                disabled={loading}
                className="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-medium rounded-lg text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:bg-blue-400 transition-colors"
              >
                {loading ? '계정 생성 중...' : '계정 만들기'}
              </button>
            </div>
          </form>

          <div className="text-center">
            <span className="text-sm text-gray-600">이미 계정이 있으신가요? </span>
            <button
              onClick={() => router.push('/login')}
              className="text-sm text-blue-600 hover:text-blue-500 font-medium"
            >
              로그인하기
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}