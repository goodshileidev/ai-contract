package com.aibidcomposer.dao.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 企业档案实体
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Data
@TableName(value = "company_profiles", autoResultMap = true)
public class CompanyProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 组织ID（唯一）
     */
    @TableField("organization_id")
    private Long organizationId;

    /**
     * 企业简介
     */
    @TableField("brief_introduction")
    private String briefIntroduction;

    /**
     * 核心业务
     */
    @TableField("core_business")
    private String coreBusiness;

    /**
     * 核心竞争力(JSON数组)
     */
    @TableField(value = "core_competencies", typeHandler = JacksonTypeHandler.class)
    private List<String> coreCompetencies;

    /**
     * 竞争优势(JSON数组)
     */
    @TableField(value = "competitive_advantages", typeHandler = JacksonTypeHandler.class)
    private List<String> competitiveAdvantages;

    /**
     * 关键技术(JSON数组)
     */
    @TableField(value = "key_technologies", typeHandler = JacksonTypeHandler.class)
    private List<String> keyTechnologies;

    /**
     * 主要客户(JSON数组)
     */
    @TableField(value = "major_clients", typeHandler = JacksonTypeHandler.class)
    private List<String> majorClients;

    /**
     * 年营收
     */
    @TableField("annual_revenue")
    private BigDecimal annualRevenue;

    /**
     * 员工数量
     */
    @TableField("employee_count")
    private Integer employeeCount;

    /**
     * 研发人员数量
     */
    @TableField("rd_personnel_count")
    private Integer rdPersonnelCount;

    /**
     * 专利数量
     */
    @TableField("patents_count")
    private Integer patentsCount;

    /**
     * 资质认证(JSON数组)
     */
    @TableField(value = "certifications", typeHandler = JacksonTypeHandler.class)
    private List<String> certifications;

    /**
     * 荣誉奖项(JSON数组)
     */
    @TableField(value = "honors", typeHandler = JacksonTypeHandler.class)
    private List<String> honors;

    /**
     * 元数据(JSON)
     */
    @TableField(value = "metadata", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
