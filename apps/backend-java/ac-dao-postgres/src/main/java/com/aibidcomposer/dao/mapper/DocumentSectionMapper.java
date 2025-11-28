package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.DocumentSection;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 文档章节Mapper接口
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface DocumentSectionMapper extends BaseMapper<DocumentSection> {

    /**
     * 根据文档ID查询章节列表（按order_index排序）
     *
     * @param documentId 文档ID
     * @return 章节列表
     */
    List<DocumentSection> findByDocumentId(@Param("documentId") Long documentId);

    /**
     * 根据文档ID和父章节ID查询子章节列表
     *
     * @param documentId 文档ID
     * @param parentId 父章节ID
     * @return 子章节列表
     */
    List<DocumentSection> findByDocumentIdAndParentId(@Param("documentId") Long documentId,
                                                      @Param("parentId") Long parentId);

    /**
     * 根据文档ID查询根章节列表（parent_id为NULL的章节）
     *
     * @param documentId 文档ID
     * @return 根章节列表
     */
    List<DocumentSection> findRootSections(@Param("documentId") Long documentId);

    /**
     * 根据文档ID和层级查询章节列表
     *
     * @param documentId 文档ID
     * @param level 层级
     * @return 章节列表
     */
    List<DocumentSection> findByDocumentIdAndLevel(@Param("documentId") Long documentId,
                                                   @Param("level") Integer level);

    /**
     * 查询文档的必填章节列表
     *
     * @param documentId 文档ID
     * @return 必填章节列表
     */
    List<DocumentSection> findRequiredSections(@Param("documentId") Long documentId);

    /**
     * 查询文档的AI生成章节列表
     *
     * @param documentId 文档ID
     * @return AI生成的章节列表
     */
    List<DocumentSection> findGeneratedSections(@Param("documentId") Long documentId);

    /**
     * 获取文档章节的最大order_index
     *
     * @param documentId 文档ID
     * @param parentId 父章节ID（可为NULL）
     * @return 最大order_index
     */
    Integer getMaxOrderIndex(@Param("documentId") Long documentId,
                            @Param("parentId") Long parentId);

    /**
     * 统计文档的章节数量
     *
     * @param documentId 文档ID
     * @return 章节数量
     */
    int countByDocumentId(@Param("documentId") Long documentId);

    /**
     * 统计文档的必填章节数量
     *
     * @param documentId 文档ID
     * @return 必填章节数量
     */
    int countRequiredSections(@Param("documentId") Long documentId);

    /**
     * 删除文档的所有章节
     *
     * @param documentId 文档ID
     * @return 删除的记录数
     */
    int deleteByDocumentId(@Param("documentId") Long documentId);
}
