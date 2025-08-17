package io.chanhyeong.auth.auth.exception

import org.springframework.http.HttpStatus

enum class ExceptionCode(
    val httpStatus: HttpStatus,
    val messageKey: String
) {
    // Authentication errors
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "error.auth.invalid_credentials"),
    INVALID_OTP(HttpStatus.BAD_REQUEST, "error.auth.invalid_otp"),
    OTP_EXPIRED(HttpStatus.BAD_REQUEST, "error.auth.otp_expired"),
    OTP_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "error.auth.otp_generation_failed"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "error.auth.invalid_refresh_token"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "error.auth.token_expired"),
    
    // User management errors
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "error.user.not_found"),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "error.user.already_exists"),
    USER_INACTIVE(HttpStatus.FORBIDDEN, "error.user.inactive"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "error.user.invalid_password"),
    PASSWORD_CHANGE_FAILED(HttpStatus.BAD_REQUEST, "error.user.password_change_failed"),
    USER_UPDATE_FAILED(HttpStatus.BAD_REQUEST, "error.user.update_failed"),
    USER_DEACTIVATION_FAILED(HttpStatus.BAD_REQUEST, "error.user.deactivation_failed"),
    
    // Validation errors
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "error.validation.failed"),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "error.validation.invalid_input"),
    REQUIRED_FIELD_MISSING(HttpStatus.BAD_REQUEST, "error.validation.required_field_missing"),
    
    // System errors
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "error.system.internal_server_error"),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "error.system.database_error"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "error.system.access_denied"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "error.system.resource_not_found")
}