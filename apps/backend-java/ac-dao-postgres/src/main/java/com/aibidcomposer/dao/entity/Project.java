package com.aibidcomposer.dao.entity;

import com.aibidcomposer.common.biz.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 项目实体
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
@TableName(value = "projects", autoResultMap = true)
public class Project extends BaseEntity {

    /**
     * 项目名称
     */
    @TableField("name")
    private String name;

    /**
     * 项目编号
     */
    @TableField("code")
    private String code;

    /**
     * 项目描述
     */
    @TableField("description")
    private String description;

    /**
     * 所属组织ID
     */
    @TableField("organization_id")
    private Long organizationId;

    /**
     * 招标类型: government-政府, enterprise-企业, international-国际, other-其他
     */
    @TableField("bidding_type")
    private String biddingType;

    /**
     * 所属行业
     */
    @TableField("industry")
    private String industry;

    /**
     * 预算金额
     */
    @TableField("budget_amount")
    private BigDecimal budgetAmount;

    /**
     * 货币单位
     */
    @TableField("currency")
    private String currency;

    /**
     * 项目开始日期
     */
    @TableField("start_date")
    private LocalDate startDate;

    /**
     * 项目结束日期
     */
    @TableField("end_date")
    private LocalDate endDate;

    /**
     * 投标截止时间
     */
    @TableField("submission_deadline")
    private LocalDateTime submissionDeadline;

    /**
     * 项目状态: draft-草稿, in_progress-进行中, review-评审中, submitted-已提交,
     *            won-中标, lost-未中标, archived-已归档
     */
    @TableField("status")
    private String status;

    /**
     * 优先级: low-低, medium-中, high-高, urgent-紧急
     */
    @TableField("priority")
    private String priority;

    /**
     * 中标概率(0-100)
     */
    @TableField("win_probability")
    private Integer winProbability;

    /**
     * 标签
     */
    @TableField(value = "tags", typeHandler = JacksonTypeHandler.class)
    private List<String> tags;

    /**
     * 项目设置(JSON)
     */
    @TableField(value = "settings", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> settings;

    /**
     * 元数据(JSON)
     */
    @TableField(value = "metadata", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;
}
