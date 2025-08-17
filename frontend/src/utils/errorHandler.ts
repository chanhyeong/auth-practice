import { AxiosError } from 'axios';
import { ErrorResponse, ValidationErrorResponse } from '@/types/api';

export interface ErrorInfo {
  message: string;
  code?: string;
  fieldErrors?: Record<string, string>;
}

export function handleApiError(error: unknown): ErrorInfo {
  if (error instanceof AxiosError) {
    const response = error.response;
    
    if (response?.data) {
      const errorData = response.data as ErrorResponse | ValidationErrorResponse;
      
      // 새로운 표준 에러 응답 형식
      if ('code' in errorData && 'message' in errorData) {
        const result: ErrorInfo = {
          message: errorData.message,
          code: errorData.code
        };
        
        // ValidationErrorResponse인 경우 fieldErrors 추가
        if ('fieldErrors' in errorData) {
          result.fieldErrors = errorData.fieldErrors;
        }
        
        return result;
      }
      
      // 기존 형식 지원 (하위 호환성)
      if (errorData && typeof errorData === 'object' && 'message' in errorData) {
        const message = (errorData as { message: unknown }).message;
        if (typeof message === 'string') {
          return {
            message: message
          };
        }
      }
    }
    
    // HTTP 상태 코드에 따른 기본 메시지
    switch (response?.status) {
      case 400:
        return { message: '잘못된 요청입니다.' };
      case 401:
        return { message: '인증이 필요합니다.' };
      case 403:
        return { message: '접근 권한이 없습니다.' };
      case 404:
        return { message: '요청한 리소스를 찾을 수 없습니다.' };
      case 409:
        return { message: '이미 존재하는 데이터입니다.' };
      case 500:
        return { message: '서버 오류가 발생했습니다.' };
      default:
        return { message: '네트워크 오류가 발생했습니다.' };
    }
  }
  
  if (error instanceof Error) {
    return { message: error.message };
  }
  
  return { message: '알 수 없는 오류가 발생했습니다.' };
}

export function getFieldErrorMessage(fieldErrors: Record<string, string> | undefined, fieldName: string): string | undefined {
  return fieldErrors?.[fieldName];
}