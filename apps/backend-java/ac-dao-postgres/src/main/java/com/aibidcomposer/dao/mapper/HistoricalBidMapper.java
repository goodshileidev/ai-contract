package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.HistoricalBid;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 历史标书Mapper接口
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface HistoricalBidMapper extends BaseMapper<HistoricalBid> {

    /**
     * 根据组织ID查询历史标书列表
     *
     * @param organizationId 组织ID
     * @return 历史标书列表
     */
    List<HistoricalBid> findByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 根据组织ID分页查询历史标书
     *
     * @param page 分页对象
     * @param organizationId 组织ID
     * @param result 结果（可选）
     * @param industry 行业（可选）
     * @return 分页结果
     */
    IPage<HistoricalBid> findPageByOrganizationId(Page<HistoricalBid> page,
                                                   @Param("organizationId") Long organizationId,
                                                   @Param("result") String result,
                                                   @Param("industry") String industry);

    /**
     * 根据结果查询历史标书
     *
     * @param organizationId 组织ID
     * @param result 结果（won, lost, pending, withdrawn）
     * @return 历史标书列表
     */
    List<HistoricalBid> findByResult(@Param("organizationId") Long organizationId,
                                     @Param("result") String result);

    /**
     * 根据行业查询历史标书
     *
     * @param organizationId 组织ID
     * @param industry 行业
     * @return 历史标书列表
     */
    List<HistoricalBid> findByIndustry(@Param("organizationId") Long organizationId,
                                       @Param("industry") String industry);

    /**
     * 根据分类查询历史标书
     *
     * @param organizationId 组织ID
     * @param category 分类
     * @return 历史标书列表
     */
    List<HistoricalBid> findByCategory(@Param("organizationId") Long organizationId,
                                       @Param("category") String category);

    /**
     * 查询可复用的历史标书
     *
     * @param organizationId 组织ID
     * @return 历史标书列表
     */
    List<HistoricalBid> findReusableByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 根据日期范围查询历史标书
     *
     * @param organizationId 组织ID
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 历史标书列表
     */
    List<HistoricalBid> findByDateRange(@Param("organizationId") Long organizationId,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

    /**
     * 根据组织ID统计历史标书数量
     *
     * @param organizationId 组织ID
     * @return 数量
     */
    int countByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 根据组织ID和结果统计数量
     *
     * @param organizationId 组织ID
     * @param result 结果
     * @return 数量
     */
    int countByOrganizationIdAndResult(@Param("organizationId") Long organizationId,
                                       @Param("result") String result);

    /**
     * 计算组织的平均中标率
     *
     * @param organizationId 组织ID
     * @return 平均中标率
     */
    BigDecimal calculateAverageWinRate(@Param("organizationId") Long organizationId);

    /**
     * 根据组织ID删除历史标书
     *
     * @param organizationId 组织ID
     * @return 影响行数
     */
    int deleteByOrganizationId(@Param("organizationId") Long organizationId);
}
