package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.AITask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI任务Mapper接口
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface AITaskMapper extends BaseMapper<AITask> {

    /**
     * 根据任务类型查询任务列表
     *
     * @param taskType 任务类型
     * @return 任务列表
     */
    List<AITask> findByTaskType(@Param("taskType") String taskType);

    /**
     * 根据状态查询任务列表
     *
     * @param status 状态
     * @return 任务列表
     */
    List<AITask> findByStatus(@Param("status") String status);

    /**
     * 根据项目ID查询任务列表
     *
     * @param projectId 项目ID
     * @return 任务列表
     */
    List<AITask> findByProjectId(@Param("projectId") Long projectId);

    /**
     * 根据项目ID分页查询任务
     *
     * @param page 分页对象
     * @param projectId 项目ID
     * @param taskType 任务类型（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<AITask> findPageByProjectId(Page<AITask> page,
                                       @Param("projectId") Long projectId,
                                       @Param("taskType") String taskType,
                                       @Param("status") String status);

    /**
     * 根据文档ID查询任务列表
     *
     * @param documentId 文档ID
     * @return 任务列表
     */
    List<AITask> findByDocumentId(@Param("documentId") Long documentId);

    /**
     * 根据创建人查询任务列表
     *
     * @param createdBy 创建人ID
     * @return 任务列表
     */
    List<AITask> findByCreatedBy(@Param("createdBy") Long createdBy);

    /**
     * 根据创建人分页查询任务
     *
     * @param page 分页对象
     * @param createdBy 创建人ID
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<AITask> findPageByCreatedBy(Page<AITask> page,
                                       @Param("createdBy") Long createdBy,
                                       @Param("status") String status);

    /**
     * 统计指定状态的任务数量
     *
     * @param status 状态
     * @return 数量
     */
    int countByStatus(@Param("status") String status);

    /**
     * 统计指定项目的任务数量
     *
     * @param projectId 项目ID
     * @return 数量
     */
    int countByProjectId(@Param("projectId") Long projectId);

    /**
     * 统计指定项目和状态的任务数量
     *
     * @param projectId 项目ID
     * @param status 状态
     * @return 数量
     */
    int countByProjectIdAndStatus(@Param("projectId") Long projectId,
                                   @Param("status") String status);

    /**
     * 根据项目ID删除任务
     *
     * @param projectId 项目ID
     * @return 影响行数
     */
    int deleteByProjectId(@Param("projectId") Long projectId);

    /**
     * 根据文档ID删除任务
     *
     * @param documentId 文档ID
     * @return 影响行数
     */
    int deleteByDocumentId(@Param("documentId") Long documentId);
}
