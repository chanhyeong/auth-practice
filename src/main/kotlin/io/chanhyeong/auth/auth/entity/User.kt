package io.chanhyeong.auth.auth.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("USERS")
data class User(
    @Id
    val id: Long? = null,
    @Column("EMAIL")
    val email: String,
    @Column("PASSWORD")
    val password: String,
    @Column("NAME")
    val name: String,
    @Column("PROFILE_IMAGE_URL")
    val profileImageUrl: String? = null,
    @Column("IS_ACTIVE")
    val isActive: Boolean = true,
    @Column("CREATED_AT")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column("UPDATED_AT")
    val updatedAt: LocalDateTime = LocalDateTime.now()
)