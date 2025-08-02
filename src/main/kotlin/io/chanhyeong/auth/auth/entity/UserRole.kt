package io.chanhyeong.auth.auth.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("user_roles")
data class UserRole(
    @Id
    val id: Long? = null,
    val userId: Long,
    val role: String = "USER"
)