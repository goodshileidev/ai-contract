package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.CollaborationEvent;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 协作事件Mapper接口(时序数据)
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface CollaborationEventMapper extends BaseMapper<CollaborationEvent> {

    /**
     * 根据会话ID查询协作事件
     *
     * @param sessionId 会话ID
     * @return 事件列表
     */
    List<CollaborationEvent> findBySessionId(@Param("sessionId") Long sessionId);

    /**
     * 根据会话ID分页查询协作事件
     *
     * @param page 分页对象
     * @param sessionId 会话ID
     * @return 分页结果
     */
    IPage<CollaborationEvent> findPageBySessionId(Page<CollaborationEvent> page,
                                                   @Param("sessionId") Long sessionId);

    /**
     * 根据文档ID查询协作事件
     *
     * @param documentId 文档ID
     * @return 事件列表
     */
    List<CollaborationEvent> findByDocumentId(@Param("documentId") Long documentId);

    /**
     * 根据文档ID分页查询协作事件
     *
     * @param page 分页对象
     * @param documentId 文档ID
     * @return 分页结果
     */
    IPage<CollaborationEvent> findPageByDocumentId(Page<CollaborationEvent> page,
                                                    @Param("documentId") Long documentId);

    /**
     * 根据用户ID查询协作事件
     *
     * @param userId 用户ID
     * @return 事件列表
     */
    List<CollaborationEvent> findByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID分页查询协作事件
     *
     * @param page 分页对象
     * @param userId 用户ID
     * @return 分页结果
     */
    IPage<CollaborationEvent> findPageByUserId(Page<CollaborationEvent> page,
                                                @Param("userId") Long userId);

    /**
     * 根据事件类型查询协作事件
     *
     * @param eventType 事件类型
     * @return 事件列表
     */
    List<CollaborationEvent> findByEventType(@Param("eventType") String eventType);

    /**
     * 根据时间范围查询协作事件
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 事件列表
     */
    List<CollaborationEvent> findByTimeRange(@Param("startTime") LocalDateTime startTime,
                                              @Param("endTime") LocalDateTime endTime);

    /**
     * 根据会话和时间范围查询协作事件
     *
     * @param sessionId 会话ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 事件列表
     */
    List<CollaborationEvent> findBySessionIdAndTimeRange(@Param("sessionId") Long sessionId,
                                                          @Param("startTime") LocalDateTime startTime,
                                                          @Param("endTime") LocalDateTime endTime);

    /**
     * 根据文档和时间范围查询协作事件
     *
     * @param documentId 文档ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 事件列表
     */
    List<CollaborationEvent> findByDocumentIdAndTimeRange(@Param("documentId") Long documentId,
                                                           @Param("startTime") LocalDateTime startTime,
                                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 统计会话的事件数
     *
     * @param sessionId 会话ID
     * @return 事件数
     */
    int countBySessionId(@Param("sessionId") Long sessionId);

    /**
     * 统计文档的事件数
     *
     * @param documentId 文档ID
     * @return 事件数
     */
    int countByDocumentId(@Param("documentId") Long documentId);

    /**
     * 统计用户的事件数
     *
     * @param userId 用户ID
     * @return 事件数
     */
    int countByUserId(@Param("userId") Long userId);

    /**
     * 根据会话ID删除事件
     *
     * @param sessionId 会话ID
     * @return 影响行数
     */
    int deleteBySessionId(@Param("sessionId") Long sessionId);

    /**
     * 根据文档ID删除事件
     *
     * @param documentId 文档ID
     * @return 影响行数
     */
    int deleteByDocumentId(@Param("documentId") Long documentId);
}
