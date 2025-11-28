package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.Project;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 项目Mapper接口
 *
 * 需求编号: REQ-JAVA-DAO-002
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {

    /**
     * 根据项目编号查询项目
     *
     * @param code 项目编号
     * @return 项目信息
     */
    Optional<Project> findByCode(@Param("code") String code);

    /**
     * 根据组织ID查询项目列表
     *
     * @param organizationId 组织ID
     * @return 项目列表
     */
    List<Project> findByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 根据组织ID分页查询项目列表
     *
     * @param page 分页参数
     * @param organizationId 组织ID
     * @param status 项目状态（可选）
     * @return 分页结果
     */
    IPage<Project> findPageByOrganizationId(Page<Project> page,
                                            @Param("organizationId") Long organizationId,
                                            @Param("status") String status);

    /**
     * 根据用户ID查询参与的项目列表
     *
     * @param userId 用户ID
     * @return 项目列表
     */
    List<Project> findByUserId(@Param("userId") Long userId);

    /**
     * 检查项目编号是否存在
     *
     * @param code 项目编号
     * @return true-存在, false-不存在
     */
    boolean existsByCode(@Param("code") String code);
}
