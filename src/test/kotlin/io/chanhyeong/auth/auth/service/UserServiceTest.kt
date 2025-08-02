package io.chanhyeong.auth.auth.service

import io.chanhyeong.auth.auth.entity.User
import io.chanhyeong.auth.auth.entity.UserRole
import io.chanhyeong.auth.auth.repository.UserRepository
import io.chanhyeong.auth.auth.repository.UserRoleRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var userRoleRepository: UserRoleRepository

    @InjectMocks
    private lateinit var userService: UserService

    private lateinit var testUser: User
    private val passwordEncoder = BCryptPasswordEncoder()

    @BeforeEach
    fun setUp() {
        testUser = User(
            id = 1L,
            email = "test@example.com",
            password = passwordEncoder.encode("password123"),
            name = "Test User",
            isActive = true,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
    }

    @Test
    fun `findByEmail should return user when exists`() {
        `when`(userRepository.findByEmail("test@example.com")).thenReturn(testUser)

        val result = userService.findByEmail("test@example.com")

        assertNotNull(result)
        assertEquals("test@example.com", result?.email)
        assertEquals("Test User", result?.name)
    }

    @Test
    fun `findByEmail should return null when user does not exist`() {
        `when`(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null)

        val result = userService.findByEmail("nonexistent@example.com")

        assertNull(result)
    }

    @Test
    fun `createUser should create user successfully`() {
        val email = "new@example.com"
        val password = "password123"
        val name = "New User"

        `when`(userRepository.existsByEmail(email)).thenReturn(false)
        `when`(userRepository.save(any(User::class.java))).thenReturn(testUser.copy(email = email, name = name))
        `when`(userRoleRepository.save(any(UserRole::class.java))).thenReturn(UserRole(1L, 1L, "USER"))

        val result = userService.createUser(email, password, name)

        assertNotNull(result)
        assertEquals(email, result.email)
        assertEquals(name, result.name)
        verify(userRepository).save(any(User::class.java))
        verify(userRoleRepository).save(any(UserRole::class.java))
    }

    @Test
    fun `createUser should throw exception when email already exists`() {
        val email = "existing@example.com"
        
        `when`(userRepository.existsByEmail(email)).thenReturn(true)

        assertThrows(IllegalArgumentException::class.java) {
            userService.createUser(email, "password123", "Test User")
        }
    }

    @Test
    fun `verifyPassword should return true for correct password`() {
        val rawPassword = "password123"
        val encodedPassword = passwordEncoder.encode(rawPassword)

        val result = userService.verifyPassword(rawPassword, encodedPassword)

        assertTrue(result)
    }

    @Test
    fun `verifyPassword should return false for incorrect password`() {
        val rawPassword = "wrongpassword"
        val encodedPassword = passwordEncoder.encode("password123")

        val result = userService.verifyPassword(rawPassword, encodedPassword)

        assertFalse(result)
    }

    @Test
    fun `getUserRoles should return user roles`() {
        val userId = 1L
        val roles = listOf(
            UserRole(1L, userId, "USER"),
            UserRole(2L, userId, "ADMIN")
        )

        `when`(userRoleRepository.findByUserId(userId)).thenReturn(roles)

        val result = userService.getUserRoles(userId)

        assertEquals(2, result.size)
        assertTrue(result.contains("USER"))
        assertTrue(result.contains("ADMIN"))
    }
}