package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.Template;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 模板Mapper接口
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface TemplateMapper extends BaseMapper<Template> {

    /**
     * 根据模板代码查询模板
     *
     * @param code 模板代码
     * @return 模板
     */
    Optional<Template> findByCode(@Param("code") String code);

    /**
     * 检查模板代码是否存在
     *
     * @param code 模板代码
     * @return true-存在, false-不存在
     */
    boolean existsByCode(@Param("code") String code);

    /**
     * 根据分类查询模板列表
     *
     * @param category 分类
     * @return 模板列表
     */
    List<Template> findByCategory(@Param("category") String category);

    /**
     * 根据行业查询模板列表
     *
     * @param industry 行业
     * @return 模板列表
     */
    List<Template> findByIndustry(@Param("industry") String industry);

    /**
     * 根据作用域查询模板列表
     *
     * @param scope 作用域
     * @param organizationId 组织ID（当scope为organization或private时）
     * @return 模板列表
     */
    List<Template> findByScope(@Param("scope") String scope,
                               @Param("organizationId") Long organizationId);

    /**
     * 分页查询激活的公开模板
     *
     * @param page 分页对象
     * @param category 分类（可选）
     * @param industry 行业（可选）
     * @return 分页结果
     */
    IPage<Template> findActivePublicTemplates(Page<Template> page,
                                              @Param("category") String category,
                                              @Param("industry") String industry);

    /**
     * 查询热门模板（按使用次数排序）
     *
     * @param limit 数量限制
     * @return 热门模板列表
     */
    List<Template> findPopularTemplates(@Param("limit") Integer limit);

    /**
     * 查询高评分模板（按评分排序）
     *
     * @param limit 数量限制
     * @return 高评分模板列表
     */
    List<Template> findTopRatedTemplates(@Param("limit") Integer limit);

    /**
     * 增加模板使用次数
     *
     * @param templateId 模板ID
     * @return 更新的记录数
     */
    int incrementUsageCount(@Param("templateId") Long templateId);

    /**
     * 更新模板评分
     *
     * @param templateId 模板ID
     * @param rating 新评分
     * @return 更新的记录数
     */
    int updateRating(@Param("templateId") Long templateId,
                    @Param("rating") java.math.BigDecimal rating);
}
