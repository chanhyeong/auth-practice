package io.chanhyeong.auth.auth.service

import io.chanhyeong.auth.auth.entity.OtpCode
import io.chanhyeong.auth.auth.repository.OtpCodeRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class OtpServiceTest {

    @Mock
    private lateinit var otpCodeRepository: OtpCodeRepository

    @InjectMocks
    private lateinit var otpService: OtpService

    private val userId = 1L
    private lateinit var validOtp: OtpCode
    private lateinit var expiredOtp: OtpCode

    @BeforeEach
    fun setUp() {
        validOtp = OtpCode(
            id = 1L,
            userId = userId,
            otpCode = "123456",
            expiresAt = LocalDateTime.now().plusMinutes(1),
            isUsed = false,
            createdAt = LocalDateTime.now()
        )

        expiredOtp = OtpCode(
            id = 2L,
            userId = userId,
            otpCode = "654321",
            expiresAt = LocalDateTime.now().minusMinutes(1),
            isUsed = false,
            createdAt = LocalDateTime.now().minusMinutes(2)
        )
    }

    @Test
    fun `generateOtp should create and return new OTP`() {
        `when`(otpCodeRepository.save(any(OtpCode::class.java))).thenReturn(validOtp)

        val result = otpService.generateOtp(userId)

        assertNotNull(result)
        assertEquals(6, result.length)
        assertTrue(result.matches(Regex("\\d{6}")))
        verify(otpCodeRepository).markAllAsUsedByUserId(userId)
        verify(otpCodeRepository).save(any(OtpCode::class.java))
    }

    @Test
    fun `verifyOtp should return true for valid OTP`() {
        `when`(otpCodeRepository.findByOtpCodeAndUserIdAndIsUsedFalse("123456", userId))
            .thenReturn(validOtp)
        `when`(otpCodeRepository.save(any(OtpCode::class.java)))
            .thenReturn(validOtp.copy(isUsed = true))

        val result = otpService.verifyOtp(userId, "123456")

        assertTrue(result)
        verify(otpCodeRepository).save(any(OtpCode::class.java))
    }

    @Test
    fun `verifyOtp should return false for non-existent OTP`() {
        `when`(otpCodeRepository.findByOtpCodeAndUserIdAndIsUsedFalse("000000", userId))
            .thenReturn(null)

        val result = otpService.verifyOtp(userId, "000000")

        assertFalse(result)
        verify(otpCodeRepository, never()).save(any(OtpCode::class.java))
    }

    @Test
    fun `verifyOtp should return false for expired OTP`() {
        `when`(otpCodeRepository.findByOtpCodeAndUserIdAndIsUsedFalse("654321", userId))
            .thenReturn(expiredOtp)

        val result = otpService.verifyOtp(userId, "654321")

        assertFalse(result)
        verify(otpCodeRepository, never()).save(any(OtpCode::class.java))
    }

    @Test
    fun `getLatestOtp should return valid OTP code`() {
        `when`(otpCodeRepository.findByUserIdAndIsUsedFalseOrderByCreatedAtDesc(userId))
            .thenReturn(listOf(validOtp))

        val result = otpService.getLatestOtp(userId)

        assertEquals("123456", result)
    }

    @Test
    fun `getLatestOtp should return null for expired OTP`() {
        `when`(otpCodeRepository.findByUserIdAndIsUsedFalseOrderByCreatedAtDesc(userId))
            .thenReturn(listOf(expiredOtp))

        val result = otpService.getLatestOtp(userId)

        assertNull(result)
    }

    @Test
    fun `getLatestOtp should return null when no OTP exists`() {
        `when`(otpCodeRepository.findByUserIdAndIsUsedFalseOrderByCreatedAtDesc(userId))
            .thenReturn(emptyList())

        val result = otpService.getLatestOtp(userId)

        assertNull(result)
    }
}