package io.chanhyeong.auth.auth.repository

import io.chanhyeong.auth.auth.entity.OtpCode
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface OtpCodeRepository : CrudRepository<OtpCode, Long> {
    fun findByUserIdAndIsUsedFalseOrderByCreatedAtDesc(userId: Long): List<OtpCode>
    
    fun findByOtpCodeAndUserIdAndIsUsedFalse(otpCode: String, userId: Long): OtpCode?
    
    @Modifying
    @Query("UPDATE otp_codes SET is_used = true WHERE user_id = :userId AND is_used = false")
    fun markAllAsUsedByUserId(userId: Long)
    
    @Modifying
    @Query("DELETE FROM otp_codes WHERE expires_at < :now")
    fun deleteExpiredCodes(now: LocalDateTime)
}