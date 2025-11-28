package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.ProjectCase;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 项目案例Mapper接口
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface ProjectCaseMapper extends BaseMapper<ProjectCase> {

    /**
     * 根据组织ID查询项目案例列表
     *
     * @param organizationId 组织ID
     * @return 项目案例列表
     */
    List<ProjectCase> findByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 根据组织ID分页查询项目案例
     *
     * @param page 分页对象
     * @param organizationId 组织ID
     * @param clientIndustry 客户行业（可选）
     * @param projectCategory 项目分类（可选）
     * @return 分页结果
     */
    IPage<ProjectCase> findPageByOrganizationId(Page<ProjectCase> page,
                                                 @Param("organizationId") Long organizationId,
                                                 @Param("clientIndustry") String clientIndustry,
                                                 @Param("projectCategory") String projectCategory);

    /**
     * 根据客户行业查询项目案例
     *
     * @param clientIndustry 客户行业
     * @return 项目案例列表
     */
    List<ProjectCase> findByClientIndustry(@Param("clientIndustry") String clientIndustry);

    /**
     * 根据项目分类查询项目案例
     *
     * @param projectCategory 项目分类
     * @return 项目案例列表
     */
    List<ProjectCase> findByProjectCategory(@Param("projectCategory") String projectCategory);

    /**
     * 查询可作为参考的案例
     *
     * @param organizationId 组织ID
     * @return 项目案例列表
     */
    List<ProjectCase> findReferenceByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 查询公开的案例
     *
     * @param organizationId 组织ID
     * @return 项目案例列表
     */
    List<ProjectCase> findPublicByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 根据日期范围查询项目案例
     *
     * @param organizationId 组织ID
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 项目案例列表
     */
    List<ProjectCase> findByDateRange(@Param("organizationId") Long organizationId,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    /**
     * 根据组织ID统计项目案例数量
     *
     * @param organizationId 组织ID
     * @return 数量
     */
    int countByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 根据组织ID和客户行业统计数量
     *
     * @param organizationId 组织ID
     * @param clientIndustry 客户行业
     * @return 数量
     */
    int countByOrganizationIdAndIndustry(@Param("organizationId") Long organizationId,
                                         @Param("clientIndustry") String clientIndustry);

    /**
     * 根据组织ID删除项目案例
     *
     * @param organizationId 组织ID
     * @return 影响行数
     */
    int deleteByOrganizationId(@Param("organizationId") Long organizationId);
}
