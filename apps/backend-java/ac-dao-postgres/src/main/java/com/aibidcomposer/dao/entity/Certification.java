package com.aibidcomposer.dao.entity;

import com.aibidcomposer.common.biz.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.Map;

/**
 * 资质证书实体
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
@TableName(value = "certifications", autoResultMap = true)
public class Certification extends BaseEntity {

    /**
     * 组织ID
     */
    @TableField("organization_id")
    private Long organizationId;

    /**
     * 证书名称
     */
    @TableField("certification_name")
    private String certificationName;

    /**
     * 证书类型
     */
    @TableField("certification_type")
    private String certificationType;

    /**
     * 颁发机构
     */
    @TableField("issuing_authority")
    private String issuingAuthority;

    /**
     * 证书编号
     */
    @TableField("certificate_number")
    private String certificateNumber;

    /**
     * 颁发日期
     */
    @TableField("issue_date")
    private LocalDate issueDate;

    /**
     * 到期日期
     */
    @TableField("expiry_date")
    private LocalDate expiryDate;

    /**
     * 是否有效
     */
    @TableField("is_valid")
    private Boolean isValid;

    /**
     * 认证范围
     */
    @TableField("scope")
    private String scope;

    /**
     * 等级
     */
    @TableField("level")
    private String level;

    /**
     * 证书URL
     */
    @TableField("certificate_url")
    private String certificateUrl;

    /**
     * 元数据(JSON)
     */
    @TableField(value = "metadata", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;
}
