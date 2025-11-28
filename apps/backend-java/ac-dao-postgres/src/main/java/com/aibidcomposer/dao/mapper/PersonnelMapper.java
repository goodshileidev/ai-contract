package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.Personnel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 人员资质Mapper接口
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface PersonnelMapper extends BaseMapper<Personnel> {

    /**
     * 根据组织ID查询人员列表
     *
     * @param organizationId 组织ID
     * @return 人员列表
     */
    List<Personnel> findByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 根据组织ID分页查询人员
     *
     * @param page 分页对象
     * @param organizationId 组织ID
     * @param position 职位（可选）
     * @param department 部门（可选）
     * @return 分页结果
     */
    IPage<Personnel> findPageByOrganizationId(Page<Personnel> page,
                                               @Param("organizationId") Long organizationId,
                                               @Param("position") String position,
                                               @Param("department") String department);

    /**
     * 根据员工编号查询人员
     *
     * @param employeeId 员工编号
     * @return 人员信息
     */
    Optional<Personnel> findByEmployeeId(@Param("employeeId") String employeeId);

    /**
     * 根据职位查询人员
     *
     * @param organizationId 组织ID
     * @param position 职位
     * @return 人员列表
     */
    List<Personnel> findByPosition(@Param("organizationId") Long organizationId,
                                   @Param("position") String position);

    /**
     * 根据部门查询人员
     *
     * @param organizationId 组织ID
     * @param department 部门
     * @return 人员列表
     */
    List<Personnel> findByDepartment(@Param("organizationId") Long organizationId,
                                     @Param("department") String department);

    /**
     * 查询可用人员
     *
     * @param organizationId 组织ID
     * @return 人员列表
     */
    List<Personnel> findAvailableByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 检查员工编号是否已存在
     *
     * @param employeeId 员工编号
     * @return 是否存在
     */
    boolean existsByEmployeeId(@Param("employeeId") String employeeId);

    /**
     * 根据组织ID统计人员数量
     *
     * @param organizationId 组织ID
     * @return 数量
     */
    int countByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 根据组织ID和职位统计数量
     *
     * @param organizationId 组织ID
     * @param position 职位
     * @return 数量
     */
    int countByOrganizationIdAndPosition(@Param("organizationId") Long organizationId,
                                         @Param("position") String position);

    /**
     * 根据组织ID删除人员
     *
     * @param organizationId 组织ID
     * @return 影响行数
     */
    int deleteByOrganizationId(@Param("organizationId") Long organizationId);
}
