package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.Certification;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 资质证书Mapper接口
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface CertificationMapper extends BaseMapper<Certification> {

    /**
     * 根据组织ID查询资质证书列表
     *
     * @param organizationId 组织ID
     * @return 资质证书列表
     */
    List<Certification> findByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 根据组织ID分页查询资质证书
     *
     * @param page 分页对象
     * @param organizationId 组织ID
     * @param certificationType 证书类型（可选）
     * @return 分页结果
     */
    IPage<Certification> findPageByOrganizationId(Page<Certification> page,
                                                   @Param("organizationId") Long organizationId,
                                                   @Param("certificationType") String certificationType);

    /**
     * 根据证书编号查询资质证书
     *
     * @param certificateNumber 证书编号
     * @return 资质证书
     */
    Optional<Certification> findByCertificateNumber(@Param("certificateNumber") String certificateNumber);

    /**
     * 根据证书类型查询资质证书
     *
     * @param organizationId 组织ID
     * @param certificationType 证书类型
     * @return 资质证书列表
     */
    List<Certification> findByCertificationType(@Param("organizationId") Long organizationId,
                                                @Param("certificationType") String certificationType);

    /**
     * 查询有效的资质证书
     *
     * @param organizationId 组织ID
     * @return 资质证书列表
     */
    List<Certification> findValidByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 查询即将过期的资质证书
     *
     * @param organizationId 组织ID
     * @param beforeDate 过期日期前（如30天内过期）
     * @return 资质证书列表
     */
    List<Certification> findExpiringBefore(@Param("organizationId") Long organizationId,
                                           @Param("beforeDate") LocalDate beforeDate);

    /**
     * 检查证书编号是否已存在
     *
     * @param certificateNumber 证书编号
     * @return 是否存在
     */
    boolean existsByCertificateNumber(@Param("certificateNumber") String certificateNumber);

    /**
     * 根据组织ID统计资质证书数量
     *
     * @param organizationId 组织ID
     * @return 数量
     */
    int countByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 根据组织ID和证书类型统计数量
     *
     * @param organizationId 组织ID
     * @param certificationType 证书类型
     * @return 数量
     */
    int countByOrganizationIdAndType(@Param("organizationId") Long organizationId,
                                     @Param("certificationType") String certificationType);

    /**
     * 统计有效证书数量
     *
     * @param organizationId 组织ID
     * @return 数量
     */
    int countValidByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 根据组织ID删除资质证书
     *
     * @param organizationId 组织ID
     * @return 影响行数
     */
    int deleteByOrganizationId(@Param("organizationId") Long organizationId);
}
