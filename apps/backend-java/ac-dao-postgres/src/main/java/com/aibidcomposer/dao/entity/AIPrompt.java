package com.aibidcomposer.dao.entity;

import com.aibidcomposer.common.biz.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * AI Prompt模板实体
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
@TableName(value = "ai_prompts", autoResultMap = true)
public class AIPrompt extends BaseEntity {

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 代码(唯一)
     */
    @TableField("code")
    private String code;

    /**
     * 分类
     */
    @TableField("category")
    private String category;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * Prompt模板
     */
    @TableField("prompt_template")
    private String promptTemplate;

    /**
     * 系统Prompt
     */
    @TableField("system_prompt")
    private String systemPrompt;

    /**
     * 变量列表(JSON)
     */
    @TableField(value = "variables", typeHandler = JacksonTypeHandler.class)
    private List<Map<String, Object>> variables;

    /**
     * 模型名称
     */
    @TableField("model_name")
    private String modelName;

    /**
     * 模型参数(JSON): temperature, max_tokens等
     */
    @TableField(value = "model_params", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> modelParams;

    /**
     * 是否启用
     */
    @TableField("is_active")
    private Boolean isActive;

    /**
     * 版本
     */
    @TableField("version")
    private String version;

    /**
     * 使用次数
     */
    @TableField("usage_count")
    private Integer usageCount;

    /**
     * 平均Token数
     */
    @TableField("average_tokens")
    private Integer averageTokens;

    /**
     * 平均成本
     */
    @TableField("average_cost")
    private BigDecimal averageCost;

    /**
     * 标签(数组)
     */
    @TableField(value = "tags", typeHandler = JacksonTypeHandler.class)
    private List<String> tags;

    /**
     * 元数据(JSON)
     */
    @TableField(value = "metadata", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;
}
