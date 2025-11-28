package com.aibidcomposer.common.exception;

/**
 * Base Service Exception
 *
 * <p>This is the base exception class for all service layer exceptions in the
 * AIBidComposer platform. It extends RuntimeException to avoid requiring explicit
 * exception handling while still providing meaningful error information.</p>
 *
 * <p>Service exceptions represent errors that occur during business logic processing,
 * such as validation failures, business rule violations, or service-level errors.</p>
 *
 * <p>This exception should be extended by specific exception classes for different
 * types of service errors (e.g., BusinessException, ValidationException, etc.).</p>
 *
 * <p>需求编号: REQ-JAVA-COMMON-007</p>
 *
 * @author AIBidComposer Team
 * @version 1.0
 * @since 2025-11-26
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor
     */
    public ServiceException() {
        super();
    }

    /**
     * Constructor with message only
     *
     * @param message the detail message
     */
    public ServiceException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with cause only
     *
     * @param cause the cause of the exception
     */
    public ServiceException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor with full exception configuration
     *
     * @param message            the detail message
     * @param cause              the cause of the exception
     * @param enableSuppression  whether suppression is enabled
     * @param writableStackTrace whether the stack trace should be writable
     */
    public ServiceException(String message, Throwable cause,
                            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
