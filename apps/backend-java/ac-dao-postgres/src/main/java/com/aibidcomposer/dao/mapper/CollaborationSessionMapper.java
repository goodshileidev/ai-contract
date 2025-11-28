package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.CollaborationSession;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 协作会话Mapper接口
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface CollaborationSessionMapper extends BaseMapper<CollaborationSession> {

    /**
     * 根据文档ID查询协作会话
     *
     * @param documentId 文档ID
     * @return 会话列表
     */
    List<CollaborationSession> findByDocumentId(@Param("documentId") Long documentId);

    /**
     * 根据会话密钥查询协作会话
     *
     * @param sessionKey 会话密钥
     * @return 会话
     */
    Optional<CollaborationSession> findBySessionKey(@Param("sessionKey") String sessionKey);

    /**
     * 根据文档ID查询活跃的协作会话
     *
     * @param documentId 文档ID
     * @return 活跃会话列表
     */
    List<CollaborationSession> findActiveByDocumentId(@Param("documentId") Long documentId);

    /**
     * 根据文档ID和活跃状态查询协作会话
     *
     * @param documentId 文档ID
     * @param isActive 是否活跃
     * @return 会话列表
     */
    List<CollaborationSession> findByDocumentIdAndActive(@Param("documentId") Long documentId,
                                                          @Param("isActive") Boolean isActive);

    /**
     * 更新最后活动时间
     *
     * @param sessionId 会话ID
     * @param lastActivityAt 最后活动时间
     * @return 影响行数
     */
    int updateLastActivityAt(@Param("sessionId") Long sessionId,
                             @Param("lastActivityAt") LocalDateTime lastActivityAt);

    /**
     * 停用会话
     *
     * @param sessionId 会话ID
     * @return 影响行数
     */
    int deactivateSession(@Param("sessionId") Long sessionId);

    /**
     * 统计文档的活跃会话数
     *
     * @param documentId 文档ID
     * @return 活跃会话数
     */
    int countActiveByDocumentId(@Param("documentId") Long documentId);

    /**
     * 根据文档ID删除会话
     *
     * @param documentId 文档ID
     * @return 影响行数
     */
    int deleteByDocumentId(@Param("documentId") Long documentId);
}
