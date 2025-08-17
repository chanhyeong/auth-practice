import React from 'react';
import { ErrorInfo } from '@/utils/errorHandler';

interface ErrorMessageProps {
  error: string | ErrorInfo;
  className?: string;
}

export function ErrorMessage({ error, className = '' }: ErrorMessageProps) {
  const errorInfo = typeof error === 'string' ? { message: error } : error;

  return (
    <div className={`bg-red-50 border border-red-200 rounded-md p-3 ${className}`}>
      <div className="flex">
        <div className="flex-shrink-0">
          <span className="text-red-400">⚠️</span>
        </div>
        <div className="ml-3">
          <h3 className="text-sm font-medium text-red-800">오류가 발생했습니다</h3>
          <div className="mt-2 text-sm text-red-700">
            <p>{errorInfo.message}</p>
            {errorInfo.code && (
              <p className="mt-1 text-xs text-red-600">오류 코드: {errorInfo.code}</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

interface FieldErrorMessageProps {
  fieldErrors?: Record<string, string>;
  fieldName: string;
  className?: string;
}

export function FieldErrorMessage({ fieldErrors, fieldName, className = '' }: FieldErrorMessageProps) {
  const error = fieldErrors?.[fieldName];
  
  if (!error) return null;

  return (
    <p className={`mt-1 text-sm text-red-600 ${className}`}>
      {error}
    </p>
  );
}

interface ValidationErrorsProps {
  fieldErrors?: Record<string, string>;
  className?: string;
}

export function ValidationErrors({ fieldErrors, className = '' }: ValidationErrorsProps) {
  if (!fieldErrors || Object.keys(fieldErrors).length === 0) return null;

  return (
    <div className={`bg-red-50 border border-red-200 rounded-md p-3 ${className}`}>
      <div className="flex">
        <div className="flex-shrink-0">
          <span className="text-red-400">⚠️</span>
        </div>
        <div className="ml-3">
          <h3 className="text-sm font-medium text-red-800">입력 오류</h3>
          <div className="mt-2">
            <ul className="list-disc list-inside text-sm text-red-700 space-y-1">
              {Object.entries(fieldErrors).map(([field, message]) => (
                <li key={field}>
                  <span className="font-medium">{field}:</span> {message}
                </li>
              ))}
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}