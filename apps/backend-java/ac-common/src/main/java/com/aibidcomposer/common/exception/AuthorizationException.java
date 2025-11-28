package com.aibidcomposer.common.exception;

import com.aibidcomposer.common.http.result.ResultCode;

/**
 * Authorization Exception
 *
 * <p>This exception is thrown when a user attempts to perform an operation they
 * don't have permission for. It typically corresponds to HTTP 403 Forbidden status code.</p>
 *
 * <p>Common authorization scenarios:
 * <ul>
 *   <li>Insufficient permissions for the requested operation</li>
 *   <li>Access to resource denied</li>
 *   <li>Operation not allowed for current user role</li>
 *   <li>Resource ownership mismatch</li>
 * </ul>
 * </p>
 *
 * <p>Usage Examples:
 * <pre>{@code
 * // Simple authorization error
 * throw new AuthorizationException("Access denied");
 *
 * // Specific authorization error
 * throw new AuthorizationException("You don't have permission to delete this project");
 *
 * // Authorization error with result code
 * throw new AuthorizationException(ResultCode.PERMISSION_DENIED, "Insufficient permissions");
 * }</pre>
 * </p>
 *
 * <p>需求编号: REQ-JAVA-COMMON-012</p>
 *
 * @author AIBidComposer Team
 * @version 1.0
 * @since 2025-11-26
 */
public class AuthorizationException extends BusinessException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor with message only
     *
     * @param message the error message
     */
    public AuthorizationException(String message) {
        super(ResultCode.FORBIDDEN, message);
    }

    /**
     * Constructor with result code and message
     *
     * @param resultCode the result code
     * @param message    the error message
     */
    public AuthorizationException(ResultCode resultCode, String message) {
        super(resultCode, message);
    }

    /**
     * Constructor with message and cause
     *
     * @param message the error message
     * @param cause   the cause of the exception
     */
    public AuthorizationException(String message, Throwable cause) {
        super(ResultCode.FORBIDDEN, message, cause);
    }

    /**
     * Constructor with result code, message, and cause
     *
     * @param resultCode the result code
     * @param message    the error message
     * @param cause      the cause of the exception
     */
    public AuthorizationException(ResultCode resultCode, String message, Throwable cause) {
        super(resultCode, message, cause);
    }
}
