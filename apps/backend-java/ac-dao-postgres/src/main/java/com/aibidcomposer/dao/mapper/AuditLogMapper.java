package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.AuditLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审计日志Mapper接口(时序数据)
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLog> {

    /**
     * 根据用户ID查询审计日志
     *
     * @param userId 用户ID
     * @return 日志列表
     */
    List<AuditLog> findByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID分页查询审计日志
     *
     * @param page 分页对象
     * @param userId 用户ID
     * @return 分页结果
     */
    IPage<AuditLog> findPageByUserId(Page<AuditLog> page,
                                      @Param("userId") Long userId);

    /**
     * 根据组织ID查询审计日志
     *
     * @param organizationId 组织ID
     * @return 日志列表
     */
    List<AuditLog> findByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 根据组织ID分页查询审计日志
     *
     * @param page 分页对象
     * @param organizationId 组织ID
     * @return 分页结果
     */
    IPage<AuditLog> findPageByOrganizationId(Page<AuditLog> page,
                                              @Param("organizationId") Long organizationId);

    /**
     * 根据资源类型查询审计日志
     *
     * @param resourceType 资源类型
     * @return 日志列表
     */
    List<AuditLog> findByResourceType(@Param("resourceType") String resourceType);

    /**
     * 根据资源ID查询审计日志
     *
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     * @return 日志列表
     */
    List<AuditLog> findByResourceId(@Param("resourceType") String resourceType,
                                     @Param("resourceId") Long resourceId);

    /**
     * 根据操作类型查询审计日志
     *
     * @param action 操作类型
     * @return 日志列表
     */
    List<AuditLog> findByAction(@Param("action") String action);

    /**
     * 根据时间范围查询审计日志
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表
     */
    List<AuditLog> findByTimeRange(@Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime);

    /**
     * 根据时间范围分页查询审计日志
     *
     * @param page 分页对象
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<AuditLog> findPageByTimeRange(Page<AuditLog> page,
                                         @Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime);

    /**
     * 根据用户和时间范围查询审计日志
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表
     */
    List<AuditLog> findByUserIdAndTimeRange(@Param("userId") Long userId,
                                             @Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime);

    /**
     * 根据组织和时间范围查询审计日志
     *
     * @param organizationId 组织ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表
     */
    List<AuditLog> findByOrganizationIdAndTimeRange(@Param("organizationId") Long organizationId,
                                                     @Param("startTime") LocalDateTime startTime,
                                                     @Param("endTime") LocalDateTime endTime);

    /**
     * 统计用户的操作次数
     *
     * @param userId 用户ID
     * @return 操作次数
     */
    int countByUserId(@Param("userId") Long userId);

    /**
     * 统计组织的操作次数
     *
     * @param organizationId 组织ID
     * @return 操作次数
     */
    int countByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 统计资源的操作次数
     *
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     * @return 操作次数
     */
    int countByResourceId(@Param("resourceType") String resourceType,
                          @Param("resourceId") Long resourceId);
}
