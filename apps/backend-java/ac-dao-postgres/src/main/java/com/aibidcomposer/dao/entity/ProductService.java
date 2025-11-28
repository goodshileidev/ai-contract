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
 * 产品服务实体
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
@TableName(value = "products_services", autoResultMap = true)
public class ProductService extends BaseEntity {

    /**
     * 组织ID
     */
    @TableField("organization_id")
    private Long organizationId;

    /**
     * 产品/服务名称
     */
    @TableField("name")
    private String name;

    /**
     * 分类
     */
    @TableField("category")
    private String category;

    /**
     * 类型: product-产品, service-服务, solution-解决方案
     */
    @TableField("type")
    private String type;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 特性(JSON数组)
     */
    @TableField(value = "features", typeHandler = JacksonTypeHandler.class)
    private List<String> features;

    /**
     * 规格参数(JSON)
     */
    @TableField(value = "specifications", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> specifications;

    /**
     * 优势(JSON数组)
     */
    @TableField(value = "advantages", typeHandler = JacksonTypeHandler.class)
    private List<String> advantages;

    /**
     * 应用场景(JSON数组)
     */
    @TableField(value = "application_scenarios", typeHandler = JacksonTypeHandler.class)
    private List<String> applicationScenarios;

    /**
     * 技术栈(JSON数组)
     */
    @TableField(value = "technology_stack", typeHandler = JacksonTypeHandler.class)
    private List<String> technologyStack;

    /**
     * 价格区间
     */
    @TableField("price_range")
    private String priceRange;

    /**
     * 是否启用
     */
    @TableField("is_active")
    private Boolean isActive;

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
