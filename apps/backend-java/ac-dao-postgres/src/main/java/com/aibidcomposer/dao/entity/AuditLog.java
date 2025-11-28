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
 * 审计日志实体(时序数据)
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Data
@TableName(value = "audit_logs", autoResultMap = true)
public class AuditLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 组织ID
     */
    @TableField("organization_id")
    private Long organizationId;

    /**
     * 操作
     */
    @TableField("action")
    private String action;

    /**
     * 资源类型
     */
    @TableField("resource_type")
    private String resourceType;

    /**
     * 资源ID
     */
    @TableField("resource_id")
    private Long resourceId;

    /**
     * 资源名称
     */
    @TableField("resource_name")
    private String resourceName;

    /**
     * 详情(JSON)
     */
    @TableField(value = "details", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> details;

    /**
     * 变更内容(JSON) - before/after
     */
    @TableField(value = "changes", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> changes;

    /**
     * IP地址
     */
    @TableField("ip_address")
    private String ipAddress;

    /**
     * User Agent
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * 状态
     */
    @TableField("status")
    private String status;

    /**
     * 错误信息
     */
    @TableField("error_message")
    private String errorMessage;

    /**
     * 创建时间(自动填充)
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
