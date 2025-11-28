package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.DocumentVersion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 文档版本Mapper接口
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface DocumentVersionMapper extends BaseMapper<DocumentVersion> {

    /**
     * 根据文档ID查询版本列表（按version_number降序）
     *
     * @param documentId 文档ID
     * @return 版本列表
     */
    List<DocumentVersion> findByDocumentId(@Param("documentId") Long documentId);

    /**
     * 根据文档ID分页查询版本列表
     *
     * @param page 分页对象
     * @param documentId 文档ID
     * @return 分页结果
     */
    IPage<DocumentVersion> findPageByDocumentId(Page<DocumentVersion> page,
                                                @Param("documentId") Long documentId);

    /**
     * 根据文档ID和版本号查询版本
     *
     * @param documentId 文档ID
     * @param versionNumber 版本号
     * @return 文档版本
     */
    Optional<DocumentVersion> findByDocumentIdAndVersionNumber(@Param("documentId") Long documentId,
                                                               @Param("versionNumber") Integer versionNumber);

    /**
     * 根据文档ID和版本字符串查询版本
     *
     * @param documentId 文档ID
     * @param version 版本字符串（如"1.0", "1.1"）
     * @return 文档版本
     */
    Optional<DocumentVersion> findByDocumentIdAndVersion(@Param("documentId") Long documentId,
                                                         @Param("version") String version);

    /**
     * 查询文档的最新版本
     *
     * @param documentId 文档ID
     * @return 最新版本
     */
    Optional<DocumentVersion> findLatestVersion(@Param("documentId") Long documentId);

    /**
     * 根据变更类型查询版本列表
     *
     * @param documentId 文档ID
     * @param changeType 变更类型
     * @return 版本列表
     */
    List<DocumentVersion> findByDocumentIdAndChangeType(@Param("documentId") Long documentId,
                                                        @Param("changeType") String changeType);

    /**
     * 获取文档的最大版本号
     *
     * @param documentId 文档ID
     * @return 最大版本号
     */
    Integer getMaxVersionNumber(@Param("documentId") Long documentId);

    /**
     * 统计文档的版本数量
     *
     * @param documentId 文档ID
     * @return 版本数量
     */
    int countByDocumentId(@Param("documentId") Long documentId);

    /**
     * 删除文档的所有版本（慎用）
     *
     * @param documentId 文档ID
     * @return 删除的记录数
     */
    int deleteByDocumentId(@Param("documentId") Long documentId);
}
