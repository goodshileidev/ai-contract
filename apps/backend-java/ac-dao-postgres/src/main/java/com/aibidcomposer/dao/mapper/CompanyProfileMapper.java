package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.CompanyProfile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

/**
 * 企业档案Mapper接口
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface CompanyProfileMapper extends BaseMapper<CompanyProfile> {

    /**
     * 根据组织ID查询企业档案
     *
     * @param organizationId 组织ID
     * @return 企业档案
     */
    Optional<CompanyProfile> findByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 检查组织是否已有档案
     *
     * @param organizationId 组织ID
     * @return 是否存在
     */
    boolean existsByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 根据组织ID删除企业档案
     *
     * @param organizationId 组织ID
     * @return 影响行数
     */
    int deleteByOrganizationId(@Param("organizationId") Long organizationId);
}
