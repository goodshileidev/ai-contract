package com.aibidcomposer.dao.entity;

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
 * 审批任务实体
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Data
@TableName(value = "approval_tasks", autoResultMap = true)
public class ApprovalTask implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 流程ID
     */
    @TableField("workflow_id")
    private Long workflowId;

    /**
     * 文档ID
     */
    @TableField("document_id")
    private Long documentId;

    /**
     * 任务名称
     */
    @TableField("task_name")
    private String taskName;

    /**
     * 步骤序号
     */
    @TableField("step_number")
    private Integer stepNumber;

    /**
     * 审批人ID
     */
    @TableField("assignee_id")
    private Long assigneeId;

    /**
     * 审批人类型: user-用户, role-角色
     */
    @TableField("assignee_type")
    private String assigneeType;

    /**
     * 状态: pending-待审批, approved-已通过, rejected-已拒绝, cancelled-已取消
     */
    @TableField("status")
    private String status;

    /**
     * 决策
     */
    @TableField("decision")
    private String decision;

    /**
     * 审批意见
     */
    @TableField("comments")
    private String comments;

    /**
     * 截止时间
     */
    @TableField("deadline")
    private LocalDateTime deadline;

    /**
     * 完成时间
     */
    @TableField("completed_at")
    private LocalDateTime completedAt;

    /**
     * 元数据(JSON)
     */
    @TableField(value = "metadata", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
