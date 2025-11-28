package com.aibidcomposer.dao.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 文档版本实体
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Data
@TableName(value = "document_versions", autoResultMap = true)
public class DocumentVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文档ID
     */
    @TableField("document_id")
    private Long documentId;

    /**
     * 版本号（如：1.0, 1.1）
     */
    @TableField("version")
    private String version;

    /**
     * 版本序号（递增）
     */
    @TableField("version_number")
    private Integer versionNumber;

    /**
     * 文档标题
     */
    @TableField("title")
    private String title;

    /**
     * 文档内容(JSON)
     */
    @TableField(value = "content", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> content;

    /**
     * 纯文本内容
     */
    @TableField("plain_content")
    private String plainContent;

    /**
     * 变更摘要
     */
    @TableField("change_summary")
    private String changeSummary;

    /**
     * 变更类型: major-主版本, minor-次版本, patch-补丁, draft-草稿
     */
    @TableField("change_type")
    private String changeType;

    /**
     * 字数统计
     */
    @TableField("word_count")
    private Integer wordCount;

    /**
     * 创建人ID
     */
    @TableField("created_by")
    private Long createdBy;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
