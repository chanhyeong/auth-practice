package io.chanhyeong.auth.auth.service

import io.chanhyeong.auth.auth.entity.RefreshToken
import io.chanhyeong.auth.auth.repository.RefreshTokenRepository
import io.chanhyeong.auth.auth.security.JwtTokenProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class AuthService(
    private val userService: UserService,
    private val otpService: OtpService,
    private val jwtTokenProvider: JwtTokenProvider,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    
    data class LoginResult(
        val success: Boolean,
        val userId: Long? = null,
        val message: String? = null,
        val requiresOtp: Boolean = false
    )
    
    data class OtpVerificationResult(
        val success: Boolean,
        val accessToken: String? = null,
        val refreshToken: String? = null,
        val message: String? = null
    )
    
    data class TokenRefreshResult(
        val success: Boolean,
        val accessToken: String? = null,
        val message: String? = null
    )
    
    fun authenticateUser(email: String, password: String): LoginResult {
        val user = userService.findByEmail(email)
            ?: return LoginResult(false, message = "Invalid email or password")
        
        if (!user.isActive) {
            return LoginResult(false, message = "Account is deactivated")
        }
        
        if (!userService.verifyPassword(password, user.password)) {
            return LoginResult(false, message = "Invalid email or password")
        }
        
        val otpCode = otpService.generateOtp(user.id!!)
        
        return LoginResult(
            success = true,
            userId = user.id,
            requiresOtp = true,
            message = "OTP sent. Please verify with: $otpCode"
        )
    }
    
    @Transactional
    fun verifyOtpAndLogin(userId: Long, otpCode: String): OtpVerificationResult {
        val user = userService.findById(userId)
            ?: return OtpVerificationResult(false, message = "User not found")
        
        if (!user.isActive) {
            return OtpVerificationResult(false, message = "Account is deactivated")
        }
        
        if (!otpService.verifyOtp(userId, otpCode)) {
            return OtpVerificationResult(false, message = "Invalid or expired OTP")
        }
        
        val roles = userService.getUserRoles(userId)
        val authorities: Collection<GrantedAuthority> = roles.map { SimpleGrantedAuthority("ROLE_$it") }
        
        val authentication = UsernamePasswordAuthenticationToken(user.email, null, authorities)
        val accessToken = jwtTokenProvider.generateAccessToken(authentication)
        val refreshToken = jwtTokenProvider.generateRefreshToken(user.email)
        
        saveRefreshToken(userId, refreshToken)
        
        return OtpVerificationResult(
            success = true,
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }
    
    @Transactional
    fun refreshAccessToken(refreshToken: String): TokenRefreshResult {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return TokenRefreshResult(false, message = "Invalid refresh token")
        }
        
        val tokenType = jwtTokenProvider.getTokenType(refreshToken)
        if (tokenType != "refresh") {
            return TokenRefreshResult(false, message = "Invalid token type")
        }
        
        val storedToken = refreshTokenRepository.findByToken(refreshToken)
            ?: return TokenRefreshResult(false, message = "Refresh token not found")
        
        if (storedToken.expiresAt.isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(storedToken)
            return TokenRefreshResult(false, message = "Refresh token expired")
        }
        
        val username = jwtTokenProvider.getUsernameFromToken(refreshToken)
        val user = userService.findByEmail(username)
            ?: return TokenRefreshResult(false, message = "User not found")
        
        if (!user.isActive) {
            return TokenRefreshResult(false, message = "Account is deactivated")
        }
        
        val roles = userService.getUserRoles(user.id!!)
        val authorities: Collection<GrantedAuthority> = roles.map { SimpleGrantedAuthority("ROLE_$it") }
        
        val authentication = UsernamePasswordAuthenticationToken(user.email, null, authorities)
        val newAccessToken = jwtTokenProvider.generateAccessToken(authentication)
        
        return TokenRefreshResult(
            success = true,
            accessToken = newAccessToken
        )
    }
    
    @Transactional
    fun logout(userId: Long) {
        refreshTokenRepository.deleteByUserId(userId)
    }
    
    fun generateNewOtp(userId: Long): String {
        return otpService.generateOtp(userId)
    }
    
    fun getCurrentOtp(userId: Long): String? {
        return otpService.getLatestOtp(userId)
    }
    
    private fun saveRefreshToken(userId: Long, token: String) {
        val expirationDate = jwtTokenProvider.getExpirationFromToken(token)
        val expiresAt = expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        
        val refreshToken = RefreshToken(
            userId = userId,
            token = token,
            expiresAt = expiresAt
        )
        
        refreshTokenRepository.save(refreshToken)
    }
}