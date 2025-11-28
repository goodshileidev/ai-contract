package com.aibidcomposer.dao.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 模板使用日志实体
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Data
@TableName(value = "template_usage_logs", autoResultMap = true)
public class TemplateUsageLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模板ID
     */
    @TableField("template_id")
    private Long templateId;

    /**
     * 文档ID
     */
    @TableField("document_id")
    private Long documentId;

    /**
     * 项目ID
     */
    @TableField("project_id")
    private Long projectId;

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
     * 使用时间
     */
    @TableField(value = "used_at", fill = FieldFill.INSERT)
    private LocalDateTime usedAt;
}
