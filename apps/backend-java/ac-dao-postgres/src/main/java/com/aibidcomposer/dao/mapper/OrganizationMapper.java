package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.Organization;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

/**
 * 组织Mapper接口
 *
 * 需求编号: REQ-JAVA-DAO-001
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface OrganizationMapper extends BaseMapper<Organization> {

    /**
     * 根据税号查询组织
     *
     * @param taxId 税号
     * @return 组织信息
     */
    Optional<Organization> findByTaxId(@Param("taxId") String taxId);

    /**
     * 根据组织名称查询组织
     *
     * @param name 组织名称
     * @return 组织信息
     */
    Optional<Organization> findByName(@Param("name") String name);

    /**
     * 检查税号是否存在
     *
     * @param taxId 税号
     * @return true-存在, false-不存在
     */
    boolean existsByTaxId(@Param("taxId") String taxId);
}
