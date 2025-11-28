package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.ProductService;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 产品服务Mapper接口
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface ProductServiceMapper extends BaseMapper<ProductService> {

    /**
     * 根据组织ID查询产品服务列表
     *
     * @param organizationId 组织ID
     * @return 产品服务列表
     */
    List<ProductService> findByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 根据组织ID分页查询产品服务
     *
     * @param page 分页对象
     * @param organizationId 组织ID
     * @param category 分类（可选）
     * @param type 类型（可选）
     * @return 分页结果
     */
    IPage<ProductService> findPageByOrganizationId(Page<ProductService> page,
                                                    @Param("organizationId") Long organizationId,
                                                    @Param("category") String category,
                                                    @Param("type") String type);

    /**
     * 根据分类查询产品服务
     *
     * @param category 分类
     * @return 产品服务列表
     */
    List<ProductService> findByCategory(@Param("category") String category);

    /**
     * 根据类型查询产品服务
     *
     * @param type 类型
     * @return 产品服务列表
     */
    List<ProductService> findByType(@Param("type") String type);

    /**
     * 查询活跃的产品服务
     *
     * @param organizationId 组织ID
     * @return 产品服务列表
     */
    List<ProductService> findActiveByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 根据组织ID统计产品服务数量
     *
     * @param organizationId 组织ID
     * @return 数量
     */
    int countByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 根据组织ID和类型统计数量
     *
     * @param organizationId 组织ID
     * @param type 类型
     * @return 数量
     */
    int countByOrganizationIdAndType(@Param("organizationId") Long organizationId,
                                     @Param("type") String type);

    /**
     * 根据组织ID删除产品服务
     *
     * @param organizationId 组织ID
     * @return 影响行数
     */
    int deleteByOrganizationId(@Param("organizationId") Long organizationId);
}
