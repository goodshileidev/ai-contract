package com.aibidcomposer.common.http.result;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Unified API Response Wrapper
 *
 * <p>This class provides a standardized response structure for all API endpoints
 * in the AIBidComposer platform. It wraps the actual response data along with
 * status information, error codes, and messages.</p>
 *
 * <p>Response Structure:
 * <pre>{@code
 * {
 *   "success": true,
 *   "code": 200,
 *   "message": "Operation successful",
 *   "data": { ... },
 *   "timestamp": "2025-11-26T10:00:00"
 * }
 * }</pre>
 * </p>
 *
 * <p>Usage Examples:
 * <pre>{@code
 * // Success with data
 * return R.success(user);
 *
 * // Success with custom message
 * return R.success("User created successfully", user);
 *
 * // Error response
 * return R.error("User not found");
 *
 * // Error with custom code
 * return R.error(ResultCode.INVALID_CREDENTIALS, "Invalid username or password");
 * }</pre>
 * </p>
 *
 * <p>需求编号: REQ-JAVA-COMMON-006</p>
 *
 * @param <T> the type of data being returned
 * @author AIBidComposer Team
 * @version 1.0
 * @since 2025-11-26
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Indicates whether the operation was successful
     */
    private Boolean success;

    /**
     * Response code (based on ResultCode enum)
     */
    private Integer code;

    /**
     * Response message
     */
    private String message;

    /**
     * Response data payload
     */
    private T data;

    /**
     * Timestamp of the response
     */
    private LocalDateTime timestamp;

    /**
     * Private constructor to enforce factory method usage
     *
     * @param success whether operation was successful
     * @param code    response code
     * @param message response message
     * @param data    response data
     */
    private R(Boolean success, Integer code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Default constructor for serialization
     */
    public R() {
        this.timestamp = LocalDateTime.now();
    }

    // ==================== Success Response Factory Methods ====================

    /**
     * Create a success response with data
     *
     * @param data the response data
     * @param <T>  the type of data
     * @return success response with data
     */
    public static <T> R<T> success(T data) {
        return new R<>(true, ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * Create a success response with custom message and data
     *
     * @param message custom success message
     * @param data    the response data
     * @param <T>     the type of data
     * @return success response with message and data
     */
    public static <T> R<T> success(String message, T data) {
        return new R<>(true, ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * Create a success response with message only (no data)
     *
     * @param message success message
     * @param <T>     the type of data
     * @return success response with message
     */
    public static <T> R<T> success(String message) {
        return new R<>(true, ResultCode.SUCCESS.getCode(), message, null);
    }

    /**
     * Create a simple success response
     *
     * @param <T> the type of data
     * @return success response
     */
    public static <T> R<T> success() {
        return new R<>(true, ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    /**
     * Create a success response with custom result code
     *
     * @param resultCode the result code
     * @param data       the response data
     * @param <T>        the type of data
     * @return success response
     */
    public static <T> R<T> success(ResultCode resultCode, T data) {
        return new R<>(true, resultCode.getCode(), resultCode.getMessage(), data);
    }

    // ==================== Error Response Factory Methods ====================

    /**
     * Create an error response with default server error code
     *
     * @param message error message
     * @param <T>     the type of data
     * @return error response
     */
    public static <T> R<T> error(String message) {
        return new R<>(false, ResultCode.SERVER_ERROR.getCode(), message, null);
    }

    /**
     * Create an error response with result code
     *
     * @param resultCode the result code
     * @param <T>        the type of data
     * @return error response
     */
    public static <T> R<T> error(ResultCode resultCode) {
        return new R<>(false, resultCode.getCode(), resultCode.getMessage(), null);
    }

    /**
     * Create an error response with result code and custom message
     *
     * @param resultCode the result code
     * @param message    custom error message
     * @param <T>        the type of data
     * @return error response
     */
    public static <T> R<T> error(ResultCode resultCode, String message) {
        return new R<>(false, resultCode.getCode(), message, null);
    }

    /**
     * Create an error response with custom code and message
     *
     * @param code    error code
     * @param message error message
     * @param <T>     the type of data
     * @return error response
     */
    public static <T> R<T> error(Integer code, String message) {
        return new R<>(false, code, message, null);
    }

    /**
     * Create an error response with result code, message, and data
     *
     * @param resultCode the result code
     * @param message    error message
     * @param data       error details data
     * @param <T>        the type of data
     * @return error response
     */
    public static <T> R<T> error(ResultCode resultCode, String message, T data) {
        return new R<>(false, resultCode.getCode(), message, data);
    }

    // ==================== Specialized Response Factory Methods ====================

    /**
     * Create a "not found" error response
     *
     * @param message error message
     * @param <T>     the type of data
     * @return not found error response
     */
    public static <T> R<T> notFound(String message) {
        return error(ResultCode.NOT_FOUND, message);
    }

    /**
     * Create an "unauthorized" error response
     *
     * @param message error message
     * @param <T>     the type of data
     * @return unauthorized error response
     */
    public static <T> R<T> unauthorized(String message) {
        return error(ResultCode.UNAUTHORIZED, message);
    }

    /**
     * Create a "forbidden" error response
     *
     * @param message error message
     * @param <T>     the type of data
     * @return forbidden error response
     */
    public static <T> R<T> forbidden(String message) {
        return error(ResultCode.FORBIDDEN, message);
    }

    /**
     * Create a "bad request" error response
     *
     * @param message error message
     * @param <T>     the type of data
     * @return bad request error response
     */
    public static <T> R<T> badRequest(String message) {
        return error(ResultCode.BAD_REQUEST, message);
    }

    /**
     * Create a validation error response
     *
     * @param message validation error message
     * @param <T>     the type of data
     * @return validation error response
     */
    public static <T> R<T> validationError(String message) {
        return error(ResultCode.VALIDATION_ERROR, message);
    }

    /**
     * Create a validation error response with error details
     *
     * @param message validation error message
     * @param errors  validation error details
     * @param <T>     the type of data
     * @return validation error response
     */
    public static <T> R<T> validationError(String message, T errors) {
        return error(ResultCode.VALIDATION_ERROR, message, errors);
    }

    // ==================== Utility Methods ====================

    /**
     * Check if the response indicates success
     *
     * @return true if success
     */
    public boolean isSuccess() {
        return this.success != null && this.success;
    }

    /**
     * Check if the response indicates error
     *
     * @return true if error
     */
    public boolean isError() {
        return !isSuccess();
    }

    @Override
    public String toString() {
        return "R{" +
                "success=" + success +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                '}';
    }
}
