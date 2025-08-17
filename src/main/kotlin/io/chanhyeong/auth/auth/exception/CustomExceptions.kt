package io.chanhyeong.auth.auth.exception

open class BusinessException(
    val code: ExceptionCode,
    override val message: String? = null
) : RuntimeException(message)

class AuthenticationFailedException(
    code: ExceptionCode = ExceptionCode.INVALID_CREDENTIALS,
    message: String? = null
) : BusinessException(code, message)

class OtpException(
    code: ExceptionCode,
    message: String? = null
) : BusinessException(code, message)

class UserNotFoundException(
    message: String? = null
) : BusinessException(ExceptionCode.USER_NOT_FOUND, message)

class UserAlreadyExistsException(
    message: String? = null
) : BusinessException(ExceptionCode.USER_ALREADY_EXISTS, message)

class InvalidPasswordException(
    message: String? = null
) : BusinessException(ExceptionCode.INVALID_PASSWORD, message)

class ResourceNotFoundException(
    message: String? = null
) : BusinessException(ExceptionCode.RESOURCE_NOT_FOUND, message)

class AccessDeniedException(
    message: String? = null
) : BusinessException(ExceptionCode.ACCESS_DENIED, message)