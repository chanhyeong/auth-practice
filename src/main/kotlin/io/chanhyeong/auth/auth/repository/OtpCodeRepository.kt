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
    @Query("UPDATE OTP_CODES SET IS_USED = true WHERE USER_ID = :userId AND IS_USED = false")
    fun markAllAsUsedByUserId(userId: Long)
    
    @Modifying
    @Query("DELETE FROM OTP_CODES WHERE EXPIRES_AT < :now")
    fun deleteExpiredCodes(now: LocalDateTime)
}