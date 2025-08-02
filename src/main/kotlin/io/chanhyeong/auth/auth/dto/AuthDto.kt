package io.chanhyeong.auth.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(
    @field:Email(message = "Valid email is required")
    @field:NotBlank(message = "Email is required")
    val email: String,
    
    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters")
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String?,
    val userId: Long? = null,
    val requiresOtp: Boolean = false,
    val otpCode: String? = null
)

data class OtpVerificationRequest(
    val userId: Long,
    @field:NotBlank(message = "OTP code is required")
    @field:Size(min = 6, max = 6, message = "OTP code must be 6 digits")
    val otpCode: String
)

data class OtpVerificationResponse(
    val success: Boolean,
    val message: String?,
    val accessToken: String? = null,
    val refreshToken: String? = null
)

data class RegisterRequest(
    @field:Email(message = "Valid email is required")
    @field:NotBlank(message = "Email is required")
    val email: String,
    
    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters")
    val password: String,
    
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    val name: String
)

data class RegisterResponse(
    val success: Boolean,
    val message: String?,
    val userId: Long? = null
)

data class RefreshTokenRequest(
    @field:NotBlank(message = "Refresh token is required")
    val refreshToken: String
)

data class RefreshTokenResponse(
    val success: Boolean,
    val message: String?,
    val accessToken: String? = null
)

data class GenerateOtpRequest(
    val userId: Long
)

data class GenerateOtpResponse(
    val success: Boolean,
    val message: String?,
    val otpCode: String? = null
)