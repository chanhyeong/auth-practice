package io.chanhyeong.auth.auth.service

import io.chanhyeong.auth.auth.entity.OtpCode
import io.chanhyeong.auth.auth.repository.OtpCodeRepository
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.time.LocalDateTime

@Service
class OtpService(
    private val otpCodeRepository: OtpCodeRepository
) {
    private val secureRandom = SecureRandom()
    
    fun generateOtp(userId: Long): String {
        markAllPreviousOtpAsUsed(userId)
        
        val otpCode = generateRandomOtp()
        val expiresAt = LocalDateTime.now().plusSeconds(60)
        
        val otp = OtpCode(
            userId = userId,
            otpCode = otpCode,
            expiresAt = expiresAt
        )
        
        otpCodeRepository.save(otp)
        return otpCode
    }
    
    fun verifyOtp(userId: Long, inputOtp: String): Boolean {
        val otpCode = otpCodeRepository.findByOtpCodeAndUserIdAndIsUsedFalse(inputOtp, userId)
            ?: return false
        
        if (otpCode.expiresAt.isBefore(LocalDateTime.now())) {
            return false
        }
        
        val updatedOtp = otpCode.copy(isUsed = true)
        otpCodeRepository.save(updatedOtp)
        
        return true
    }
    
    fun getLatestOtp(userId: Long): String? {
        val otpCodes = otpCodeRepository.findByUserIdAndIsUsedFalseOrderByCreatedAtDesc(userId)
        
        return otpCodes.firstOrNull()?.let { otp ->
            if (otp.expiresAt.isAfter(LocalDateTime.now())) {
                otp.otpCode
            } else {
                null
            }
        }
    }
    
    fun cleanupExpiredOtps() {
        otpCodeRepository.deleteExpiredCodes(LocalDateTime.now())
    }
    
    private fun generateRandomOtp(): String {
        return String.format("%06d", secureRandom.nextInt(1000000))
    }
    
    private fun markAllPreviousOtpAsUsed(userId: Long) {
        otpCodeRepository.markAllAsUsedByUserId(userId)
    }
}