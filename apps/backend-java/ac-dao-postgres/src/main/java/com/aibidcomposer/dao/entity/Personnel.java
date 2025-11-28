package com.aibidcomposer.dao.entity;

import com.aibidcomposer.common.biz.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * 人员资质实体
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
@TableName(value = "personnel", autoResultMap = true)
public class Personnel extends BaseEntity {

    /**
     * 组织ID
     */
    @TableField("organization_id")
    private Long organizationId;

    /**
     * 姓名
     */
    @TableField("name")
    private String name;

    /**
     * 员工编号
     */
    @TableField("employee_id")
    private String employeeId;

    /**
     * 职位
     */
    @TableField("position")
    private String position;

    /**
     * 部门
     */
    @TableField("department")
    private String department;

    /**
     * 学历
     */
    @TableField("education")
    private String education;

    /**
     * 专业
     */
    @TableField("major")
    private String major;

    /**
     * 工作年限
     */
    @TableField("years_of_experience")
    private Integer yearsOfExperience;

    /**
     * 专长(JSON数组)
     */
    @TableField(value = "specialties", typeHandler = JacksonTypeHandler.class)
    private List<String> specialties;

    /**
     * 认证证书(JSON数组)
     */
    @TableField(value = "certifications", typeHandler = JacksonTypeHandler.class)
    private List<String> certifications;

    /**
     * 项目经验数量
     */
    @TableField("project_experience_count")
    private Integer projectExperienceCount;

    /**
     * 技能列表(JSON)
     */
    @TableField(value = "skills", typeHandler = JacksonTypeHandler.class)
    private List<Map<String, Object>> skills;

    /**
     * 是否可用
     */
    @TableField("is_available")
    private Boolean isAvailable;

    /**
     * 简历URL
     */
    @TableField("resume_url")
    private String resumeUrl;

    /**
     * 元数据(JSON)
     */
    @TableField(value = "metadata", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;
}
