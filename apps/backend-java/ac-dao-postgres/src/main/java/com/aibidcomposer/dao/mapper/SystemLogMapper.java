package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.SystemLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统日志Mapper接口(时序数据)
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface SystemLogMapper extends BaseMapper<SystemLog> {

    /**
     * 根据日志级别查询系统日志
     *
     * @param logLevel 日志级别
     * @return 日志列表
     */
    List<SystemLog> findByLogLevel(@Param("logLevel") String logLevel);

    /**
     * 根据日志级别分页查询系统日志
     *
     * @param page 分页对象
     * @param logLevel 日志级别
     * @return 分页结果
     */
    IPage<SystemLog> findPageByLogLevel(Page<SystemLog> page,
                                         @Param("logLevel") String logLevel);

    /**
     * 根据服务名称查询系统日志
     *
     * @param service 服务名称
     * @return 日志列表
     */
    List<SystemLog> findByService(@Param("service") String service);

    /**
     * 根据服务名称分页查询系统日志
     *
     * @param page 分页对象
     * @param service 服务名称
     * @return 分页结果
     */
    IPage<SystemLog> findPageByService(Page<SystemLog> page,
                                        @Param("service") String service);

    /**
     * 根据模块名称查询系统日志
     *
     * @param module 模块名称
     * @return 日志列表
     */
    List<SystemLog> findByModule(@Param("module") String module);

    /**
     * 根据模块名称分页查询系统日志
     *
     * @param page 分页对象
     * @param module 模块名称
     * @return 分页结果
     */
    IPage<SystemLog> findPageByModule(Page<SystemLog> page,
                                       @Param("module") String module);

    /**
     * 根据追踪ID查询系统日志(用于分布式追踪)
     *
     * @param traceId 追踪ID
     * @return 日志列表
     */
    List<SystemLog> findByTraceId(@Param("traceId") String traceId);

    /**
     * 根据追踪ID分页查询系统日志
     *
     * @param page 分页对象
     * @param traceId 追踪ID
     * @return 分页结果
     */
    IPage<SystemLog> findPageByTraceId(Page<SystemLog> page,
                                        @Param("traceId") String traceId);

    /**
     * 根据用户ID查询系统日志
     *
     * @param userId 用户ID
     * @return 日志列表
     */
    List<SystemLog> findByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID分页查询系统日志
     *
     * @param page 分页对象
     * @param userId 用户ID
     * @return 分页结果
     */
    IPage<SystemLog> findPageByUserId(Page<SystemLog> page,
                                       @Param("userId") Long userId);

    /**
     * 根据时间范围查询系统日志
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表
     */
    List<SystemLog> findByTimeRange(@Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime);

    /**
     * 根据时间范围分页查询系统日志
     *
     * @param page 分页对象
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<SystemLog> findPageByTimeRange(Page<SystemLog> page,
                                          @Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime);

    /**
     * 根据服务和时间范围查询系统日志
     *
     * @param service 服务名称
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表
     */
    List<SystemLog> findByServiceAndTimeRange(@Param("service") String service,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);

    /**
     * 根据日志级别和时间范围查询系统日志
     *
     * @param logLevel 日志级别
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表
     */
    List<SystemLog> findByLogLevelAndTimeRange(@Param("logLevel") String logLevel,
                                                @Param("startTime") LocalDateTime startTime,
                                                @Param("endTime") LocalDateTime endTime);

    /**
     * 统计指定日志级别的日志数
     *
     * @param logLevel 日志级别
     * @return 日志数
     */
    int countByLogLevel(@Param("logLevel") String logLevel);

    /**
     * 统计指定服务的日志数
     *
     * @param service 服务名称
     * @return 日志数
     */
    int countByService(@Param("service") String service);

    /**
     * 统计指定模块的日志数
     *
     * @param module 模块名称
     * @return 日志数
     */
    int countByModule(@Param("module") String module);

    /**
     * 统计指定追踪ID的日志数
     *
     * @param traceId 追踪ID
     * @return 日志数
     */
    int countByTraceId(@Param("traceId") String traceId);
}
