package io.chanhyeong.auth.auth.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class UserProfileResponse(
    val id: Long,
    val email: String,
    val name: String,
    val profileImageUrl: String?,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val roles: List<String>
)

data class UpdateUserRequest(
    @field:Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    val name: String?,
    
    @field:Size(max = 500, message = "Profile image URL must not exceed 500 characters")
    val profileImageUrl: String?
)

data class UpdateUserResponse(
    val success: Boolean,
    val message: String?,
    val user: UserProfileResponse? = null
)

data class ChangePasswordRequest(
    @field:NotBlank(message = "Current password is required")
    val currentPassword: String,
    
    @field:NotBlank(message = "New password is required")
    @field:Size(min = 8, message = "New password must be at least 8 characters")
    val newPassword: String
)

data class ChangePasswordResponse(
    val success: Boolean,
    val message: String?
)