import axios from 'axios';
import { LoginResponse, OtpVerificationResponse, RegisterResponse, RefreshTokenResponse, GenerateOtpResponse, UserProfileResponse, UpdateUserResponse, ChangePasswordResponse } from '@/types/api';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle token refresh
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      const refreshToken = localStorage.getItem('refreshToken');
      if (refreshToken) {
        try {
          const response = await axios.post(`${API_BASE_URL}/auth/refresh`, {
            refreshToken,
          });

          const { accessToken } = response.data;
          localStorage.setItem('accessToken', accessToken);

          // Retry original request with new token
          originalRequest.headers.Authorization = `Bearer ${accessToken}`;
          return api(originalRequest);
        } catch {
          // Refresh failed, redirect to login
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
          window.location.href = '/login';
        }
      } else {
        // No refresh token, redirect to login
        localStorage.removeItem('accessToken');
        window.location.href = '/login';
      }
    }

    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: (email: string, password: string) =>
    api.post<LoginResponse>('/auth/login', { email, password }),
  
  verifyOtp: (userId: number, otpCode: string) =>
    api.post<OtpVerificationResponse>('/auth/verify-otp', { userId, otpCode }),
  
  generateOtp: (userId: number) =>
    api.post<GenerateOtpResponse>('/auth/generate-otp', { userId }),
  
  register: (email: string, password: string, name: string) =>
    api.post<RegisterResponse>('/auth/register', { email, password, name }),
  
  refreshToken: (refreshToken: string) =>
    api.post<RefreshTokenResponse>('/auth/refresh', { refreshToken }),
  
  logout: (userId: number) =>
    api.post<{ success: boolean; message: string }>(`/auth/logout?userId=${userId}`),
};

// User API
export const userAPI = {
  getProfile: () => api.get<UserProfileResponse>('/users/me'),
  
  updateProfile: (name?: string, profileImageUrl?: string) =>
    api.put<UpdateUserResponse>('/users/me', { name, profileImageUrl }),
  
  changePassword: (currentPassword: string, newPassword: string) =>
    api.post<ChangePasswordResponse>('/users/change-password', { currentPassword, newPassword }),
  
  deactivateAccount: () => api.delete<{ success: boolean; message: string }>('/users/me'),
};

export default api;