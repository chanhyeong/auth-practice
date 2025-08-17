package io.chanhyeong.auth.auth.controller

import io.chanhyeong.auth.auth.dto.*
import io.chanhyeong.auth.auth.exception.OtpException
import io.chanhyeong.auth.auth.exception.ExceptionCode
import io.chanhyeong.auth.auth.exception.UserAlreadyExistsException
import io.chanhyeong.auth.auth.service.AuthService
import io.chanhyeong.auth.auth.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
class AuthController(
    private val authService: AuthService,
    private val userService: UserService
) {

    @PostMapping("/login")
    @Operation(summary = "User login (1st step)", description = "Authenticate user with email and password")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        val result = authService.authenticateUser(request.email, request.password)
        
        val response = LoginResponse(
            success = result.success,
            message = result.message,
            userId = result.userId,
            requiresOtp = result.requiresOtp,
            otpCode = if (result.requiresOtp) {
                authService.getCurrentOtp(result.userId!!)
            } else null
        )
        
        return ResponseEntity.ok(response)
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP (2nd step)", description = "Verify OTP code and complete login")
    fun verifyOtp(@Valid @RequestBody request: OtpVerificationRequest): ResponseEntity<OtpVerificationResponse> {
        val result = authService.verifyOtpAndLogin(request.userId, request.otpCode)
        
        val response = OtpVerificationResponse(
            success = result.success,
            message = result.message,
            accessToken = result.accessToken,
            refreshToken = result.refreshToken
        )
        
        return ResponseEntity.ok(response)
    }

    @PostMapping("/generate-otp")
    @Operation(summary = "Generate new OTP", description = "Generate a new OTP code for the user")
    fun generateOtp(@Valid @RequestBody request: GenerateOtpRequest): ResponseEntity<GenerateOtpResponse> {
        val otpCode = authService.generateNewOtp(request.userId)
        
        return ResponseEntity.ok(GenerateOtpResponse(
            success = true,
            message = "New OTP generated successfully",
            otpCode = otpCode
        ))
    }

    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user account")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<RegisterResponse> {
        val user = userService.createUser(request.email, request.password, request.name)
        
        return ResponseEntity.ok(RegisterResponse(
            success = true,
            message = "User registered successfully",
            userId = user.id
        ))
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Refresh the access token using refresh token")
    fun refreshToken(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<RefreshTokenResponse> {
        val result = authService.refreshAccessToken(request.refreshToken)
        
        val response = RefreshTokenResponse(
            success = result.success,
            message = result.message,
            accessToken = result.accessToken
        )
        
        return ResponseEntity.ok(response)
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout user and invalidate tokens")
    fun logout(@RequestParam userId: Long): ResponseEntity<Map<String, Any>> {
        authService.logout(userId)
        
        return ResponseEntity.ok(mapOf(
            "success" to true,
            "message" to "Logout successful"
        ))
    }
}