package com.aibidcomposer.dao.entity;

import com.aibidcomposer.common.biz.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 项目案例实体
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
@TableName(value = "project_cases", autoResultMap = true)
public class ProjectCase extends BaseEntity {

    /**
     * 组织ID
     */
    @TableField("organization_id")
    private Long organizationId;

    /**
     * 项目名称
     */
    @TableField("project_name")
    private String projectName;

    /**
     * 客户名称
     */
    @TableField("client_name")
    private String clientName;

    /**
     * 客户行业
     */
    @TableField("client_industry")
    private String clientIndustry;

    /**
     * 项目分类
     */
    @TableField("project_category")
    private String projectCategory;

    /**
     * 项目类型
     */
    @TableField("project_type")
    private String projectType;

    /**
     * 合同金额
     */
    @TableField("contract_amount")
    private BigDecimal contractAmount;

    /**
     * 开始日期
     */
    @TableField("start_date")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @TableField("end_date")
    private LocalDate endDate;

    /**
     * 项目周期（月）
     */
    @TableField("duration_months")
    private Integer durationMonths;

    /**
     * 项目描述
     */
    @TableField("project_description")
    private String projectDescription;

    /**
     * 项目挑战
     */
    @TableField("challenges")
    private String challenges;

    /**
     * 解决方案
     */
    @TableField("solutions")
    private String solutions;

    /**
     * 项目成果(JSON数组)
     */
    @TableField(value = "achievements", typeHandler = JacksonTypeHandler.class)
    private List<String> achievements;

    /**
     * 使用技术(JSON数组)
     */
    @TableField(value = "technologies_used", typeHandler = JacksonTypeHandler.class)
    private List<String> technologiesUsed;

    /**
     * 团队规模
     */
    @TableField("team_size")
    private Integer teamSize;

    /**
     * 项目角色
     */
    @TableField("project_role")
    private String projectRole;

    /**
     * 客户满意度(0-5分)
     */
    @TableField("customer_satisfaction")
    private BigDecimal customerSatisfaction;

    /**
     * 是否可作为参考案例
     */
    @TableField("is_reference")
    private Boolean isReference;

    /**
     * 是否公开
     */
    @TableField("is_public")
    private Boolean isPublic;

    /**
     * 图片URL列表(JSON数组)
     */
    @TableField(value = "images", typeHandler = JacksonTypeHandler.class)
    private List<String> images;

    /**
     * 文档URL列表(JSON数组)
     */
    @TableField(value = "documents", typeHandler = JacksonTypeHandler.class)
    private List<String> documents;

    /**
     * 标签(JSON数组)
     */
    @TableField(value = "tags", typeHandler = JacksonTypeHandler.class)
    private List<String> tags;

    /**
     * 元数据(JSON)
     */
    @TableField(value = "metadata", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;
}
