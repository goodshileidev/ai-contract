package com.aibidcomposer.dao.entity;

import com.aibidcomposer.common.biz.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 项目需求实体
 *
 * 需求编号: REQ-JAVA-DAO-002
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "project_requirements", autoResultMap = true)
public class ProjectRequirement extends BaseEntity {

    /**
     * 项目ID
     */
    @TableField("project_id")
    private Long projectId;

    /**
     * 需求类型: technical-技术, business-业务, compliance-合规, resource-资源, other-其他
     */
    @TableField("requirement_type")
    private String requirementType;

    /**
     * 需求分类
     */
    @TableField("category")
    private String category;

    /**
     * 需求标题
     */
    @TableField("title")
    private String title;

    /**
     * 需求描述
     */
    @TableField("description")
    private String description;

    /**
     * 优先级: low-低, medium-中, high-高, critical-关键
     */
    @TableField("priority")
    private String priority;

    /**
     * 是否强制要求
     */
    @TableField("is_mandatory")
    private Boolean isMandatory;

    /**
     * 评分权重
     */
    @TableField("score_weight")
    private BigDecimal scoreWeight;

    /**
     * 匹配状态: pending-待匹配, matched-已匹配, partial-部分匹配, unmatched-未匹配
     */
    @TableField("match_status")
    private String matchStatus;

    /**
     * 匹配分数(0-100)
     */
    @TableField("match_score")
    private BigDecimal matchScore;

    /**
     * 匹配详情(JSON)
     */
    @TableField(value = "match_details", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> matchDetails;

    /**
     * 来源文档
     */
    @TableField("source")
    private String source;

    /**
     * 来源页码
     */
    @TableField("source_page")
    private Integer sourcePage;

    /**
     * 提取方式: ai-AI提取, manual-手动录入
     */
    @TableField("extracted_by")
    private String extractedBy;

    /**
     * 元数据(JSON)
     */
    @TableField(value = "metadata", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;
}
