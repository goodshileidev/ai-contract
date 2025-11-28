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
 * 模板章节实体
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
@TableName(value = "template_sections", autoResultMap = true)
public class TemplateSection extends BaseEntity {

    /**
     * 模板ID
     */
    @TableField("template_id")
    private Long templateId;

    /**
     * 父章节ID
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 章节标题
     */
    @TableField("title")
    private String title;

    /**
     * 章节编号
     */
    @TableField("section_number")
    private String sectionNumber;

    /**
     * 章节层级
     */
    @TableField("level")
    private Integer level;

    /**
     * 内容模板（包含变量占位符）
     */
    @TableField("content_template")
    private String contentTemplate;

    /**
     * 内容类型: text-文本, table-表格, image-图片, chart-图表, list-列表, mixed-混合
     */
    @TableField("content_type")
    private String contentType;

    /**
     * 排序序号
     */
    @TableField("order_index")
    private Integer orderIndex;

    /**
     * 是否必填
     */
    @TableField("is_required")
    private Boolean isRequired;

    /**
     * 是否可编辑
     */
    @TableField("is_editable")
    private Boolean isEditable;

    /**
     * 变量列表(JSON数组)
     */
    @TableField(value = "variables", typeHandler = JacksonTypeHandler.class)
    private List<String> variables;

    /**
     * 填写提示
     */
    @TableField("hints")
    private String hints;

    /**
     * 元数据(JSON)
     */
    @TableField(value = "metadata", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;
}
