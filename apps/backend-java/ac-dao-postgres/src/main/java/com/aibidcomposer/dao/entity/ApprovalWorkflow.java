package com.aibidcomposer.dao.entity;

import com.aibidcomposer.common.biz.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 审批流程实体
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "approval_workflows", autoResultMap = true)
public class ApprovalWorkflow extends BaseEntity {

    /**
     * 流程名称
     */
    @TableField("name")
    private String name;

    /**
     * 流程代码(唯一)
     */
    @TableField("code")
    private String code;

    /**
     * 流程描述
     */
    @TableField("description")
    private String description;

    /**
     * 流程类型: sequential-串行, parallel-并行, conditional-条件, custom-自定义
     */
    @TableField("workflow_type")
    private String workflowType;

    /**
     * 组织ID
     */
    @TableField("organization_id")
    private Long organizationId;

    /**
     * 文档类型
     */
    @TableField("document_type")
    private String documentType;

    /**
     * 流程定义(JSON)
     */
    @TableField(value = "definition", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> definition;

    /**
     * 是否启用
     */
    @TableField("is_active")
    private Boolean isActive;

    /**
     * 是否默认流程
     */
    @TableField("is_default")
    private Boolean isDefault;

    /**
     * 元数据(JSON)
     */
    @TableField(value = "metadata", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;
}
