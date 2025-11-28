package com.aibidcomposer.common.biz.exception;

import com.aibidcomposer.common.exception.AuthenticationException;
import com.aibidcomposer.common.exception.AuthorizationException;
import com.aibidcomposer.common.exception.BusinessException;
import com.aibidcomposer.common.exception.ResourceNotFoundException;
import com.aibidcomposer.common.exception.ServiceException;
import com.aibidcomposer.common.exception.ValidationException;
import com.aibidcomposer.common.http.result.R;
import com.aibidcomposer.common.http.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 *
 * 需求编号: REQ-JAVA-COMMON-BIZ-001
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常处理
     *
     * @param e 业务异常
     * @return 响应结果
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<R<Void>> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(R.fail(e.getCode(), e.getMessage()));
    }

    /**
     * 服务异常处理
     *
     * @param e 服务异常
     * @return 响应结果
     */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<R<Void>> handleServiceException(ServiceException e) {
        log.error("服务异常: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(R.error(e.getMessage()));
    }

    /**
     * 资源未找到异常处理
     *
     * @param e 资源未找到异常
     * @return 响应结果
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<R<Void>> handleResourceNotFoundException(ResourceNotFoundException e) {
        log.warn("资源未找到: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(R.fail(ResultCode.NOT_FOUND, e.getMessage()));
    }

    /**
     * 认证异常处理
     *
     * @param e 认证异常
     * @return 响应结果
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<R<Void>> handleAuthenticationException(AuthenticationException e) {
        log.warn("认证失败: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(R.fail(ResultCode.UNAUTHORIZED, e.getMessage()));
    }

    /**
     * 授权异常处理
     *
     * @param e 授权异常
     * @return 响应结果
     */
    @ExceptionHandler({AuthorizationException.class, AccessDeniedException.class})
    public ResponseEntity<R<Void>> handleAuthorizationException(Exception e) {
        log.warn("授权失败: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(R.fail(ResultCode.FORBIDDEN, "没有权限访问该资源"));
    }

    /**
     * 参数验证异常处理
     *
     * @param e 验证异常
     * @return 响应结果
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<R<Map<String, String>>> handleValidationException(ValidationException e) {
        log.warn("参数验证失败: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(R.fail(ResultCode.PARAM_ERROR, e.getMessage(), e.getErrors()));
    }

    /**
     * 方法参数验证异常处理（@Valid）
     *
     * @param e 方法参数验证异常
     * @return 响应结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<R<Map<String, String>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        log.warn("方法参数验证失败");

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(R.fail(ResultCode.PARAM_ERROR, "参数验证失败", errors));
    }

    /**
     * 绑定异常处理
     *
     * @param e 绑定异常
     * @return 响应结果
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<R<Map<String, String>>> handleBindException(BindException e) {
        log.warn("参数绑定失败");

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(R.fail(ResultCode.PARAM_ERROR, "参数绑定失败", errors));
    }

    /**
     * 非法参数异常处理
     *
     * @param e 非法参数异常
     * @return 响应结果
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<R<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("非法参数: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(R.fail(ResultCode.PARAM_ERROR, e.getMessage()));
    }

    /**
     * 未知异常处理
     *
     * @param e 异常
     * @return 响应结果
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<R<Void>> handleException(Exception e) {
        log.error("系统异常", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(R.error("系统繁忙，请稍后再试"));
    }
}
