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
 * 协作事件实体(时序数据)
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Data
@TableName(value = "collaboration_events", autoResultMap = true)
public class CollaborationEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID
     */
    @TableField("session_id")
    private Long sessionId;

    /**
     * 文档ID
     */
    @TableField("document_id")
    private Long documentId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 事件类型: join, leave, edit, comment, cursor_move, selection, other
     */
    @TableField("event_type")
    private String eventType;

    /**
     * 事件数据(JSON)
     */
    @TableField(value = "event_data", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> eventData;

    /**
     * 创建时间(自动填充)
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
