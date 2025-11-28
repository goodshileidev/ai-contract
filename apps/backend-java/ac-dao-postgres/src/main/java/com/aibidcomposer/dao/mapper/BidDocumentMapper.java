package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.BidDocument;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 标书文档Mapper接口
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface BidDocumentMapper extends BaseMapper<BidDocument> {

    /**
     * 根据项目ID查询标书文档列表
     *
     * @param projectId 项目ID
     * @return 标书文档列表
     */
    List<BidDocument> findByProjectId(@Param("projectId") Long projectId);

    /**
     * 根据项目ID分页查询标书文档列表
     *
     * @param page 分页对象
     * @param projectId 项目ID
     * @param status 文档状态（可选）
     * @return 分页结果
     */
    IPage<BidDocument> findPageByProjectId(Page<BidDocument> page,
                                           @Param("projectId") Long projectId,
                                           @Param("status") String status);

    /**
     * 根据模板ID查询标书文档列表
     *
     * @param templateId 模板ID
     * @return 标书文档列表
     */
    List<BidDocument> findByTemplateId(@Param("templateId") Long templateId);

    /**
     * 根据文档状态查询标书文档列表
     *
     * @param projectId 项目ID
     * @param status 文档状态
     * @return 标书文档列表
     */
    List<BidDocument> findByProjectIdAndStatus(@Param("projectId") Long projectId,
                                               @Param("status") String status);

    /**
     * 根据锁定人查询被锁定的文档列表
     *
     * @param lockedBy 锁定人ID
     * @return 被锁定的文档列表
     */
    List<BidDocument> findByLockedBy(@Param("lockedBy") Long lockedBy);

    /**
     * 检查文档是否被锁定
     *
     * @param documentId 文档ID
     * @return true-被锁定, false-未锁定
     */
    boolean isLocked(@Param("documentId") Long documentId);

    /**
     * 统计项目的文档数量
     *
     * @param projectId 项目ID
     * @return 文档数量
     */
    int countByProjectId(@Param("projectId") Long projectId);

    /**
     * 统计项目指定状态的文档数量
     *
     * @param projectId 项目ID
     * @param status 文档状态
     * @return 文档数量
     */
    int countByProjectIdAndStatus(@Param("projectId") Long projectId,
                                  @Param("status") String status);
}
