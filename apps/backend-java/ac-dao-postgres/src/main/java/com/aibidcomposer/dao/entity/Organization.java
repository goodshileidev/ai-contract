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
 * 组织/企业实体
 *
 * 需求编号: REQ-JAVA-DAO-001
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "organizations", autoResultMap = true)
public class Organization extends BaseEntity {

    /**
     * 组织名称
     */
    @TableField("name")
    private String name;

    /**
     * 简称
     */
    @TableField("short_name")
    private String shortName;

    /**
     * 组织类型: company-公司, government-政府, institution-事业单位, individual-个人
     */
    @TableField("organization_type")
    private String organizationType;

    /**
     * 统一社会信用代码/税号
     */
    @TableField("tax_id")
    private String taxId;

    /**
     * 法定代表人
     */
    @TableField("legal_person")
    private String legalPerson;

    /**
     * 联系电话
     */
    @TableField("contact_phone")
    private String contactPhone;

    /**
     * 联系邮箱
     */
    @TableField("contact_email")
    private String contactEmail;

    /**
     * 详细地址
     */
    @TableField("address")
    private String address;

    /**
     * 省份
     */
    @TableField("province")
    private String province;

    /**
     * 城市
     */
    @TableField("city")
    private String city;

    /**
     * 区县
     */
    @TableField("district")
    private String district;

    /**
     * Logo URL
     */
    @TableField("logo_url")
    private String logoUrl;

    /**
     * 官网地址
     */
    @TableField("website")
    private String website;

    /**
     * 所属行业
     */
    @TableField("industry")
    private String industry;

    /**
     * 企业规模: small-小型, medium-中型, large-大型, xlarge-超大型
     */
    @TableField("scale")
    private String scale;

    /**
     * 成立日期
     */
    @TableField("established_date")
    private LocalDate establishedDate;

    /**
     * 组织状态: active-活跃, inactive-未激活, suspended-已暂停
     */
    @TableField("status")
    private String status;

    /**
     * 组织设置(JSON)
     */
    @TableField(value = "settings", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> settings;

    /**
     * 元数据(JSON)
     */
    @TableField(value = "metadata", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;
}
