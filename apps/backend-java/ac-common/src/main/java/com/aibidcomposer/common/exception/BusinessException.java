package com.aibidcomposer.common.exception;

import com.aibidcomposer.common.http.result.ResultCode;
import lombok.Getter;

/**
 * Business Exception
 *
 * <p>This exception is used to represent business logic errors in the AIBidComposer
 * platform. It extends ServiceException and adds support for error codes and
 * additional error details.</p>
 *
 * <p>Business exceptions are typically thrown when:
 * <ul>
 *   <li>Business rules are violated</li>
 *   <li>Invalid business operations are attempted</li>
 *   <li>Business validation fails</li>
 *   <li>Resource conflicts occur</li>
 * </ul>
 * </p>
 *
 * <p>Usage Examples:
 * <pre>{@code
 * // Simple business exception
 * throw new BusinessException("User already exists");
 *
 * // Business exception with error code
 * throw new BusinessException(ResultCode.DUPLICATE_RESOURCE, "Email already registered");
 *
 * // Business exception with error code and additional parameters
 * throw new BusinessException(ResultCode.INVALID_STATE, "Order cannot be cancelled", "status=SHIPPED");
 * }</pre>
 * </p>
 *
 * <p>需求编号: REQ-JAVA-COMMON-008</p>
 *
 * @author AIBidComposer Team
 * @version 1.0
 * @since 2025-11-26
 */
@Getter
public class BusinessException extends ServiceException {

    private static final long serialVersionUID = 1L;

    /**
     * Error code
     */
    private Integer code;

    /**
     * Additional error parameters or details
     */
    private String params;

    /**
     * Underlying exception
     */
    private Exception exception;

    /**
     * Default constructor
     */
    public BusinessException() {
        super();
    }

    /**
     * Constructor with message only
     *
     * @param message the error message
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     *
     * @param message the error message
     * @param cause   the cause of the exception
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with result code
     *
     * @param resultCode the result code
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    /**
     * Constructor with result code and custom message
     *
     * @param resultCode the result code
     * @param message    the custom error message
     */
    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }

    /**
     * Constructor with result code, message, and cause
     *
     * @param resultCode the result code
     * @param message    the error message
     * @param cause      the cause of the exception
     */
    public BusinessException(ResultCode resultCode, String message, Throwable cause) {
        super(message, cause);
        this.code = resultCode.getCode();
    }

    /**
     * Constructor with code and message
     *
     * @param code    the error code
     * @param message the error message
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Constructor with code, message, and parameters
     *
     * @param code    the error code
     * @param message the error message
     * @param params  additional error parameters
     */
    public BusinessException(Integer code, String message, String params) {
        super(message);
        this.code = code;
        this.params = params;
    }

    /**
     * Constructor with code, message, parameters, and underlying exception
     *
     * @param code      the error code
     * @param message   the error message
     * @param params    additional error parameters
     * @param exception the underlying exception
     */
    public BusinessException(Integer code, String message, String params, Exception exception) {
        super(message, exception);
        this.code = code;
        this.params = params;
        this.exception = exception;
    }

    /**
     * Constructor with result code, message, and parameters
     *
     * @param resultCode the result code
     * @param message    the error message
     * @param params     additional error parameters
     */
    public BusinessException(ResultCode resultCode, String message, String params) {
        super(message);
        this.code = resultCode.getCode();
        this.params = params;
    }

    @Override
    public String toString() {
        return "BusinessException{" +
                "code=" + code +
                ", message='" + getMessage() + '\'' +
                ", params='" + params + '\'' +
                '}';
    }
}
