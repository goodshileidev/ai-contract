package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.ApprovalWorkflow;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 审批流程Mapper接口
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface ApprovalWorkflowMapper extends BaseMapper<ApprovalWorkflow> {

    /**
     * 根据流程代码查询审批流程
     *
     * @param code 流程代码
     * @return 审批流程
     */
    Optional<ApprovalWorkflow> findByCode(@Param("code") String code);

    /**
     * 检查流程代码是否存在
     *
     * @param code 流程代码
     * @return 是否存在
     */
    boolean existsByCode(@Param("code") String code);

    /**
     * 根据组织ID查询审批流程
     *
     * @param organizationId 组织ID
     * @return 流程列表
     */
    List<ApprovalWorkflow> findByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 根据组织ID分页查询审批流程
     *
     * @param page 分页对象
     * @param organizationId 组织ID
     * @param isActive 是否启用（可选）
     * @return 分页结果
     */
    IPage<ApprovalWorkflow> findPageByOrganizationId(Page<ApprovalWorkflow> page,
                                                      @Param("organizationId") Long organizationId,
                                                      @Param("isActive") Boolean isActive);

    /**
     * 根据组织ID查询活跃的审批流程
     *
     * @param organizationId 组织ID
     * @return 流程列表
     */
    List<ApprovalWorkflow> findActiveByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 根据文档类型查询审批流程
     *
     * @param organizationId 组织ID
     * @param documentType 文档类型
     * @return 流程列表
     */
    List<ApprovalWorkflow> findByDocumentType(@Param("organizationId") Long organizationId,
                                               @Param("documentType") String documentType);

    /**
     * 根据组织和文档类型查询默认审批流程
     *
     * @param organizationId 组织ID
     * @param documentType 文档类型
     * @return 默认流程
     */
    Optional<ApprovalWorkflow> findDefaultByOrganizationIdAndDocumentType(
            @Param("organizationId") Long organizationId,
            @Param("documentType") String documentType);

    /**
     * 统计组织的流程数量
     *
     * @param organizationId 组织ID
     * @return 流程数量
     */
    int countByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 根据组织ID删除流程
     *
     * @param organizationId 组织ID
     * @return 影响行数
     */
    int deleteByOrganizationId(@Param("organizationId") Long organizationId);
}
