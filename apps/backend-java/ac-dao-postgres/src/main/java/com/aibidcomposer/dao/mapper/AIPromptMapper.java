package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.AIPrompt;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * AI Prompt模板Mapper接口
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface AIPromptMapper extends BaseMapper<AIPrompt> {

    /**
     * 根据代码查询Prompt
     *
     * @param code 代码
     * @return Prompt
     */
    Optional<AIPrompt> findByCode(@Param("code") String code);

    /**
     * 检查代码是否已存在
     *
     * @param code 代码
     * @return 是否存在
     */
    boolean existsByCode(@Param("code") String code);

    /**
     * 根据分类查询Prompt列表
     *
     * @param category 分类
     * @return Prompt列表
     */
    List<AIPrompt> findByCategory(@Param("category") String category);

    /**
     * 分页查询Prompt
     *
     * @param page 分页对象
     * @param category 分类（可选）
     * @param isActive 是否启用（可选）
     * @return 分页结果
     */
    IPage<AIPrompt> findPage(Page<AIPrompt> page,
                              @Param("category") String category,
                              @Param("isActive") Boolean isActive);

    /**
     * 查询活跃的Prompt列表
     *
     * @return Prompt列表
     */
    List<AIPrompt> findAllActive();

    /**
     * 查询最常使用的Prompt
     *
     * @param limit 数量限制
     * @return Prompt列表
     */
    List<AIPrompt> findMostUsed(@Param("limit") Integer limit);

    /**
     * 增加使用次数
     *
     * @param promptId Prompt ID
     * @return 影响行数
     */
    int incrementUsageCount(@Param("promptId") Long promptId);

    /**
     * 更新平均Token数和成本
     *
     * @param promptId Prompt ID
     * @param averageTokens 平均Token数
     * @param averageCost 平均成本
     * @return 影响行数
     */
    int updateAverageMetrics(@Param("promptId") Long promptId,
                            @Param("averageTokens") Integer averageTokens,
                            @Param("averageCost") java.math.BigDecimal averageCost);

    /**
     * 根据分类统计Prompt数量
     *
     * @param category 分类
     * @return 数量
     */
    int countByCategory(@Param("category") String category);
}
