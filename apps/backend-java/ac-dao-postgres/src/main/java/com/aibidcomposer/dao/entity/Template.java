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
 * 模板实体
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
@TableName(value = "templates", autoResultMap = true)
public class Template extends BaseEntity {

    /**
     * 模板名称
     */
    @TableField("name")
    private String name;

    /**
     * 模板代码（唯一）
     */
    @TableField("code")
    private String code;

    /**
     * 模板描述
     */
    @TableField("description")
    private String description;

    /**
     * 模板分类
     */
    @TableField("category")
    private String category;

    /**
     * 行业分类
     */
    @TableField("industry")
    private String industry;

    /**
     * 模板类型: standard-标准, industry-行业, custom-自定义
     */
    @TableField("template_type")
    private String templateType;

    /**
     * 作用域: public-公开, organization-组织, private-私有
     */
    @TableField("scope")
    private String scope;

    /**
     * 组织ID（当scope为organization或private时）
     */
    @TableField("organization_id")
    private Long organizationId;

    /**
     * 版本号
     */
    @TableField("version")
    private String version;

    /**
     * 语言
     */
    @TableField("language")
    private String language;

    /**
     * 模板内容(JSON)
     */
    @TableField(value = "content", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> content;

    /**
     * 模板结构(JSON)
     */
    @TableField(value = "structure", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> structure;

    /**
     * 模板变量(JSON数组)
     */
    @TableField(value = "variables", typeHandler = JacksonTypeHandler.class)
    private List<Map<String, Object>> variables;

    /**
     * 占位符定义(JSON数组)
     */
    @TableField(value = "placeholders", typeHandler = JacksonTypeHandler.class)
    private List<Map<String, Object>> placeholders;

    /**
     * 标签(JSON数组)
     */
    @TableField(value = "tags", typeHandler = JacksonTypeHandler.class)
    private List<String> tags;

    /**
     * 是否激活
     */
    @TableField("is_active")
    private Boolean isActive;

    /**
     * 使用次数
     */
    @TableField("usage_count")
    private Integer usageCount;

    /**
     * 评分
     */
    @TableField("rating")
    private BigDecimal rating;

    /**
     * 评分次数
     */
    @TableField("rating_count")
    private Integer ratingCount;

    /**
     * 缩略图URL
     */
    @TableField("thumbnail_url")
    private String thumbnailUrl;

    /**
     * 预览URL
     */
    @TableField("preview_url")
    private String previewUrl;

    /**
     * 元数据(JSON)
     */
    @TableField(value = "metadata", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;
}
