package io.chanhyeong.auth.auth.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("OTP_CODES")
data class OtpCode(
    @Id
    val id: Long? = null,
    @Column("USER_ID")
    val userId: Long,
    @Column("OTP_CODE")
    val otpCode: String,
    @Column("EXPIRES_AT")
    val expiresAt: LocalDateTime,
    @Column("IS_USED")
    val isUsed: Boolean = false,
    @Column("CREATED_AT")
    val createdAt: LocalDateTime = LocalDateTime.now()
)