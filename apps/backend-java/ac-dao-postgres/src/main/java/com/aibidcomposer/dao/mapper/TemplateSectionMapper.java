package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.TemplateSection;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 模板章节Mapper接口
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface TemplateSectionMapper extends BaseMapper<TemplateSection> {

    /**
     * 根据模板ID查询章节列表（按order_index排序）
     *
     * @param templateId 模板ID
     * @return 章节列表
     */
    List<TemplateSection> findByTemplateId(@Param("templateId") Long templateId);

    /**
     * 根据模板ID和父章节ID查询子章节列表
     *
     * @param templateId 模板ID
     * @param parentId 父章节ID
     * @return 子章节列表
     */
    List<TemplateSection> findByTemplateIdAndParentId(@Param("templateId") Long templateId,
                                                      @Param("parentId") Long parentId);

    /**
     * 根据模板ID查询根章节列表（parent_id为NULL的章节）
     *
     * @param templateId 模板ID
     * @return 根章节列表
     */
    List<TemplateSection> findRootSections(@Param("templateId") Long templateId);

    /**
     * 根据模板ID和层级查询章节列表
     *
     * @param templateId 模板ID
     * @param level 层级
     * @return 章节列表
     */
    List<TemplateSection> findByTemplateIdAndLevel(@Param("templateId") Long templateId,
                                                   @Param("level") Integer level);

    /**
     * 查询模板的必填章节列表
     *
     * @param templateId 模板ID
     * @return 必填章节列表
     */
    List<TemplateSection> findRequiredSections(@Param("templateId") Long templateId);

    /**
     * 获取模板章节的最大order_index
     *
     * @param templateId 模板ID
     * @param parentId 父章节ID（可为NULL）
     * @return 最大order_index
     */
    Integer getMaxOrderIndex(@Param("templateId") Long templateId,
                            @Param("parentId") Long parentId);

    /**
     * 统计模板的章节数量
     *
     * @param templateId 模板ID
     * @return 章节数量
     */
    int countByTemplateId(@Param("templateId") Long templateId);

    /**
     * 删除模板的所有章节
     *
     * @param templateId 模板ID
     * @return 删除的记录数
     */
    int deleteByTemplateId(@Param("templateId") Long templateId);
}
