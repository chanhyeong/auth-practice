package io.chanhyeong.auth.auth.repository

import io.chanhyeong.auth.auth.entity.OtpCode
import io.chanhyeong.auth.auth.entity.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@DataJdbcTest
@Transactional
class OtpCodeRepositoryTest {

    @Autowired
    private lateinit var otpCodeRepository: OtpCodeRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `save should create otp code successfully`() {
        val user = User(
            email = "otp-test@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        val savedUser = userRepository.save(user)

        val otpCode = OtpCode(
            userId = savedUser.id!!,
            otpCode = "123456",
            expiresAt = LocalDateTime.now().plusMinutes(5)
        )

        val savedOtpCode = otpCodeRepository.save(otpCode)

        assertThat(savedOtpCode.id).isNotNull()
        assertThat(savedOtpCode.userId).isEqualTo(savedUser.id)
        assertThat(savedOtpCode.otpCode).isEqualTo("123456")
        assertThat(savedOtpCode.isUsed).isFalse()
    }

    @Test
    fun `findById should return otp code when exists`() {
        val user = User(
            email = "findbyid-otp@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        val savedUser = userRepository.save(user)

        val otpCode = OtpCode(
            userId = savedUser.id!!,
            otpCode = "654321",
            expiresAt = LocalDateTime.now().plusMinutes(5)
        )
        val savedOtpCode = otpCodeRepository.save(otpCode)

        val foundOtpCode = otpCodeRepository.findById(savedOtpCode.id!!)

        assertThat(foundOtpCode.isPresent).isTrue()
        assertThat(foundOtpCode.get().otpCode).isEqualTo("654321")
        assertThat(foundOtpCode.get().userId).isEqualTo(savedUser.id)
    }

    @Test
    fun `findById should return empty when otp code does not exist`() {
        val foundOtpCode = otpCodeRepository.findById(999L)

        assertThat(foundOtpCode.isPresent).isFalse()
    }

    @Test
    fun `findByUserIdAndIsUsedFalseOrderByCreatedAtDesc should return unused otp codes ordered by created date`() {
        val user = User(
            email = "findbyuserid-otp@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        val savedUser = userRepository.save(user)

        val otpCode1 = OtpCode(
            userId = savedUser.id!!,
            otpCode = "111111",
            expiresAt = LocalDateTime.now().plusMinutes(5),
            createdAt = LocalDateTime.now().minusMinutes(10)
        )
        val otpCode2 = OtpCode(
            userId = savedUser.id!!,
            otpCode = "222222",
            expiresAt = LocalDateTime.now().plusMinutes(5),
            isUsed = true,
            createdAt = LocalDateTime.now().minusMinutes(5)
        )
        val otpCode3 = OtpCode(
            userId = savedUser.id!!,
            otpCode = "333333",
            expiresAt = LocalDateTime.now().plusMinutes(5),
            createdAt = LocalDateTime.now().minusMinutes(1)
        )

        otpCodeRepository.save(otpCode1)
        otpCodeRepository.save(otpCode2)
        otpCodeRepository.save(otpCode3)

        val otpCodes = otpCodeRepository.findByUserIdAndIsUsedFalseOrderByCreatedAtDesc(savedUser.id!!)

        assertThat(otpCodes).hasSize(2)
        assertThat(otpCodes[0].otpCode).isEqualTo("333333") // most recent
        assertThat(otpCodes[1].otpCode).isEqualTo("111111") // oldest
        assertThat(otpCodes.all { !it.isUsed }).isTrue()
    }

    @Test
    fun `findByUserIdAndIsUsedFalseOrderByCreatedAtDesc should return empty list when no unused codes`() {
        val user = User(
            email = "nounused-otp@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        val savedUser = userRepository.save(user)

        val otpCodes = otpCodeRepository.findByUserIdAndIsUsedFalseOrderByCreatedAtDesc(savedUser.id!!)

        assertThat(otpCodes).isEmpty()
    }

    @Test
    fun `findByOtpCodeAndUserIdAndIsUsedFalse should return otp code when valid`() {
        val user = User(
            email = "findbycode-otp@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        val savedUser = userRepository.save(user)

        val otpCode = OtpCode(
            userId = savedUser.id!!,
            otpCode = "987654",
            expiresAt = LocalDateTime.now().plusMinutes(5)
        )
        otpCodeRepository.save(otpCode)

        val foundOtpCode = otpCodeRepository.findByOtpCodeAndUserIdAndIsUsedFalse("987654", savedUser.id!!)

        assertThat(foundOtpCode).isNotNull()
        assertThat(foundOtpCode!!.otpCode).isEqualTo("987654")
        assertThat(foundOtpCode.userId).isEqualTo(savedUser.id)
        assertThat(foundOtpCode.isUsed).isFalse()
    }

    @Test
    fun `findByOtpCodeAndUserIdAndIsUsedFalse should return null when otp code is used`() {
        val user = User(
            email = "used-otp@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        val savedUser = userRepository.save(user)

        val otpCode = OtpCode(
            userId = savedUser.id!!,
            otpCode = "456789",
            expiresAt = LocalDateTime.now().plusMinutes(5),
            isUsed = true
        )
        otpCodeRepository.save(otpCode)

        val foundOtpCode = otpCodeRepository.findByOtpCodeAndUserIdAndIsUsedFalse("456789", savedUser.id!!)

        assertThat(foundOtpCode).isNull()
    }

    @Test
    fun `findByOtpCodeAndUserIdAndIsUsedFalse should return null when otp code does not exist`() {
        val foundOtpCode = otpCodeRepository.findByOtpCodeAndUserIdAndIsUsedFalse("nonexistent", 999L)

        assertThat(foundOtpCode).isNull()
    }

    @Test
    fun `markAllAsUsedByUserId should mark all unused codes as used`() {
        val user = User(
            email = "markused-otp@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        val savedUser = userRepository.save(user)

        val otpCode1 = OtpCode(
            userId = savedUser.id!!,
            otpCode = "111111",
            expiresAt = LocalDateTime.now().plusMinutes(5)
        )
        val otpCode2 = OtpCode(
            userId = savedUser.id!!,
            otpCode = "222222",
            expiresAt = LocalDateTime.now().plusMinutes(5)
        )
        val otpCode3 = OtpCode(
            userId = savedUser.id!!,
            otpCode = "333333",
            expiresAt = LocalDateTime.now().plusMinutes(5),
            isUsed = true
        )

        otpCodeRepository.save(otpCode1)
        otpCodeRepository.save(otpCode2)
        otpCodeRepository.save(otpCode3)

        otpCodeRepository.markAllAsUsedByUserId(savedUser.id!!)

        val unusedCodes = otpCodeRepository.findByUserIdAndIsUsedFalseOrderByCreatedAtDesc(savedUser.id!!)
        assertThat(unusedCodes).isEmpty()
    }

    @Test
    fun `deleteExpiredCodes should remove expired otp codes`() {
        otpCodeRepository.deleteAll()

        val user = User(
            email = "expired-otp@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        val savedUser = userRepository.save(user)

        val expiredOtpCode = OtpCode(
            userId = savedUser.id!!,
            otpCode = "000000",
            expiresAt = LocalDateTime.now().minusMinutes(10)
        )
        val validOtpCode = OtpCode(
            userId = savedUser.id!!,
            otpCode = "111111",
            expiresAt = LocalDateTime.now().plusMinutes(5)
        )

        otpCodeRepository.save(expiredOtpCode)
        otpCodeRepository.save(validOtpCode)

        otpCodeRepository.deleteExpiredCodes(LocalDateTime.now())

        val allCodes = otpCodeRepository.findAll()
        assertThat(allCodes).hasSize(1)
        assertThat(allCodes.first().otpCode).isEqualTo("111111")
    }

    @Test
    fun `findAll should return all otp codes`() {
        otpCodeRepository.deleteAll()

        val user1 = User(
            email = "findall-otp1@example.com",
            password = "hashedPassword",
            name = "User 1"
        )
        val user2 = User(
            email = "findall-otp2@example.com",
            password = "hashedPassword",
            name = "User 2"
        )

        val savedUser1 = userRepository.save(user1)
        val savedUser2 = userRepository.save(user2)

        val otpCode1 = OtpCode(
            userId = savedUser1.id!!,
            otpCode = "111111",
            expiresAt = LocalDateTime.now().plusMinutes(5)
        )
        val otpCode2 = OtpCode(
            userId = savedUser2.id!!,
            otpCode = "222222",
            expiresAt = LocalDateTime.now().plusMinutes(5)
        )

        otpCodeRepository.save(otpCode1)
        otpCodeRepository.save(otpCode2)

        val allOtpCodes = otpCodeRepository.findAll()

        assertThat(allOtpCodes).hasSize(2)
    }

    @Test
    fun `delete should remove specific otp code`() {
        val user = User(
            email = "delete-otp@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        val savedUser = userRepository.save(user)

        val otpCode = OtpCode(
            userId = savedUser.id!!,
            otpCode = "999999",
            expiresAt = LocalDateTime.now().plusMinutes(5)
        )
        val savedOtpCode = otpCodeRepository.save(otpCode)

        otpCodeRepository.deleteById(savedOtpCode.id!!)

        val foundOtpCode = otpCodeRepository.findById(savedOtpCode.id!!)
        assertThat(foundOtpCode.isPresent).isFalse()
    }
}