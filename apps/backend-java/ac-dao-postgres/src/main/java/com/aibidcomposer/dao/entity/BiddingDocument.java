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
 * 招标文件实体
 *
 * 需求编号: REQ-JAVA-DAO-002
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "bidding_documents", autoResultMap = true)
public class BiddingDocument extends BaseEntity {

    /**
     * 项目ID
     */
    @TableField("project_id")
    private Long projectId;

    /**
     * 文件名
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 文件路径
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 文件大小（字节）
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 文件类型
     */
    @TableField("file_type")
    private String fileType;

    /**
     * MIME类型
     */
    @TableField("mime_type")
    private String mimeType;

    /**
     * 存储键（MinIO）
     */
    @TableField("storage_key")
    private String storageKey;

    /**
     * 解析状态: pending-等待, processing-处理中, success-成功, failed-失败
     */
    @TableField("parsed_status")
    private String parsedStatus;

    /**
     * 解析后的内容(JSON)
     */
    @TableField(value = "parsed_content", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> parsedContent;

    /**
     * 解析时间
     */
    @TableField("parsed_at")
    private LocalDateTime parsedAt;

    /**
     * 解析错误信息
     */
    @TableField("parse_error")
    private String parseError;

    /**
     * 文件哈希(用于去重)
     */
    @TableField("document_hash")
    private String documentHash;

    /**
     * 上传人ID
     */
    @TableField("uploaded_by")
    private Long uploadedBy;
}
