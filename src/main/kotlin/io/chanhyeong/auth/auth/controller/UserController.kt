package io.chanhyeong.auth.auth.controller

import io.chanhyeong.auth.auth.dto.*
import io.chanhyeong.auth.auth.exception.UserNotFoundException
import io.chanhyeong.auth.auth.exception.InvalidPasswordException
import io.chanhyeong.auth.auth.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "User profile management APIs")
@SecurityRequirement(name = "bearerAuth")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Get the profile of the currently authenticated user")
    fun getCurrentUser(authentication: Authentication): ResponseEntity<UserProfileResponse> {
        val email = authentication.name
        val user = userService.findByEmail(email)
            ?: throw UserNotFoundException("User not found with email: $email")

        val roles = userService.getUserRoles(user.id!!)
        
        val response = UserProfileResponse(
            id = user.id,
            email = user.email,
            name = user.name,
            profileImageUrl = user.profileImageUrl,
            isActive = user.isActive,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt,
            roles = roles
        )

        return ResponseEntity.ok(response)
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile", description = "Update the profile of the currently authenticated user")
    fun updateCurrentUser(
        authentication: Authentication,
        @Valid @RequestBody request: UpdateUserRequest
    ): ResponseEntity<UpdateUserResponse> {
        val email = authentication.name
        val user = userService.findByEmail(email)
            ?: throw UserNotFoundException("User not found with email: $email")

        val updatedUser = userService.updateUser(
            userId = user.id!!,
            name = request.name,
            profileImageUrl = request.profileImageUrl
        ) ?: throw RuntimeException("Failed to update user profile")

        val roles = userService.getUserRoles(updatedUser.id!!)
        val userProfile = UserProfileResponse(
            id = updatedUser.id,
            email = updatedUser.email,
            name = updatedUser.name,
            profileImageUrl = updatedUser.profileImageUrl,
            isActive = updatedUser.isActive,
            createdAt = updatedUser.createdAt,
            updatedAt = updatedUser.updatedAt,
            roles = roles
        )

        return ResponseEntity.ok(UpdateUserResponse(
            success = true,
            message = "User profile updated successfully",
            user = userProfile
        ))
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Change the password of the currently authenticated user")
    fun changePassword(
        authentication: Authentication,
        @Valid @RequestBody request: ChangePasswordRequest
    ): ResponseEntity<ChangePasswordResponse> {
        val email = authentication.name
        val user = userService.findByEmail(email)
            ?: throw UserNotFoundException("User not found with email: $email")

        val success = userService.changePassword(
            userId = user.id!!,
            currentPassword = request.currentPassword,
            newPassword = request.newPassword
        )

        if (!success) {
            throw InvalidPasswordException("Current password is incorrect")
        }

        return ResponseEntity.ok(ChangePasswordResponse(
            success = true,
            message = "Password changed successfully"
        ))
    }

    @DeleteMapping("/me")
    @Operation(summary = "Deactivate account", description = "Deactivate the currently authenticated user's account")
    fun deactivateAccount(authentication: Authentication): ResponseEntity<Map<String, Any>> {
        val email = authentication.name
        val user = userService.findByEmail(email)
            ?: throw UserNotFoundException("User not found with email: $email")

        val success = userService.deactivateUser(user.id!!)

        if (!success) {
            throw RuntimeException("Failed to deactivate account")
        }

        return ResponseEntity.ok(mapOf(
            "success" to true,
            "message" to "Account deactivated successfully"
        ))
    }
}