package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.AIUsageLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AI使用日志Mapper接口(时序数据)
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface AIUsageLogMapper extends BaseMapper<AIUsageLog> {

    /**
     * 根据用户ID查询使用日志
     *
     * @param userId 用户ID
     * @return 日志列表
     */
    List<AIUsageLog> findByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID分页查询使用日志
     *
     * @param page 分页对象
     * @param userId 用户ID
     * @return 分页结果
     */
    IPage<AIUsageLog> findPageByUserId(Page<AIUsageLog> page,
                                        @Param("userId") Long userId);

    /**
     * 根据组织ID查询使用日志
     *
     * @param organizationId 组织ID
     * @return 日志列表
     */
    List<AIUsageLog> findByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 根据组织ID分页查询使用日志
     *
     * @param page 分页对象
     * @param organizationId 组织ID
     * @return 分页结果
     */
    IPage<AIUsageLog> findPageByOrganizationId(Page<AIUsageLog> page,
                                                @Param("organizationId") Long organizationId);

    /**
     * 根据任务ID查询使用日志
     *
     * @param taskId 任务ID
     * @return 日志列表
     */
    List<AIUsageLog> findByTaskId(@Param("taskId") Long taskId);

    /**
     * 根据时间范围查询使用日志
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表
     */
    List<AIUsageLog> findByTimeRange(@Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime);

    /**
     * 根据用户和时间范围查询使用日志
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表
     */
    List<AIUsageLog> findByUserIdAndTimeRange(@Param("userId") Long userId,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);

    /**
     * 根据组织和时间范围查询使用日志
     *
     * @param organizationId 组织ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表
     */
    List<AIUsageLog> findByOrganizationIdAndTimeRange(@Param("organizationId") Long organizationId,
                                                       @Param("startTime") LocalDateTime startTime,
                                                       @Param("endTime") LocalDateTime endTime);

    /**
     * 统计用户使用次数
     *
     * @param userId 用户ID
     * @return 次数
     */
    int countByUserId(@Param("userId") Long userId);

    /**
     * 统计组织使用次数
     *
     * @param organizationId 组织ID
     * @return 次数
     */
    int countByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 统计用户总Token数
     *
     * @param userId 用户ID
     * @return Token总数
     */
    Long sumTokensByUserId(@Param("userId") Long userId);

    /**
     * 统计组织总Token数
     *
     * @param organizationId 组织ID
     * @return Token总数
     */
    Long sumTokensByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 统计用户总成本
     *
     * @param userId 用户ID
     * @return 总成本
     */
    BigDecimal sumCostByUserId(@Param("userId") Long userId);

    /**
     * 统计组织总成本
     *
     * @param organizationId 组织ID
     * @return 总成本
     */
    BigDecimal sumCostByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 统计用户在时间范围内的总成本
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 总成本
     */
    BigDecimal sumCostByUserIdAndTimeRange(@Param("userId") Long userId,
                                           @Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 统计组织在时间范围内的总成本
     *
     * @param organizationId 组织ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 总成本
     */
    BigDecimal sumCostByOrganizationIdAndTimeRange(@Param("organizationId") Long organizationId,
                                                   @Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime);
}
