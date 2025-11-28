package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.ProjectMember;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目成员Mapper接口
 *
 * 需求编号: REQ-JAVA-DAO-002
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface ProjectMemberMapper extends BaseMapper<ProjectMember> {

    /**
     * 根据项目ID查询成员列表
     *
     * @param projectId 项目ID
     * @return 成员列表
     */
    List<ProjectMember> findByProjectId(@Param("projectId") Long projectId);

    /**
     * 根据用户ID查询项目成员列表
     *
     * @param userId 用户ID
     * @return 成员列表
     */
    List<ProjectMember> findByUserId(@Param("userId") Long userId);

    /**
     * 检查用户是否是项目成员
     *
     * @param projectId 项目ID
     * @param userId 用户ID
     * @return true-是成员, false-不是成员
     */
    boolean existsByProjectIdAndUserId(@Param("projectId") Long projectId,
                                       @Param("userId") Long userId);

    /**
     * 根据项目ID和角色查询成员列表
     *
     * @param projectId 项目ID
     * @param role 角色
     * @return 成员列表
     */
    List<ProjectMember> findByProjectIdAndRole(@Param("projectId") Long projectId,
                                               @Param("role") String role);

    /**
     * 删除项目的所有成员
     *
     * @param projectId 项目ID
     * @return 删除的记录数
     */
    int deleteByProjectId(@Param("projectId") Long projectId);
}
