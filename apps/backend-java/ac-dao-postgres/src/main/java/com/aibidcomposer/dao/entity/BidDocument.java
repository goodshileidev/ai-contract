package com.aibidcomposer.dao.entity;

import com.aibidcomposer.common.biz.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 标书文档实体
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
@TableName(value = "bid_documents", autoResultMap = true)
public class BidDocument extends BaseEntity {

    /**
     * 项目ID
     */
    @TableField("project_id")
    private Long projectId;

    /**
     * 模板ID
     */
    @TableField("template_id")
    private Long templateId;

    /**
     * 文档标题
     */
    @TableField("title")
    private String title;

    /**
     * 版本号
     */
    @TableField("version")
    private String version;

    /**
     * 文档类型: main-主标书, technical-技术标, commercial-商务标,
     *           qualification-资质标, other-其他
     */
    @TableField("document_type")
    private String documentType;

    /**
     * 文档状态: draft-草稿, editing-编辑中, review-评审中,
     *           approved-已批准, submitted-已提交, archived-已归档
     */
    @TableField("status")
    private String status;

    /**
     * 内容类型: structured-结构化, freeform-自由格式, mixed-混合
     */
    @TableField("content_type")
    private String contentType;

    /**
     * 结构化内容(JSON)
     */
    @TableField(value = "content", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> content;

    /**
     * 纯文本内容(用于搜索)
     */
    @TableField("plain_content")
    private String plainContent;

    /**
     * 目录结构(JSON)
     */
    @TableField(value = "toc", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> toc;

    /**
     * 字数统计
     */
    @TableField("word_count")
    private Integer wordCount;

    /**
     * 页数统计
     */
    @TableField("page_count")
    private Integer pageCount;

    /**
     * 最后编辑人ID
     */
    @TableField("last_edited_by")
    private Long lastEditedBy;

    /**
     * 最后编辑时间
     */
    @TableField("last_edited_at")
    private LocalDateTime lastEditedAt;

    /**
     * 锁定人ID（编辑锁）
     */
    @TableField("locked_by")
    private Long lockedBy;

    /**
     * 锁定时间
     */
    @TableField("locked_at")
    private LocalDateTime lockedAt;

    /**
     * 元数据(JSON)
     */
    @TableField(value = "metadata", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;
}
