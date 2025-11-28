package com.aibidcomposer.common.http.result;

import lombok.Getter;

/**
 * Result Status Code Enumeration
 *
 * <p>Defines standard HTTP and application-specific result codes used throughout
 * the AIBidComposer platform. These codes represent the outcome of API operations
 * and provide consistent error handling across all services.</p>
 *
 * <p>Code Ranges:
 * <ul>
 *   <li>2xx - Success codes</li>
 *   <li>4xx - Client error codes</li>
 *   <li>5xx - Server error codes</li>
 *   <li>6xx - Custom business error codes</li>
 *   <li>7xx - Data layer error codes</li>
 * </ul>
 * </p>
 *
 * <p>需求编号: REQ-JAVA-COMMON-005</p>
 *
 * @author AIBidComposer Team
 * @version 1.0
 * @since 2025-11-26
 */
@Getter
public enum ResultCode {

    // ==================== Success Codes (2xx) ====================

    /**
     * Operation successful
     */
    SUCCESS(200, "Operation successful"),

    /**
     * Resource created successfully
     */
    CREATED(201, "Resource created successfully"),

    /**
     * Request accepted, processing in background
     */
    ACCEPTED(202, "Request accepted"),

    // ==================== Client Error Codes (4xx) ====================

    /**
     * Bad request - invalid request format or syntax
     */
    BAD_REQUEST(400, "Bad request"),

    /**
     * Unauthorized - authentication required
     */
    UNAUTHORIZED(401, "Unauthorized"),

    /**
     * Forbidden - insufficient permissions
     */
    FORBIDDEN(403, "Forbidden"),

    /**
     * Resource not found
     */
    NOT_FOUND(404, "Resource not found"),

    /**
     * Request method not allowed
     */
    METHOD_NOT_ALLOWED(405, "Method not allowed"),

    /**
     * Request timeout
     */
    REQUEST_TIMEOUT(408, "Request timeout"),

    /**
     * Resource conflict
     */
    CONFLICT(409, "Resource conflict"),

    /**
     * Too many requests - rate limit exceeded
     */
    TOO_MANY_REQUESTS(429, "Too many requests"),

    // ==================== Server Error Codes (5xx) ====================

    /**
     * Internal server error
     */
    SERVER_ERROR(500, "Internal server error"),

    /**
     * Service unavailable
     */
    SERVICE_UNAVAILABLE(503, "Service unavailable"),

    /**
     * Gateway timeout
     */
    GATEWAY_TIMEOUT(504, "Gateway timeout"),

    // ==================== Custom Business Error Codes (6xx) ====================

    /**
     * Invalid parameter
     */
    INVALID_PARAM(600, "Invalid parameter"),

    /**
     * Missing required parameter
     */
    MISSING_PARAM(601, "Missing required parameter"),

    /**
     * Parameter type mismatch
     */
    PARAM_TYPE_ERROR(602, "Parameter type mismatch"),

    /**
     * Unknown parameter
     */
    UNKNOWN_PARAM(603, "Unknown parameter"),

    /**
     * Parameter validation failed
     */
    VALIDATION_ERROR(604, "Validation failed"),

    /**
     * Business logic error
     */
    BUSINESS_ERROR(610, "Business logic error"),

    /**
     * Duplicate resource
     */
    DUPLICATE_RESOURCE(611, "Duplicate resource"),

    /**
     * Resource already exists
     */
    RESOURCE_EXISTS(612, "Resource already exists"),

    /**
     * Invalid resource state
     */
    INVALID_STATE(613, "Invalid resource state"),

    /**
     * Operation not allowed
     */
    OPERATION_NOT_ALLOWED(614, "Operation not allowed"),

    // ==================== Authentication & Authorization Codes (71x) ====================

    /**
     * Authentication failed
     */
    AUTH_ERROR(710, "Authentication failed"),

    /**
     * Invalid credentials
     */
    INVALID_CREDENTIALS(711, "Invalid credentials"),

    /**
     * Token expired
     */
    TOKEN_EXPIRED(712, "Token expired"),

    /**
     * Invalid token
     */
    INVALID_TOKEN(713, "Invalid token"),

    /**
     * Insufficient permissions
     */
    PERMISSION_DENIED(714, "Insufficient permissions"),

    // ==================== Data Layer Error Codes (70x) ====================

    /**
     * Database error
     */
    DATABASE_ERROR(700, "Database error"),

    /**
     * SQL execution error
     */
    SQL_ERROR(706, "SQL execution error"),

    /**
     * Data integrity violation
     */
    DATA_INTEGRITY_ERROR(707, "Data integrity violation"),

    /**
     * Cache error
     */
    CACHE_ERROR(708, "Cache error"),

    // ==================== External Service Error Codes (72x) ====================

    /**
     * External service error
     */
    EXTERNAL_SERVICE_ERROR(720, "External service error"),

    /**
     * AI service error
     */
    AI_SERVICE_ERROR(721, "AI service error"),

    /**
     * File storage error
     */
    FILE_STORAGE_ERROR(722, "File storage error"),

    /**
     * Message queue error
     */
    MESSAGE_QUEUE_ERROR(723, "Message queue error"),

    // ==================== Generic Error Codes (1xxxx) ====================

    /**
     * Unknown exception
     */
    UNKNOWN_EXCEPTION(10000, "Unknown exception"),
    ;

    /**
     * Result code
     */
    private final Integer code;

    /**
     * Result message
     */
    private final String message;

    /**
     * Constructor
     *
     * @param code    the result code
     * @param message the result message
     */
    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Check if this is a success code
     *
     * @return true if code is 2xx
     */
    public boolean isSuccess() {
        return code >= 200 && code < 300;
    }

    /**
     * Check if this is an error code
     *
     * @return true if code is not 2xx
     */
    public boolean isError() {
        return !isSuccess();
    }

    /**
     * Get ResultCode by code value
     *
     * @param code the code value
     * @return ResultCode or null if not found
     */
    public static ResultCode valueOf(Integer code) {
        for (ResultCode resultCode : values()) {
            if (resultCode.getCode().equals(code)) {
                return resultCode;
            }
        }
        return null;
    }
}
