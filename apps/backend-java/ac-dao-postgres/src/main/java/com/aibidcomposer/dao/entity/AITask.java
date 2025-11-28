package com.aibidcomposer.dao.entity;

import com.aibidcomposer.common.biz.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * AI任务实体
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
@TableName(value = "ai_tasks", autoResultMap = true)
public class AITask extends BaseEntity {

    /**
     * 任务类型: parse-解析, analyze-分析, match-匹配, generate-生成, review-审核
     */
    @TableField("task_type")
    private String taskType;

    /**
     * 任务名称
     */
    @TableField("task_name")
    private String taskName;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 项目ID
     */
    @TableField("project_id")
    private Long projectId;

    /**
     * 文档ID
     */
    @TableField("document_id")
    private Long documentId;

    /**
     * 状态: pending-待处理, running-运行中, success-成功, failed-失败, cancelled-已取消
     */
    @TableField("status")
    private String status;

    /**
     * 优先级
     */
    @TableField("priority")
    private Integer priority;

    /**
     * 输入数据(JSON)
     */
    @TableField(value = "input_data", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> inputData;

    /**
     * 输出数据(JSON)
     */
    @TableField(value = "output_data", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> outputData;

    /**
     * 错误信息
     */
    @TableField("error_message")
    private String errorMessage;

    /**
     * 模型名称
     */
    @TableField("model_name")
    private String modelName;

    /**
     * 模型版本
     */
    @TableField("model_version")
    private String modelVersion;

    /**
     * Prompt模板
     */
    @TableField("prompt_template")
    private String promptTemplate;

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
     * 开始时间
     */
    @TableField("started_at")
    private LocalDateTime startedAt;

    /**
     * 完成时间
     */
    @TableField("completed_at")
    private LocalDateTime completedAt;

    /**
     * 持续时间(秒)
     */
    @TableField("duration_seconds")
    private Integer durationSeconds;

    /**
     * 重试次数
     */
    @TableField("retry_count")
    private Integer retryCount;

    /**
     * 最大重试次数
     */
    @TableField("max_retries")
    private Integer maxRetries;

    /**
     * 元数据(JSON)
     */
    @TableField(value = "metadata", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;
}
