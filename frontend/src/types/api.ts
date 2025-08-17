// Error Response Types
export interface ErrorResponse {
  status: number;
  error: string;
  message: string;
  code: string;
  timestamp: string;
  path: string;
}

export interface ValidationErrorResponse extends ErrorResponse {
  fieldErrors: Record<string, string>;
}

// API Response Types
export interface ApiResponse<T = unknown> {
  success: boolean;
  message?: string;
  data?: T;
}

// Auth Related Types
export interface LoginResponse {
  success: boolean;
  message: string;
  userId?: number;
  requiresOtp?: boolean;
  otpCode?: string;
}

export interface OtpVerificationResponse {
  success: boolean;
  message: string;
  accessToken?: string;
  refreshToken?: string;
}

export interface RegisterResponse {
  success: boolean;
  message: string;
  userId?: number;
}

export interface RefreshTokenResponse {
  success: boolean;
  message: string;
  accessToken?: string;
}

export interface GenerateOtpResponse {
  success: boolean;
  message: string;
  otpCode?: string;
}

// User Related Types
export interface UserProfile {
  id: number;
  email: string;
  name: string;
  profileImageUrl?: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
  roles: string[];
}

export interface UserProfileResponse {
  id: number;
  email: string;
  name: string;
  profileImageUrl?: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
  roles: string[];
}

export interface UpdateUserResponse {
  success: boolean;
  message: string;
  user?: UserProfile;
}

export interface ChangePasswordResponse {
  success: boolean;
  message: string;
}