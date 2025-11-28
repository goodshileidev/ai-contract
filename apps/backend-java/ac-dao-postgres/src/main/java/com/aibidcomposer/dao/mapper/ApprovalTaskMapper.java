package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.ApprovalTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审批任务Mapper接口
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface ApprovalTaskMapper extends BaseMapper<ApprovalTask> {

    /**
     * 根据流程ID查询审批任务
     *
     * @param workflowId 流程ID
     * @return 任务列表
     */
    List<ApprovalTask> findByWorkflowId(@Param("workflowId") Long workflowId);

    /**
     * 根据文档ID查询审批任务
     *
     * @param documentId 文档ID
     * @return 任务列表
     */
    List<ApprovalTask> findByDocumentId(@Param("documentId") Long documentId);

    /**
     * 根据文档ID分页查询审批任务
     *
     * @param page 分页对象
     * @param documentId 文档ID
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<ApprovalTask> findPageByDocumentId(Page<ApprovalTask> page,
                                              @Param("documentId") Long documentId,
                                              @Param("status") String status);

    /**
     * 根据审批人ID查询审批任务
     *
     * @param assigneeId 审批人ID
     * @return 任务列表
     */
    List<ApprovalTask> findByAssigneeId(@Param("assigneeId") Long assigneeId);

    /**
     * 根据审批人ID分页查询审批任务
     *
     * @param page 分页对象
     * @param assigneeId 审批人ID
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<ApprovalTask> findPageByAssigneeId(Page<ApprovalTask> page,
                                              @Param("assigneeId") Long assigneeId,
                                              @Param("status") String status);

    /**
     * 根据状态查询审批任务
     *
     * @param status 状态
     * @return 任务列表
     */
    List<ApprovalTask> findByStatus(@Param("status") String status);

    /**
     * 根据审批人ID查询待处理任务
     *
     * @param assigneeId 审批人ID
     * @return 待处理任务列表
     */
    List<ApprovalTask> findPendingByAssigneeId(@Param("assigneeId") Long assigneeId);

    /**
     * 根据审批人ID查询逾期任务
     *
     * @param assigneeId 审批人ID
     * @param now 当前时间
     * @return 逾期任务列表
     */
    List<ApprovalTask> findOverdueByAssigneeId(@Param("assigneeId") Long assigneeId,
                                                @Param("now") LocalDateTime now);

    /**
     * 统计流程的任务数
     *
     * @param workflowId 流程ID
     * @return 任务数
     */
    int countByWorkflowId(@Param("workflowId") Long workflowId);

    /**
     * 统计文档的任务数
     *
     * @param documentId 文档ID
     * @return 任务数
     */
    int countByDocumentId(@Param("documentId") Long documentId);

    /**
     * 统计审批人的任务数
     *
     * @param assigneeId 审批人ID
     * @return 任务数
     */
    int countByAssigneeId(@Param("assigneeId") Long assigneeId);

    /**
     * 统计审批人的待处理任务数
     *
     * @param assigneeId 审批人ID
     * @return 待处理任务数
     */
    int countPendingByAssigneeId(@Param("assigneeId") Long assigneeId);

    /**
     * 根据流程ID删除任务
     *
     * @param workflowId 流程ID
     * @return 影响行数
     */
    int deleteByWorkflowId(@Param("workflowId") Long workflowId);

    /**
     * 根据文档ID删除任务
     *
     * @param documentId 文档ID
     * @return 影响行数
     */
    int deleteByDocumentId(@Param("documentId") Long documentId);
}
