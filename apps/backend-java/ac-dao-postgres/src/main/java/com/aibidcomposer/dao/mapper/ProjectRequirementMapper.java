package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.ProjectRequirement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目需求Mapper接口
 *
 * 需求编号: REQ-JAVA-DAO-002
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface ProjectRequirementMapper extends BaseMapper<ProjectRequirement> {

    /**
     * 根据项目ID查询需求列表
     *
     * @param projectId 项目ID
     * @return 需求列表
     */
    List<ProjectRequirement> findByProjectId(@Param("projectId") Long projectId);

    /**
     * 根据项目ID和需求类型查询需求列表
     *
     * @param projectId 项目ID
     * @param requirementType 需求类型
     * @return 需求列表
     */
    List<ProjectRequirement> findByProjectIdAndType(@Param("projectId") Long projectId,
                                                    @Param("requirementType") String requirementType);

    /**
     * 根据项目ID和匹配状态查询需求列表
     *
     * @param projectId 项目ID
     * @param matchStatus 匹配状态
     * @return 需求列表
     */
    List<ProjectRequirement> findByProjectIdAndMatchStatus(@Param("projectId") Long projectId,
                                                           @Param("matchStatus") String matchStatus);

    /**
     * 统计项目的需求数量
     *
     * @param projectId 项目ID
     * @return 需求数量
     */
    int countByProjectId(@Param("projectId") Long projectId);

    /**
     * 统计项目的强制需求数量
     *
     * @param projectId 项目ID
     * @return 强制需求数量
     */
    int countMandatoryByProjectId(@Param("projectId") Long projectId);
}
