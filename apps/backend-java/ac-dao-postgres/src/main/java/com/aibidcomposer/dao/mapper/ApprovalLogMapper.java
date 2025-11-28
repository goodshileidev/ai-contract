package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.ApprovalLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审批日志Mapper接口
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface ApprovalLogMapper extends BaseMapper<ApprovalLog> {

    /**
     * 根据任务ID查询审批日志
     *
     * @param taskId 任务ID
     * @return 日志列表
     */
    List<ApprovalLog> findByTaskId(@Param("taskId") Long taskId);

    /**
     * 根据任务ID分页查询审批日志
     *
     * @param page 分页对象
     * @param taskId 任务ID
     * @return 分页结果
     */
    IPage<ApprovalLog> findPageByTaskId(Page<ApprovalLog> page,
                                         @Param("taskId") Long taskId);

    /**
     * 根据文档ID查询审批日志
     *
     * @param documentId 文档ID
     * @return 日志列表
     */
    List<ApprovalLog> findByDocumentId(@Param("documentId") Long documentId);

    /**
     * 根据文档ID分页查询审批日志
     *
     * @param page 分页对象
     * @param documentId 文档ID
     * @return 分页结果
     */
    IPage<ApprovalLog> findPageByDocumentId(Page<ApprovalLog> page,
                                             @Param("documentId") Long documentId);

    /**
     * 根据用户ID查询审批日志
     *
     * @param userId 用户ID
     * @return 日志列表
     */
    List<ApprovalLog> findByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID分页查询审批日志
     *
     * @param page 分页对象
     * @param userId 用户ID
     * @return 分页结果
     */
    IPage<ApprovalLog> findPageByUserId(Page<ApprovalLog> page,
                                         @Param("userId") Long userId);

    /**
     * 根据操作类型查询审批日志
     *
     * @param action 操作类型
     * @return 日志列表
     */
    List<ApprovalLog> findByAction(@Param("action") String action);

    /**
     * 根据时间范围查询审批日志
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表
     */
    List<ApprovalLog> findByTimeRange(@Param("startTime") LocalDateTime startTime,
                                       @Param("endTime") LocalDateTime endTime);

    /**
     * 根据任务和时间范围查询审批日志
     *
     * @param taskId 任务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表
     */
    List<ApprovalLog> findByTaskIdAndTimeRange(@Param("taskId") Long taskId,
                                                @Param("startTime") LocalDateTime startTime,
                                                @Param("endTime") LocalDateTime endTime);

    /**
     * 统计任务的日志数
     *
     * @param taskId 任务ID
     * @return 日志数
     */
    int countByTaskId(@Param("taskId") Long taskId);

    /**
     * 统计文档的日志数
     *
     * @param documentId 文档ID
     * @return 日志数
     */
    int countByDocumentId(@Param("documentId") Long documentId);

    /**
     * 统计用户的日志数
     *
     * @param userId 用户ID
     * @return 日志数
     */
    int countByUserId(@Param("userId") Long userId);

    /**
     * 根据任务ID删除日志
     *
     * @param taskId 任务ID
     * @return 影响行数
     */
    int deleteByTaskId(@Param("taskId") Long taskId);

    /**
     * 根据文档ID删除日志
     *
     * @param documentId 文档ID
     * @return 影响行数
     */
    int deleteByDocumentId(@Param("documentId") Long documentId);
}
