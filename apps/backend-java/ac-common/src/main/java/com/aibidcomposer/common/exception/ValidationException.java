package com.aibidcomposer.common.exception;

import com.aibidcomposer.common.http.result.ResultCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Validation Exception
 *
 * <p>This exception is thrown when input validation fails. It can hold multiple
 * field-level validation errors and provides detailed error information for each field.</p>
 *
 * <p>Common validation scenarios:
 * <ul>
 *   <li>Required field missing</li>
 *   <li>Invalid field format (email, phone, etc.)</li>
 *   <li>Field value out of range</li>
 *   <li>Field length constraints violated</li>
 *   <li>Business rule validation failures</li>
 * </ul>
 * </p>
 *
 * <p>Usage Examples:
 * <pre>{@code
 * // Simple validation error
 * throw new ValidationException("Invalid input data");
 *
 * // Validation error with field-specific errors
 * Map<String, String> errors = new HashMap<>();
 * errors.put("email", "Invalid email format");
 * errors.put("age", "Age must be between 18 and 100");
 * throw new ValidationException("Validation failed", errors);
 *
 * // Add errors incrementally
 * ValidationException ex = new ValidationException("Validation failed");
 * ex.addFieldError("username", "Username already exists");
 * ex.addFieldError("password", "Password too weak");
 * throw ex;
 * }</pre>
 * </p>
 *
 * <p>需求编号: REQ-JAVA-COMMON-010</p>
 *
 * @author AIBidComposer Team
 * @version 1.0
 * @since 2025-11-26
 */
@Getter
public class ValidationException extends BusinessException {

    private static final long serialVersionUID = 1L;

    /**
     * Field-level validation errors
     * Key: field name, Value: error message
     */
    private Map<String, String> fieldErrors;

    /**
     * Constructor with message only
     *
     * @param message the error message
     */
    public ValidationException(String message) {
        super(ResultCode.VALIDATION_ERROR, message);
        this.fieldErrors = new HashMap<>();
    }

    /**
     * Constructor with message and field errors
     *
     * @param message     the error message
     * @param fieldErrors field-level validation errors
     */
    public ValidationException(String message, Map<String, String> fieldErrors) {
        super(ResultCode.VALIDATION_ERROR, message);
        this.fieldErrors = fieldErrors != null ? fieldErrors : new HashMap<>();
    }

    /**
     * Constructor with message and cause
     *
     * @param message the error message
     * @param cause   the cause of the exception
     */
    public ValidationException(String message, Throwable cause) {
        super(ResultCode.VALIDATION_ERROR, message, cause);
        this.fieldErrors = new HashMap<>();
    }

    /**
     * Add a field-level validation error
     *
     * @param field the field name
     * @param error the error message for this field
     * @return this exception instance (for method chaining)
     */
    public ValidationException addFieldError(String field, String error) {
        if (this.fieldErrors == null) {
            this.fieldErrors = new HashMap<>();
        }
        this.fieldErrors.put(field, error);
        return this;
    }

    /**
     * Check if there are any field errors
     *
     * @return true if field errors exist
     */
    public boolean hasFieldErrors() {
        return fieldErrors != null && !fieldErrors.isEmpty();
    }

    /**
     * Get the number of field errors
     *
     * @return the count of field errors
     */
    public int getFieldErrorCount() {
        return fieldErrors != null ? fieldErrors.size() : 0;
    }

    @Override
    public String toString() {
        return "ValidationException{" +
                "message='" + getMessage() + '\'' +
                ", fieldErrors=" + fieldErrors +
                '}';
    }
}
