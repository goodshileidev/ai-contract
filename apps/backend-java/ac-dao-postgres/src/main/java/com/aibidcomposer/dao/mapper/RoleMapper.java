package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 角色Mapper接口
 *
 * 需求编号: REQ-JAVA-DAO-001
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据角色代码查询角色
     *
     * @param code 角色代码
     * @return 角色信息
     */
    Optional<Role> findByCode(@Param("code") String code);

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> findByUserId(@Param("userId") Long userId);

    /**
     * 根据组织ID查询角色列表
     *
     * @param organizationId 组织ID
     * @return 角色列表
     */
    List<Role> findByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 检查角色代码是否存在
     *
     * @param code 角色代码
     * @return true-存在, false-不存在
     */
    boolean existsByCode(@Param("code") String code);
}
