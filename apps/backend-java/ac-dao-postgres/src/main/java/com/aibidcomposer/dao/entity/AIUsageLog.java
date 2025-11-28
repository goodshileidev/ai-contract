package com.aibidcomposer.dao.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI使用日志实体(时序数据)
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Data
@TableName(value = "ai_usage_logs", autoResultMap = true)
public class AIUsageLog implements Serializable {

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
     * 任务ID
     */
    @TableField("task_id")
    private Long taskId;

    /**
     * 模型名称
     */
    @TableField("model_name")
    private String modelName;

    /**
     * 操作类型
     */
    @TableField("operation_type")
    private String operationType;

    /**
     * Prompt Token数
     */
    @TableField("prompt_tokens")
    private Integer promptTokens;

    /**
     * Completion Token数
     */
    @TableField("completion_tokens")
    private Integer completionTokens;

    /**
     * 总Token数
     */
    @TableField("total_tokens")
    private Integer totalTokens;

    /**
     * 成本
     */
    @TableField("cost")
    private BigDecimal cost;

    /**
     * 延迟(毫秒)
     */
    @TableField("latency_ms")
    private Integer latencyMs;

    /**
     * 状态
     */
    @TableField("status")
    private String status;

    /**
     * 创建时间(自动填充)
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
