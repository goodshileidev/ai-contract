package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.TemplateUsageLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 模板使用日志Mapper接口
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface TemplateUsageLogMapper extends BaseMapper<TemplateUsageLog> {

    /**
     * 根据模板ID查询使用日志
     *
     * @param templateId 模板ID
     * @return 使用日志列表
     */
    List<TemplateUsageLog> findByTemplateId(@Param("templateId") Long templateId);

    /**
     * 根据模板ID分页查询使用日志
     *
     * @param page 分页对象
     * @param templateId 模板ID
     * @return 分页结果
     */
    IPage<TemplateUsageLog> findPageByTemplateId(Page<TemplateUsageLog> page,
                                                 @Param("templateId") Long templateId);

    /**
     * 根据用户ID查询使用日志
     *
     * @param userId 用户ID
     * @return 使用日志列表
     */
    List<TemplateUsageLog> findByUserId(@Param("userId") Long userId);

    /**
     * 根据组织ID查询使用日志
     *
     * @param organizationId 组织ID
     * @return 使用日志列表
     */
    List<TemplateUsageLog> findByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 根据项目ID查询使用日志
     *
     * @param projectId 项目ID
     * @return 使用日志列表
     */
    List<TemplateUsageLog> findByProjectId(@Param("projectId") Long projectId);

    /**
     * 根据时间范围查询使用日志
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 使用日志列表
     */
    List<TemplateUsageLog> findByTimeRange(@Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 统计模板的使用次数
     *
     * @param templateId 模板ID
     * @return 使用次数
     */
    int countByTemplateId(@Param("templateId") Long templateId);

    /**
     * 统计用户使用模板的次数
     *
     * @param userId 用户ID
     * @return 使用次数
     */
    int countByUserId(@Param("userId") Long userId);

    /**
     * 统计组织使用模板的次数
     *
     * @param organizationId 组织ID
     * @return 使用次数
     */
    int countByOrganizationId(@Param("organizationId") Long organizationId);
}
