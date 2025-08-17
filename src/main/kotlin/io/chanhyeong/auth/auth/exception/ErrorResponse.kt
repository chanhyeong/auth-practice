package io.chanhyeong.auth.auth.exception

import java.time.LocalDateTime

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val code: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val path: String
)

data class ValidationErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val code: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val path: String,
    val fieldErrors: Map<String, String>
)