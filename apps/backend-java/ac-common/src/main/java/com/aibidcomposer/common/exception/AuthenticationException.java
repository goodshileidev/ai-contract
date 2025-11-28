package com.aibidcomposer.common.exception;

import com.aibidcomposer.common.http.result.ResultCode;

/**
 * Authentication Exception
 *
 * <p>This exception is thrown when authentication fails. It typically corresponds
 * to HTTP 401 Unauthorized status code.</p>
 *
 * <p>Common authentication scenarios:
 * <ul>
 *   <li>Invalid credentials (username/password)</li>
 *   <li>Token expired or invalid</li>
 *   <li>User account locked or disabled</li>
 *   <li>Missing authentication credentials</li>
 * </ul>
 * </p>
 *
 * <p>Usage Examples:
 * <pre>{@code
 * // Simple authentication error
 * throw new AuthenticationException("Invalid credentials");
 *
 * // Specific authentication error
 * throw new AuthenticationException(ResultCode.TOKEN_EXPIRED, "JWT token has expired");
 *
 * // Authentication error with cause
 * throw new AuthenticationException("Authentication failed", cause);
 * }</pre>
 * </p>
 *
 * <p>需求编号: REQ-JAVA-COMMON-011</p>
 *
 * @author AIBidComposer Team
 * @version 1.0
 * @since 2025-11-26
 */
public class AuthenticationException extends BusinessException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor with message only
     *
     * @param message the error message
     */
    public AuthenticationException(String message) {
        super(ResultCode.AUTH_ERROR, message);
    }

    /**
     * Constructor with result code and message
     *
     * @param resultCode the result code
     * @param message    the error message
     */
    public AuthenticationException(ResultCode resultCode, String message) {
        super(resultCode, message);
    }

    /**
     * Constructor with message and cause
     *
     * @param message the error message
     * @param cause   the cause of the exception
     */
    public AuthenticationException(String message, Throwable cause) {
        super(ResultCode.AUTH_ERROR, message, cause);
    }

    /**
     * Constructor with result code, message, and cause
     *
     * @param resultCode the result code
     * @param message    the error message
     * @param cause      the cause of the exception
     */
    public AuthenticationException(ResultCode resultCode, String message, Throwable cause) {
        super(resultCode, message, cause);
    }
}
