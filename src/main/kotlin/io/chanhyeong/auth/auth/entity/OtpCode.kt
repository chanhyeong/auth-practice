package io.chanhyeong.auth.auth.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("otp_codes")
data class OtpCode(
    @Id
    val id: Long? = null,
    val userId: Long,
    val otpCode: String,
    val expiresAt: LocalDateTime,
    val isUsed: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
)