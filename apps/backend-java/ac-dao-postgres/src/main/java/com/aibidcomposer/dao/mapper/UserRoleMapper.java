package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.UserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户角色关联Mapper接口
 *
 * 需求编号: REQ-JAVA-DAO-001
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 根据用户ID查询用户角色关联
     *
     * @param userId 用户ID
     * @return 用户角色关联列表
     */
    List<UserRole> findByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询用户角色关联
     *
     * @param roleId 角色ID
     * @return 用户角色关联列表
     */
    List<UserRole> findByRoleId(@Param("roleId") Long roleId);

    /**
     * 检查用户是否拥有指定角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return true-拥有, false-不拥有
     */
    boolean existsByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 删除用户的所有角色
     *
     * @param userId 用户ID
     * @return 删除的记录数
     */
    int deleteByUserId(@Param("userId") Long userId);
}
