package io.chanhyeong.auth.auth.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("REFRESH_TOKENS")
data class RefreshToken(
    @Id
    val id: Long? = null,
    @Column("USER_ID")
    val userId: Long,
    @Column("TOKEN")
    val token: String,
    @Column("EXPIRES_AT")
    val expiresAt: LocalDateTime,
    @Column("CREATED_AT")
    val createdAt: LocalDateTime = LocalDateTime.now()
)