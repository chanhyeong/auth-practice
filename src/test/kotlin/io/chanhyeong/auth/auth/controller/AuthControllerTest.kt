package io.chanhyeong.auth.auth.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.chanhyeong.auth.auth.dto.LoginRequest
import io.chanhyeong.auth.auth.dto.OtpVerificationRequest
import io.chanhyeong.auth.auth.dto.RegisterRequest
import io.chanhyeong.auth.auth.service.AuthService
import io.chanhyeong.auth.auth.service.UserService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(AuthController::class)
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var authService: AuthService

    @MockBean
    private lateinit var userService: UserService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `login should return success when credentials are valid`() {
        val loginRequest = LoginRequest("test@example.com", "password123")
        val loginResult = AuthService.LoginResult(
            success = true,
            userId = 1L,
            requiresOtp = true,
            message = "OTP sent"
        )

        `when`(authService.authenticateUser("test@example.com", "password123"))
            .thenReturn(loginResult)
        `when`(authService.getCurrentOtp(1L)).thenReturn("123456")

        mockMvc.perform(
            post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.requiresOtp").value(true))
            .andExpect(jsonPath("$.otpCode").value("123456"))
    }

    @Test
    fun `login should return bad request when credentials are invalid`() {
        val loginRequest = LoginRequest("test@example.com", "wrongpassword")
        val loginResult = AuthService.LoginResult(
            success = false,
            message = "Invalid email or password"
        )

        `when`(authService.authenticateUser("test@example.com", "wrongpassword"))
            .thenReturn(loginResult)

        mockMvc.perform(
            post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Invalid email or password"))
    }

    @Test
    fun `verifyOtp should return success with tokens when OTP is valid`() {
        val otpRequest = OtpVerificationRequest(1L, "123456")
        val otpResult = AuthService.OtpVerificationResult(
            success = true,
            accessToken = "access-token",
            refreshToken = "refresh-token"
        )

        `when`(authService.verifyOtpAndLogin(1L, "123456"))
            .thenReturn(otpResult)

        mockMvc.perform(
            post("/api/auth/verify-otp")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(otpRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.accessToken").value("access-token"))
            .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
    }

    @Test
    fun `register should return success when user is created successfully`() {
        val registerRequest = RegisterRequest(
            "new@example.com",
            "password123",
            "New User"
        )
        
        val mockUser = mock(io.chanhyeong.auth.auth.entity.User::class.java)
        `when`(mockUser.id).thenReturn(1L)
        `when`(userService.createUser("new@example.com", "password123", "New User"))
            .thenReturn(mockUser)

        mockMvc.perform(
            post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.userId").value(1))
    }

    @Test
    fun `register should return bad request when email already exists`() {
        val registerRequest = RegisterRequest(
            "existing@example.com",
            "password123",
            "Test User"
        )

        `when`(userService.createUser("existing@example.com", "password123", "Test User"))
            .thenThrow(IllegalArgumentException("Email already exists"))

        mockMvc.perform(
            post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Email already exists"))
    }

    @Test
    @WithMockUser
    fun `logout should return success`() {
        doNothing().`when`(authService).logout(1L)

        mockMvc.perform(
            post("/api/auth/logout")
                .with(csrf())
                .param("userId", "1")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Logout successful"))
    }
}