package com.aibidcomposer.common.exception;

import com.aibidcomposer.common.http.result.ResultCode;

/**
 * Resource Not Found Exception
 *
 * <p>This exception is thrown when a requested resource cannot be found in the system.
 * It typically corresponds to HTTP 404 Not Found status code.</p>
 *
 * <p>Common scenarios:
 * <ul>
 *   <li>User with specified ID does not exist</li>
 *   <li>Project with specified ID cannot be found</li>
 *   <li>Document does not exist</li>
 *   <li>Template not found</li>
 * </ul>
 * </p>
 *
 * <p>Usage Examples:
 * <pre>{@code
 * // Simple not found exception
 * throw new ResourceNotFoundException("User not found");
 *
 * // Not found with resource type and ID
 * throw new ResourceNotFoundException("User", userId);
 *
 * // Not found with custom message
 * throw new ResourceNotFoundException("Project not found with code: " + projectCode);
 * }</pre>
 * </p>
 *
 * <p>需求编号: REQ-JAVA-COMMON-009</p>
 *
 * @author AIBidComposer Team
 * @version 1.0
 * @since 2025-11-26
 */
public class ResourceNotFoundException extends BusinessException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor with message only
     *
     * @param message the error message
     */
    public ResourceNotFoundException(String message) {
        super(ResultCode.NOT_FOUND, message);
    }

    /**
     * Constructor with resource type and ID
     *
     * @param resourceType the type of resource (e.g., "User", "Project")
     * @param resourceId   the ID of the resource
     */
    public ResourceNotFoundException(String resourceType, Object resourceId) {
        super(ResultCode.NOT_FOUND,
              String.format("%s not found with id: %s", resourceType, resourceId));
    }

    /**
     * Constructor with message and cause
     *
     * @param message the error message
     * @param cause   the cause of the exception
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(ResultCode.NOT_FOUND, message, cause);
    }

    /**
     * Constructor with resource type, ID, and cause
     *
     * @param resourceType the type of resource
     * @param resourceId   the ID of the resource
     * @param cause        the cause of the exception
     */
    public ResourceNotFoundException(String resourceType, Object resourceId, Throwable cause) {
        super(ResultCode.NOT_FOUND,
              String.format("%s not found with id: %s", resourceType, resourceId),
              cause);
    }
}
