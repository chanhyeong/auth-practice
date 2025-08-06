package io.chanhyeong.auth.auth.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("USER_ROLES")
data class UserRole(
    @Id
    val id: Long? = null,
    @Column("USER_ID")
    val userId: Long,
    @Column("ROLE")
    val role: String = "USER"
)