package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 权限Mapper接口
 *
 * 需求编号: REQ-JAVA-DAO-001
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 根据权限代码查询权限
     *
     * @param code 权限代码
     * @return 权限信息
     */
    Optional<Permission> findByCode(@Param("code") String code);

    /**
     * 根据角色ID查询权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<Permission> findByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查询权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> findByUserId(@Param("userId") Long userId);

    /**
     * 根据资源和操作查询权限
     *
     * @param resource 资源名称
     * @param action 操作
     * @return 权限信息
     */
    Optional<Permission> findByResourceAndAction(@Param("resource") String resource,
                                                  @Param("action") String action);
}
