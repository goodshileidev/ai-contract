package com.aibidcomposer.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 协作会话实体
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Data
@TableName(value = "collaboration_sessions", autoResultMap = true)
public class CollaborationSession implements Serializable {

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
     * 会话密钥
     */
    @TableField("session_key")
    private String sessionKey;

    /**
     * 活跃用户列表(JSON)
     */
    @TableField(value = "active_users", typeHandler = JacksonTypeHandler.class)
    private List<Map<String, Object>> activeUsers;

    /**
     * 光标位置(JSON)
     */
    @TableField(value = "cursor_positions", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> cursorPositions;

    /**
     * 选中内容(JSON)
     */
    @TableField(value = "selections", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> selections;

    /**
     * 用户感知状态(JSON) - Yjs Awareness
     */
    @TableField(value = "awareness_state", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> awarenessState;

    /**
     * 是否活跃
     */
    @TableField("is_active")
    private Boolean isActive;

    /**
     * 会话开始时间
     */
    @TableField("started_at")
    private LocalDateTime startedAt;

    /**
     * 会话结束时间
     */
    @TableField("ended_at")
    private LocalDateTime endedAt;

    /**
     * 最后活动时间
     */
    @TableField("last_activity_at")
    private LocalDateTime lastActivityAt;
}
