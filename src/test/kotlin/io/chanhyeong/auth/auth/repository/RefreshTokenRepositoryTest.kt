package io.chanhyeong.auth.auth.repository

import io.chanhyeong.auth.auth.entity.RefreshToken
import io.chanhyeong.auth.auth.entity.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@DataJdbcTest
@Transactional
class RefreshTokenRepositoryTest {

    @Autowired
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `save should create refresh token successfully`() {
        val user = User(
            email = "token-test@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        val savedUser = userRepository.save(user)

        val refreshToken = RefreshToken(
            userId = savedUser.id!!,
            token = "test-refresh-token-12345",
            expiresAt = LocalDateTime.now().plusDays(30)
        )

        val savedRefreshToken = refreshTokenRepository.save(refreshToken)

        assertThat(savedRefreshToken.id).isNotNull()
        assertThat(savedRefreshToken.userId).isEqualTo(savedUser.id)
        assertThat(savedRefreshToken.token).isEqualTo("test-refresh-token-12345")
        assertThat(savedRefreshToken.expiresAt).isAfter(LocalDateTime.now())
    }

    @Test
    fun `findById should return refresh token when exists`() {
        val user = User(
            email = "findbyid-token@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        val savedUser = userRepository.save(user)

        val refreshToken = RefreshToken(
            userId = savedUser.id!!,
            token = "findbyid-token-12345",
            expiresAt = LocalDateTime.now().plusDays(30)
        )
        val savedRefreshToken = refreshTokenRepository.save(refreshToken)

        val foundRefreshToken = refreshTokenRepository.findById(savedRefreshToken.id!!)

        assertThat(foundRefreshToken.isPresent).isTrue()
        assertThat(foundRefreshToken.get().token).isEqualTo("findbyid-token-12345")
        assertThat(foundRefreshToken.get().userId).isEqualTo(savedUser.id)
    }

    @Test
    fun `findById should return empty when refresh token does not exist`() {
        val foundRefreshToken = refreshTokenRepository.findById(999L)

        assertThat(foundRefreshToken.isPresent).isFalse()
    }

    @Test
    fun `findByToken should return refresh token when exists`() {
        val user = User(
            email = "findbytoken@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        val savedUser = userRepository.save(user)

        val refreshToken = RefreshToken(
            userId = savedUser.id!!,
            token = "unique-token-54321",
            expiresAt = LocalDateTime.now().plusDays(30)
        )
        refreshTokenRepository.save(refreshToken)

        val foundRefreshToken = refreshTokenRepository.findByToken("unique-token-54321")

        assertThat(foundRefreshToken).isNotNull()
        assertThat(foundRefreshToken!!.token).isEqualTo("unique-token-54321")
        assertThat(foundRefreshToken.userId).isEqualTo(savedUser.id)
    }

    @Test
    fun `findByToken should return null when token does not exist`() {
        val foundRefreshToken = refreshTokenRepository.findByToken("nonexistent-token")

        assertThat(foundRefreshToken).isNull()
    }

    @Test
    fun `findByUserId should return all tokens for user`() {
        val user = User(
            email = "findbyuserid-token@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        val savedUser = userRepository.save(user)

        val refreshToken1 = RefreshToken(
            userId = savedUser.id!!,
            token = "token1-for-user",
            expiresAt = LocalDateTime.now().plusDays(30)
        )
        val refreshToken2 = RefreshToken(
            userId = savedUser.id!!,
            token = "token2-for-user",
            expiresAt = LocalDateTime.now().plusDays(15)
        )

        refreshTokenRepository.save(refreshToken1)
        refreshTokenRepository.save(refreshToken2)

        val userTokens = refreshTokenRepository.findByUserId(savedUser.id!!)

        assertThat(userTokens).hasSize(2)
        assertThat(userTokens.map { it.token }).containsExactlyInAnyOrder("token1-for-user", "token2-for-user")
        assertThat(userTokens.all { it.userId == savedUser.id }).isTrue()
    }

    @Test
    fun `findByUserId should return empty list when user has no tokens`() {
        val userTokens = refreshTokenRepository.findByUserId(999L)

        assertThat(userTokens).isEmpty()
    }

    @Test
    fun `deleteByUserId should remove all tokens for user`() {
        val user = User(
            email = "delete-tokens@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        val savedUser = userRepository.save(user)

        val refreshToken1 = RefreshToken(
            userId = savedUser.id!!,
            token = "delete-token-1",
            expiresAt = LocalDateTime.now().plusDays(30)
        )
        val refreshToken2 = RefreshToken(
            userId = savedUser.id!!,
            token = "delete-token-2",
            expiresAt = LocalDateTime.now().plusDays(15)
        )

        refreshTokenRepository.save(refreshToken1)
        refreshTokenRepository.save(refreshToken2)

        refreshTokenRepository.deleteByUserId(savedUser.id!!)

        val userTokens = refreshTokenRepository.findByUserId(savedUser.id!!)
        assertThat(userTokens).isEmpty()
    }

    @Test
    fun `deleteExpiredTokens should remove expired tokens only`() {
        refreshTokenRepository.deleteAll()

        val user = User(
            email = "expired-tokens@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        val savedUser = userRepository.save(user)

        val expiredToken = RefreshToken(
            userId = savedUser.id!!,
            token = "expired-token",
            expiresAt = LocalDateTime.now().minusDays(1)
        )
        val validToken = RefreshToken(
            userId = savedUser.id!!,
            token = "valid-token",
            expiresAt = LocalDateTime.now().plusDays(30)
        )

        refreshTokenRepository.save(expiredToken)
        refreshTokenRepository.save(validToken)

        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now())

        val allTokens = refreshTokenRepository.findAll()
        assertThat(allTokens).hasSize(1)
        assertThat(allTokens.first().token).isEqualTo("valid-token")
    }

    @Test
    fun `findAll should return all refresh tokens`() {
        refreshTokenRepository.deleteAll()

        val user1 = User(
            email = "findall-token1@example.com",
            password = "hashedPassword",
            name = "User 1"
        )
        val user2 = User(
            email = "findall-token2@example.com",
            password = "hashedPassword",
            name = "User 2"
        )

        val savedUser1 = userRepository.save(user1)
        val savedUser2 = userRepository.save(user2)

        val refreshToken1 = RefreshToken(
            userId = savedUser1.id!!,
            token = "findall-token-1",
            expiresAt = LocalDateTime.now().plusDays(30)
        )
        val refreshToken2 = RefreshToken(
            userId = savedUser2.id!!,
            token = "findall-token-2",
            expiresAt = LocalDateTime.now().plusDays(30)
        )

        refreshTokenRepository.save(refreshToken1)
        refreshTokenRepository.save(refreshToken2)

        val allTokens = refreshTokenRepository.findAll()

        assertThat(allTokens).hasSize(2)
    }

    @Test
    fun `delete should remove specific refresh token`() {
        val user = User(
            email = "delete-specific-token@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        val savedUser = userRepository.save(user)

        val refreshToken = RefreshToken(
            userId = savedUser.id!!,
            token = "specific-delete-token",
            expiresAt = LocalDateTime.now().plusDays(30)
        )
        val savedRefreshToken = refreshTokenRepository.save(refreshToken)

        refreshTokenRepository.deleteById(savedRefreshToken.id!!)

        val foundToken = refreshTokenRepository.findById(savedRefreshToken.id!!)
        assertThat(foundToken.isPresent).isFalse()
    }

    @Test
    fun `save should set created time automatically`() {
        val user = User(
            email = "created-time@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        val savedUser = userRepository.save(user)

        val refreshToken = RefreshToken(
            userId = savedUser.id!!,
            token = "time-test-token",
            expiresAt = LocalDateTime.now().plusDays(30)
        )

        val savedRefreshToken = refreshTokenRepository.save(refreshToken)

        assertThat(savedRefreshToken.createdAt).isNotNull()
        assertThat(savedRefreshToken.createdAt).isBefore(LocalDateTime.now().plusMinutes(1))
    }

    @Test
    fun `multiple tokens with same user should be allowed`() {
        val user = User(
            email = "multiple-tokens@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        val savedUser = userRepository.save(user)

        val token1 = RefreshToken(
            userId = savedUser.id!!,
            token = "multi-token-1",
            expiresAt = LocalDateTime.now().plusDays(30)
        )
        val token2 = RefreshToken(
            userId = savedUser.id!!,
            token = "multi-token-2",
            expiresAt = LocalDateTime.now().plusDays(20)
        )
        val token3 = RefreshToken(
            userId = savedUser.id!!,
            token = "multi-token-3",
            expiresAt = LocalDateTime.now().plusDays(10)
        )

        refreshTokenRepository.save(token1)
        refreshTokenRepository.save(token2)
        refreshTokenRepository.save(token3)

        val userTokens = refreshTokenRepository.findByUserId(savedUser.id!!)
        assertThat(userTokens).hasSize(3)
        assertThat(userTokens.map { it.token }).containsExactlyInAnyOrder(
            "multi-token-1", "multi-token-2", "multi-token-3"
        )
    }
}