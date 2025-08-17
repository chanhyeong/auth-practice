package io.chanhyeong.auth.auth.exception

import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.dao.DataAccessException
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class GlobalExceptionHandler(
    private val messageSource: MessageSource
) {
    
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(
        ex: BusinessException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Business exception: {}", ex.message)
        
        val message = ex.message ?: getLocalizedMessage(ex.code.messageKey)
        
        val errorResponse = ErrorResponse(
            status = ex.code.httpStatus.value(),
            error = ex.code.httpStatus.reasonPhrase,
            message = message,
            code = ex.code.name,
            path = getPath(request)
        )
        
        return ResponseEntity
            .status(ex.code.httpStatus)
            .body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception, 
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error occurred", ex)
        
        val errorResponse = ErrorResponse(
            status = ExceptionCode.INTERNAL_SERVER_ERROR.httpStatus.value(),
            error = ExceptionCode.INTERNAL_SERVER_ERROR.httpStatus.reasonPhrase,
            message = getLocalizedMessage(ExceptionCode.INTERNAL_SERVER_ERROR.messageKey),
            code = ExceptionCode.INTERNAL_SERVER_ERROR.name,
            path = getPath(request)
        )
        
        return ResponseEntity
            .status(ExceptionCode.INTERNAL_SERVER_ERROR.httpStatus)
            .body(errorResponse)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        ex: IllegalArgumentException, 
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("IllegalArgumentException: {}", ex.message)
        
        val errorResponse = ErrorResponse(
            status = ExceptionCode.INVALID_INPUT.httpStatus.value(),
            error = ExceptionCode.INVALID_INPUT.httpStatus.reasonPhrase,
            message = ex.message ?: getLocalizedMessage(ExceptionCode.INVALID_INPUT.messageKey),
            code = ExceptionCode.INVALID_INPUT.name,
            path = getPath(request)
        )
        
        return ResponseEntity
            .status(ExceptionCode.INVALID_INPUT.httpStatus)
            .body(errorResponse)
    }

    @ExceptionHandler(DataAccessException::class)
    fun handleDataAccessException(
        ex: DataAccessException, 
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error("Database error occurred", ex)
        
        val errorResponse = ErrorResponse(
            status = ExceptionCode.DATABASE_ERROR.httpStatus.value(),
            error = ExceptionCode.DATABASE_ERROR.httpStatus.reasonPhrase,
            message = getLocalizedMessage(ExceptionCode.DATABASE_ERROR.messageKey),
            code = ExceptionCode.DATABASE_ERROR.name,
            path = getPath(request)
        )
        
        return ResponseEntity
            .status(ExceptionCode.DATABASE_ERROR.httpStatus)
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
            status = ExceptionCode.VALIDATION_FAILED.httpStatus.value(),
            error = ExceptionCode.VALIDATION_FAILED.httpStatus.reasonPhrase,
            message = getLocalizedMessage(ExceptionCode.VALIDATION_FAILED.messageKey),
            code = ExceptionCode.VALIDATION_FAILED.name,
            path = getPath(request),
            fieldErrors = fieldErrors
        )
        
        return ResponseEntity
            .status(ExceptionCode.VALIDATION_FAILED.httpStatus)
            .body(errorResponse)
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(
        ex: AuthenticationException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Authentication failed: {}", ex.message)
        
        val exceptionCode = when (ex) {
            is BadCredentialsException -> ExceptionCode.INVALID_CREDENTIALS
            else -> ExceptionCode.INVALID_CREDENTIALS
        }
        
        val errorResponse = ErrorResponse(
            status = exceptionCode.httpStatus.value(),
            error = exceptionCode.httpStatus.reasonPhrase,
            message = getLocalizedMessage(exceptionCode.messageKey),
            code = exceptionCode.name,
            path = getPath(request)
        )
        
        return ResponseEntity
            .status(exceptionCode.httpStatus)
            .body(errorResponse)
    }


    private fun getPath(request: WebRequest): String {
        return request.getDescription(false).removePrefix("uri=")
    }
    
    private fun getLocalizedMessage(messageKey: String, vararg args: Any): String {
        return try {
            messageSource.getMessage(messageKey, args, LocaleContextHolder.getLocale())
        } catch (e: Exception) {
            messageKey
        }
    }
}

