package io.chanhyeong.auth.auth.exception

import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class GlobalExceptionHandler {
    
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception, 
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error occurred", ex)
        
        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Internal Server Error",
            message = "An unexpected error occurred",
            timestamp = System.currentTimeMillis(),
            path = getPath(request)
        )
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorResponse)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        ex: IllegalArgumentException, 
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("IllegalArgumentException: {}", ex.message)
        
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            message = ex.message ?: "Invalid argument provided",
            timestamp = System.currentTimeMillis(),
            path = getPath(request)
        )
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse)
    }

    @ExceptionHandler(DataAccessException::class)
    fun handleDataAccessException(
        ex: DataAccessException, 
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error("Database error occurred", ex)
        
        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Database Error",
            message = "A database error occurred. Please try again later.",
            timestamp = System.currentTimeMillis(),
            path = getPath(request)
        )
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
        request: WebRequest
    ): ResponseEntity<ValidationErrorResponse> {
        logger.warn("Validation error occurred: {}", ex.message)
        
        val fieldErrors = ex.bindingResult.fieldErrors.associate { fieldError: FieldError ->
            fieldError.field to (fieldError.defaultMessage ?: "Invalid value")
        }
        
        val errorResponse = ValidationErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Validation Failed",
            message = "Request validation failed",
            timestamp = System.currentTimeMillis(),
            path = getPath(request),
            fieldErrors = fieldErrors
        )
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse)
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(
        ex: AuthenticationException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Authentication failed: {}", ex.message)
        
        val errorResponse = ErrorResponse(
            status = HttpStatus.UNAUTHORIZED.value(),
            error = "Authentication Failed",
            message = when (ex) {
                is BadCredentialsException -> "Invalid credentials provided"
                else -> "Authentication failed"
            },
            timestamp = System.currentTimeMillis(),
            path = getPath(request)
        )
        
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(errorResponse)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(
        ex: AccessDeniedException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Access denied: {}", ex.message)
        
        val errorResponse = ErrorResponse(
            status = HttpStatus.FORBIDDEN.value(),
            error = "Access Denied",
            message = "You don't have permission to access this resource",
            timestamp = System.currentTimeMillis(),
            path = getPath(request)
        )
        
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(errorResponse)
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(
        ex: ResourceNotFoundException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Resource not found: {}", ex.message)
        
        val errorResponse = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = "Not Found",
            message = ex.message ?: "Resource not found",
            timestamp = System.currentTimeMillis(),
            path = getPath(request)
        )
        
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(errorResponse)
    }

    private fun getPath(request: WebRequest): String {
        return request.getDescription(false).removePrefix("uri=")
    }
}

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val timestamp: Long,
    val path: String
)

data class ValidationErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val timestamp: Long,
    val path: String,
    val fieldErrors: Map<String, String>
)

class ResourceNotFoundException(message: String) : RuntimeException(message)
class AccessDeniedException(message: String) : RuntimeException(message)