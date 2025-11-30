# Java Spring Boot - JAVA-002 Part1 (组织管理: 数据+前端) - 📑 文档导航 - 2.1.3: Java后端 - Service层

**OrganizationService** (`com.aibidcomposer.service.OrganizationService`):

```java
package com.aibidcomposer.service;

import com.aibidcomposer.dto.organization.*;
import com.aibidcomposer.entity.Organization;
import com.aibidcomposer.entity.OrganizationMember;
import com.aibidcomposer.entity.OrganizationRole;
import com.aibidcomposer.entity.OrganizationStatus;
import com.aibidcomposer.exception.BusinessException;
import com.aibidcomposer.exception.ResourceNotFoundException;
import com.aibidcomposer.repository.OrganizationMemberRepository;
import com.aibidcomposer.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 组织管理服务
 * 需求编号: REQ-JAVA-002
 *
 * 提供组织的完整管理功能，包括：
 * - 组织CRUD操作
 * - 组织层级管理
 * - 组织成员管理
 * - 权限验证
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository memberRepository;

    // ==================== 组织CRUD操作 ====================

    /**
     * 分页查询组织列表
     *
     * @param search 搜索关键词（可选）
     * @param status 组织状态（可选）
     * @param pageable 分页参数
     * @return 组织分页列表
     */
    public Page<OrganizationResponseDTO> getOrganizations(
            String search,
            OrganizationStatus status,
            Pageable pageable
    ) {
        log.debug("查询组织列表，搜索: {}, 状态: {}", search, status);

        Page<Organization> organizations;

        if (search != null && !search.isBlank()) {
            // 按名称搜索
            organizations = organizationRepository.searchByName(search, pageable);
        } else if (status != null) {
            // 按状态过滤
            organizations = organizationRepository.findByStatus(status, pageable);
        } else {
            // 查询全部
            organizations = organizationRepository.findAll(pageable);
        }

        return organizations.map(OrganizationResponseDTO::fromEntity);
    }

    /**
     * 根据ID获取组织详情
     *
     * @param id 组织ID
     * @return 组织详情
     * @throws ResourceNotFoundException 如果组织不存在
     */
    public OrganizationResponseDTO getOrganizationById(UUID id) {
        log.debug("查询组织详情，ID: {}", id);

        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("组织不存在，ID: " + id));

        return OrganizationResponseDTO.fromEntity(organization);
    }

    /**
     * 创建组织
     *
     * @param dto 创建请求
     * @param creatorUserId 创建者用户ID
     * @return 创建的组织
     * @throws BusinessException 如果组织名称或税号已存在
     */
    @Transactional
    public OrganizationResponseDTO createOrganization(
            CreateOrganizationDTO dto,
            UUID creatorUserId
    ) {
        log.info("创建组织，名称: {}", dto.getName());

        // 验证组织名称唯一性
        if (organizationRepository.existsByName(dto.getName())) {
            throw new BusinessException("组织名称已存在: " + dto.getName());
        }

        // 验证税号唯一性
        if (dto.getTaxId() != null && organizationRepository.existsByTaxId(dto.getTaxId())) {
            throw new BusinessException("税号已存在: " + dto.getTaxId());
        }

        // 创建组织实体
        Organization organization = Organization.builder()
                .name(dto.getName())
                .shortName(dto.getShortName())
                .organizationType(dto.getOrganizationType())
                .taxId(dto.getTaxId())
                .legalPerson(dto.getLegalPerson())
                .contactPhone(dto.getContactPhone())
                .contactEmail(dto.getContactEmail())
                .address(dto.getAddress())
                .province(dto.getProvince())
                .city(dto.getCity())
                .district(dto.getDistrict())
                .logoUrl(dto.getLogoUrl())
                .website(dto.getWebsite())
                .industry(dto.getIndustry())
                .scale(dto.getScale())
                .establishedDate(dto.getEstablishedDate())
                .status(OrganizationStatus.ACTIVE)
                .build();

        // 如果指定了父组织，建立关联
        if (dto.getParentId() != null) {
            Organization parent = organizationRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "父组织不存在，ID: " + dto.getParentId()));
            organization.setParent(parent);
        }

        // 保存组织
        organization = organizationRepository.save(organization);

        // 将创建者添加为组织所有者
        OrganizationMember ownerMember = OrganizationMember.builder()
                .organization(organization)
                .userId(creatorUserId)
                .role(OrganizationRole.OWNER)
                .build();
        memberRepository.save(ownerMember);

        log.info("组织创建成功，ID: {}, 名称: {}", organization.getId(), organization.getName());

        return OrganizationResponseDTO.fromEntity(organization);
    }

    /**
     * 更新组织
     *
     * @param id 组织ID
     * @param dto 更新请求
     * @param currentUserId 当前用户ID
     * @return 更新后的组织
     * @throws ResourceNotFoundException 如果组织不存在
     * @throws BusinessException 如果没有权限或数据冲突
     */
    @Transactional
    public OrganizationResponseDTO updateOrganization(
            UUID id,
            UpdateOrganizationDTO dto,
            UUID currentUserId
    ) {
        log.info("更新组织，ID: {}", id);

        // 获取组织
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("组织不存在，ID: " + id));

        // 验证权限（必须是OWNER或ADMIN）
        verifyAdminPermission(id, currentUserId);

        // 更新名称（如果修改了名称，检查唯一性）
        if (dto.getName() != null && !dto.getName().equals(organization.getName())) {
            if (organizationRepository.existsByName(dto.getName())) {
                throw new BusinessException("组织名称已存在: " + dto.getName());
            }
            organization.setName(dto.getName());
        }

        // 更新其他字段
        if (dto.getShortName() != null) {
            organization.setShortName(dto.getShortName());
        }
        if (dto.getOrganizationType() != null) {
            organization.setOrganizationType(dto.getOrganizationType());
        }
        if (dto.getTaxId() != null && !dto.getTaxId().equals(organization.getTaxId())) {
            if (organizationRepository.existsByTaxId(dto.getTaxId())) {
                throw new BusinessException("税号已存在: " + dto.getTaxId());
            }
            organization.setTaxId(dto.getTaxId());
        }
        if (dto.getLegalPerson() != null) {
            organization.setLegalPerson(dto.getLegalPerson());
        }
        if (dto.getContactPhone() != null) {
            organization.setContactPhone(dto.getContactPhone());
        }
        if (dto.getContactEmail() != null) {
            organization.setContactEmail(dto.getContactEmail());
        }
        if (dto.getAddress() != null) {
            organization.setAddress(dto.getAddress());
        }
        if (dto.getProvince() != null) {
            organization.setProvince(dto.getProvince());
        }
        if (dto.getCity() != null) {
            organization.setCity(dto.getCity());
        }
        if (dto.getDistrict() != null) {
            organization.setDistrict(dto.getDistrict());
        }
        if (dto.getLogoUrl() != null) {
            organization.setLogoUrl(dto.getLogoUrl());
        }
        if (dto.getWebsite() != null) {
            organization.setWebsite(dto.getWebsite());
        }
        if (dto.getIndustry() != null) {
            organization.setIndustry(dto.getIndustry());
        }
        if (dto.getScale() != null) {
            organization.setScale(dto.getScale());
        }
        if (dto.getEstablishedDate() != null) {
            organization.setEstablishedDate(dto.getEstablishedDate());
        }
        if (dto.getStatus() != null) {
            organization.setStatus(dto.getStatus());
        }

        // 保存更新
        organization = organizationRepository.save(organization);

        log.info("组织更新成功，ID: {}", id);

        return OrganizationResponseDTO.fromEntity(organization);
    }

    /**
     * 删除组织（软删除）
     *
     * @param id 组织ID
     * @param currentUserId 当前用户ID
     * @throws ResourceNotFoundException 如果组织不存在
     * @throws BusinessException 如果没有权限或有子组织
     */
    @Transactional
    public void deleteOrganization(UUID id, UUID currentUserId) {
        log.info("删除组织，ID: {}", id);

        // 获取组织
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("组织不存在，ID: " + id));

        // 验证权限（必须是OWNER）
        verifyOwnerPermission(id, currentUserId);

        // 检查是否有子组织
        List<Organization> children = organizationRepository.findByParentId(id);
        if (!children.isEmpty()) {
            throw new BusinessException("不能删除有子组织的组织，请先删除或移动子组织");
        }

        // 软删除（由@SQLDelete注解处理）
        organizationRepository.delete(organization);

        log.info("组织删除成功，ID: {}", id);
    }

    // ==================== 组织成员管理 ====================

    /**
     * 获取组织成员列表
     *
     * @param organizationId 组织ID
     * @param pageable 分页参数
     * @return 成员分页列表
     */
    public Page<OrganizationMemberResponseDTO> getOrganizationMembers(
            UUID organizationId,
            Pageable pageable
    ) {
        log.debug("查询组织成员，组织ID: {}", organizationId);

        // 验证组织存在
        if (!organizationRepository.existsById(organizationId)) {
            throw new ResourceNotFoundException("组织不存在，ID: " + organizationId);
        }

        Page<OrganizationMember> members = memberRepository.findByOrganizationId(
                organizationId,
                pageable
        );

        return members.map(OrganizationMemberResponseDTO::fromEntity);
    }

    /**
     * 添加组织成员
     *
     * @param organizationId 组织ID
     * @param dto 添加成员请求
     * @param currentUserId 当前用户ID
     * @return 添加的成员信息
     * @throws BusinessException 如果用户已是成员或没有权限
     */
    @Transactional
    public OrganizationMemberResponseDTO addOrganizationMember(
            UUID organizationId,
            AddMemberDTO dto,
            UUID currentUserId
    ) {
        log.info("添加组织成员，组织ID: {}, 用户ID: {}", organizationId, dto.getUserId());

        // 验证组织存在
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("组织不存在，ID: " + organizationId));

        // 验证权限（必须是OWNER或ADMIN）
        verifyAdminPermission(organizationId, currentUserId);

        // 检查用户是否已是成员
        if (memberRepository.existsByOrganizationIdAndUserId(organizationId, dto.getUserId())) {
            throw new BusinessException("用户已是组织成员");
        }

        // 创建成员关系
        OrganizationMember member = OrganizationMember.builder()
                .organization(organization)
                .userId(dto.getUserId())
                .role(dto.getRole() != null ? dto.getRole() : OrganizationRole.MEMBER)
                .build();

        member = memberRepository.save(member);

        log.info("组织成员添加成功，组织ID: {}, 用户ID: {}", organizationId, dto.getUserId());

        return OrganizationMemberResponseDTO.fromEntity(member);
    }

    /**
     * 移除组织成员
     *
     * @param organizationId 组织ID
     * @param memberId 成员ID
     * @param currentUserId 当前用户ID
     * @throws BusinessException 如果是最后一个OWNER或没有权限
     */
    @Transactional
    public void removeOrganizationMember(
            UUID organizationId,
            UUID memberId,
            UUID currentUserId
    ) {
        log.info("移除组织成员，组织ID: {}, 成员ID: {}", organizationId, memberId);

        // 获取成员信息
        OrganizationMember member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("成员不存在，ID: " + memberId));

        // 验证成员属于该组织
        if (!member.getOrganization().getId().equals(organizationId)) {
            throw new BusinessException("成员不属于该组织");
        }

        // 验证权限
        verifyAdminPermission(organizationId, currentUserId);

        // 如果要移除的是OWNER，检查是否是最后一个OWNER
        if (member.getRole() == OrganizationRole.OWNER) {
            long ownerCount = memberRepository.countAdminsByOrganizationId(organizationId);
            if (ownerCount <= 1) {
                throw new BusinessException("不能移除组织的唯一所有者");
            }
        }

        // 删除成员
        memberRepository.delete(member);

        log.info("组织成员移除成功，组织ID: {}, 成员ID: {}", organizationId, memberId);
    }

    /**
     * 更新成员角色
     *
     * @param organizationId 组织ID
     * @param memberId 成员ID
     * @param newRole 新角色
     * @param currentUserId 当前用户ID
     * @return 更新后的成员信息
     */
    @Transactional
    public OrganizationMemberResponseDTO updateMemberRole(
            UUID organizationId,
            UUID memberId,
            OrganizationRole newRole,
            UUID currentUserId
    ) {
        log.info("更新成员角色，组织ID: {}, 成员ID: {}, 新角色: {}",
                organizationId, memberId, newRole);

        // 获取成员信息
        OrganizationMember member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("成员不存在，ID: " + memberId));

        // 验证成员属于该组织
        if (!member.getOrganization().getId().equals(organizationId)) {
            throw new BusinessException("成员不属于该组织");
        }

        // 验证权限（必须是OWNER）
        verifyOwnerPermission(organizationId, currentUserId);

        // 如果从OWNER降级，检查是否是最后一个OWNER
        if (member.getRole() == OrganizationRole.OWNER && newRole != OrganizationRole.OWNER) {
            long ownerCount = memberRepository.countAdminsByOrganizationId(organizationId);
            if (ownerCount <= 1) {
                throw new BusinessException("不能降级组织的唯一所有者");
            }
        }

        // 更新角色
        member.setRole(newRole);
        member = memberRepository.save(member);

        log.info("成员角色更新成功，成员ID: {}, 新角色: {}", memberId, newRole);

        return OrganizationMemberResponseDTO.fromEntity(member);
    }

    // ==================== 组织层级管理 ====================

    /**
     * 获取根组织列表
     */
    public Page<OrganizationSimpleDTO> getRootOrganizations(Pageable pageable) {
        log.debug("查询根组织列表");

        Page<Organization> roots = organizationRepository.findRootOrganizations(pageable);
        return roots.map(OrganizationSimpleDTO::fromEntity);
    }

    /**
     * 获取子组织列表
     */
    public List<OrganizationSimpleDTO> getChildOrganizations(UUID parentId) {
        log.debug("查询子组织，父组织ID: {}", parentId);

        List<Organization> children = organizationRepository.findByParentId(parentId);
        return children.stream()
                .map(OrganizationSimpleDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ==================== 权限验证 ====================

    /**
     * 验证用户是否为组织管理员（OWNER或ADMIN）
     */
    private void verifyAdminPermission(UUID organizationId, UUID userId) {
        OrganizationMember member = memberRepository
                .findByOrganizationIdAndUserId(organizationId, userId)
                .orElseThrow(() -> new BusinessException("您不是该组织的成员"));

        if (!member.isAdminOrOwner()) {
            throw new BusinessException("权限不足，需要管理员或所有者权限");
        }
    }

    /**
     * 验证用户是否为组织所有者
     */
    private void verifyOwnerPermission(UUID organizationId, UUID userId) {
        OrganizationMember member = memberRepository
                .findByOrganizationIdAndUserId(organizationId, userId)
                .orElseThrow(() -> new BusinessException("您不是该组织的成员"));

        if (!member.isOwner()) {
            throw new BusinessException("权限不足，需要所有者权限");
        }
    }

    /**
     * 检查用户是否为组织成员
     */
    public boolean isMember(UUID organizationId, UUID userId) {
        return memberRepository.existsByOrganizationIdAndUserId(organizationId, userId);
    }
}
```
