package com.aibidcomposer.dao.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 系统日志实体(时序数据)
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Data
@TableName(value = "system_logs", autoResultMap = true)
public class SystemLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 日志级别: DEBUG, INFO, WARNING, ERROR, CRITICAL
     */
    @TableField("log_level")
    private String logLevel;

    /**
     * 服务名称
     */
    @TableField("service")
    private String service;

    /**
     * 模块名称
     */
    @TableField("module")
    private String module;

    /**
     * 日志消息
     */
    @TableField("message")
    private String message;

    /**
     * 详情(JSON)
     */
    @TableField(value = "details", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> details;

    /**
     * 追踪ID
     */
    @TableField("trace_id")
    private String traceId;

    /**
     * Span ID
     */
    @TableField("span_id")
    private String spanId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * IP地址
     */
    @TableField("ip_address")
    private String ipAddress;

    /**
     * 创建时间(自动填充)
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
