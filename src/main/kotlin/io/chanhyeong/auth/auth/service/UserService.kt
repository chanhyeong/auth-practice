package io.chanhyeong.auth.auth.service

import io.chanhyeong.auth.auth.entity.User
import io.chanhyeong.auth.auth.entity.UserRole
import io.chanhyeong.auth.auth.repository.UserRepository
import io.chanhyeong.auth.auth.repository.UserRoleRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userRoleRepository: UserRoleRepository,
    private val passwordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder()
) {
    
    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }
    
    fun findById(id: Long): User? {
        return userRepository.findById(id).orElse(null)
    }
    
    fun getUserRoles(userId: Long): List<String> {
        return userRoleRepository.findByUserId(userId).map { it.role }
    }
    
    @Transactional
    fun createUser(email: String, password: String, name: String): User {
        if (userRepository.existsByEmail(email)) {
            throw IllegalArgumentException("Email already exists")
        }
        
        val encodedPassword = passwordEncoder.encode(password)
        val user = User(
            email = email,
            password = encodedPassword,
            name = name
        )
        
        val savedUser = userRepository.save(user)
        
        val userRole = UserRole(
            userId = savedUser.id!!,
            role = "USER"
        )
        userRoleRepository.save(userRole)
        
        return savedUser
    }
    
    fun verifyPassword(rawPassword: String, encodedPassword: String): Boolean {
        return passwordEncoder.matches(rawPassword, encodedPassword)
    }
    
    @Transactional
    fun updateUser(userId: Long, name: String?, profileImageUrl: String?): User? {
        val user = userRepository.findById(userId).orElse(null) ?: return null
        
        val updatedUser = user.copy(
            name = name ?: user.name,
            profileImageUrl = profileImageUrl ?: user.profileImageUrl,
            updatedAt = java.time.LocalDateTime.now()
        )
        
        return userRepository.save(updatedUser)
    }
    
    @Transactional
    fun changePassword(userId: Long, currentPassword: String, newPassword: String): Boolean {
        val user = userRepository.findById(userId).orElse(null) ?: return false
        
        if (!passwordEncoder.matches(currentPassword, user.password)) {
            return false
        }
        
        val updatedUser = user.copy(
            password = passwordEncoder.encode(newPassword),
            updatedAt = java.time.LocalDateTime.now()
        )
        
        userRepository.save(updatedUser)
        return true
    }
    
    @Transactional
    fun deactivateUser(userId: Long): Boolean {
        val user = userRepository.findById(userId).orElse(null) ?: return false
        
        val updatedUser = user.copy(
            isActive = false,
            updatedAt = java.time.LocalDateTime.now()
        )
        
        userRepository.save(updatedUser)
        return true
    }
}