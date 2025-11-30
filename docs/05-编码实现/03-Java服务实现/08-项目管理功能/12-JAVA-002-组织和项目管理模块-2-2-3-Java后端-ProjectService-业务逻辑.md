# Java Spring Boot - JAVA-002 Part1 (组织管理: 数据+前端) - JAVA-002: 组织和项目管理模块 - 2.2.3: Java后端 - ProjectService 业务逻辑

```java
package com.aibidcomposer.service;

import com.aibidcomposer.dto.project.*;
import com.aibidcomposer.entity.*;
import com.aibidcomposer.exception.BusinessException;
import com.aibidcomposer.exception.ResourceNotFoundException;
import com.aibidcomposer.repository.OrganizationRepository;
import com.aibidcomposer.repository.ProjectRepository;
import com.aibidcomposer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 项目管理服务
 * 需求编号: REQ-JAVA-002-2.2
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    /**
     * 创建项目
     */
    public ProjectResponseDTO createProject(CreateProjectDTO dto, UUID currentUserId) {
        log.info("创建项目, organizationId: {}, name: {}", dto.getOrganizationId(), dto.getName());

        // 验证组织是否存在
        Organization organization = organizationRepository.findById(dto.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("组织不存在"));

        // 验证当前用户是否属于该组织
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        if (!currentUser.getOrganizationId().equals(dto.getOrganizationId())) {
            throw new BusinessException("无权在该组织下创建项目");
        }

        // 生成项目编号（如果未提供）
        String code = dto.getCode();
        if (!StringUtils.hasText(code)) {
            code = generateProjectCode(organization);
        }

        // 检查项目编号是否已存在
        if (projectRepository.existsByCodeAndDeletedAtIsNull(code)) {
            throw new BusinessException("项目编号已存在: " + code);
        }

        // 创建项目实体
        Project project = Project.builder()
                .name(dto.getName())
                .code(code)
                .description(dto.getDescription())
                .organizationId(dto.getOrganizationId())
                .biddingType(dto.getBiddingType())
                .industry(dto.getIndustry())
                .budgetAmount(dto.getBudgetAmount())
                .currency(dto.getCurrency() != null ? dto.getCurrency() : "CNY")
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .submissionDeadline(dto.getSubmissionDeadline())
                .status(ProjectStatus.DRAFT)
                .priority(dto.getPriority() != null ? dto.getPriority() : ProjectPriority.MEDIUM)
                .winProbability(dto.getWinProbability())
                .tags(dto.getTags() != null ? dto.getTags() : new String[0])
                .settings(dto.getSettings() != null ? dto.getSettings() : "{}")
                .metadata(dto.getMetadata() != null ? dto.getMetadata() : "{}")
                .createdBy(currentUser)
                .updatedBy(currentUser)
                .build();

        // 自动添加创建人为项目所有者
        ProjectMember owner = ProjectMember.builder()
                .project(project)
                .user(currentUser)
                .role(MemberRole.OWNER)
                .permissions(new String[0])
                .createdBy(currentUser)
                .build();
        project.addMember(owner);

        // 保存项目
        Project savedProject = projectRepository.save(project);

        log.info("项目创建成功, id: {}, code: {}", savedProject.getId(), savedProject.getCode());

        return ProjectResponseDTO.from(savedProject);
    }

    /**
     * 更新项目
     */
    public ProjectResponseDTO updateProject(UUID projectId, UpdateProjectDTO dto, UUID currentUserId) {
        log.info("更新项目, projectId: {}", projectId);

        // 查询项目
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("项目不存在"));

        // 检查项目是否可编辑
        if (!project.isEditable()) {
            throw new BusinessException("项目状态为 " + project.getStatus() + "，不允许编辑");
        }

        // 验证权限：必须是项目成员且有编辑权限
        validateProjectPermission(project, currentUserId, "project:update");

        // 获取当前用户
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        // 更新字段
        if (StringUtils.hasText(dto.getName())) {
            project.setName(dto.getName());
        }
        if (StringUtils.hasText(dto.getDescription())) {
            project.setDescription(dto.getDescription());
        }
        if (dto.getBiddingType() != null) {
            project.setBiddingType(dto.getBiddingType());
        }
        if (StringUtils.hasText(dto.getIndustry())) {
            project.setIndustry(dto.getIndustry());
        }
        if (dto.getBudgetAmount() != null) {
            project.setBudgetAmount(dto.getBudgetAmount());
        }
        if (dto.getStartDate() != null) {
            project.setStartDate(dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            project.setEndDate(dto.getEndDate());
        }
        if (dto.getSubmissionDeadline() != null) {
            project.setSubmissionDeadline(dto.getSubmissionDeadline());
        }
        if (dto.getStatus() != null) {
            updateProjectStatus(project, dto.getStatus());
        }
        if (dto.getPriority() != null) {
            project.setPriority(dto.getPriority());
        }
        if (dto.getWinProbability() != null) {
            project.setWinProbability(dto.getWinProbability());
        }
        if (dto.getTags() != null) {
            project.setTags(dto.getTags());
        }
        if (dto.getSettings() != null) {
            project.setSettings(dto.getSettings());
        }
        if (dto.getMetadata() != null) {
            project.setMetadata(dto.getMetadata());
        }

        project.setUpdatedBy(currentUser);

        Project updatedProject = projectRepository.save(project);

        log.info("项目更新成功, projectId: {}", projectId);

        return ProjectResponseDTO.from(updatedProject);
    }

    /**
     * 查询项目详情
     */
    @Transactional(readOnly = true)
    public ProjectResponseDTO getProjectById(UUID projectId, UUID currentUserId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("项目不存在"));

        // 验证权限：必须是项目成员或组织成员
        validateProjectAccess(project, currentUserId);

        return ProjectResponseDTO.from(project);
    }

    /**
     * 查询项目列表（分页，支持筛选）
     */
    @Transactional(readOnly = true)
    public Page<ProjectSimpleDTO> getProjects(
            UUID organizationId,
            ProjectStatus status,
            ProjectPriority priority,
            String search,
            Pageable pageable,
            UUID currentUserId
    ) {
        // 验证用户是否属于该组织
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        if (!currentUser.getOrganizationId().equals(organizationId)) {
            throw new BusinessException("无权查看该组织的项目");
        }

        // 构建查询条件
        Specification<Project> spec = Specification.where(null);

        // 组织ID
        spec = spec.and((root, query, cb) ->
                cb.equal(root.get("organizationId"), organizationId));

        // 软删除过滤
        spec = spec.and((root, query, cb) ->
                cb.isNull(root.get("deletedAt")));

        // 状态筛选
        if (status != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("status"), status));
        }

        // 优先级筛选
        if (priority != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("priority"), priority));
        }

        // 关键词搜索（项目名称、编号）
        if (StringUtils.hasText(search)) {
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(root.get("name"), "%" + search + "%"),
                            cb.like(root.get("code"), "%" + search + "%")
                    ));
        }

        Page<Project> projectPage = projectRepository.findAll(spec, pageable);

        return projectPage.map(ProjectSimpleDTO::from);
    }

    /**
     * 删除项目（软删除）
     */
    public void deleteProject(UUID projectId, UUID currentUserId) {
        log.info("删除项目, projectId: {}", projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("项目不存在"));

        // 验证权限：必须是项目所有者
        validateProjectPermission(project, currentUserId, "project:delete");

        // 检查项目状态（已归档的项目不能删除）
        if (project.getStatus() == ProjectStatus.ARCHIVED) {
            throw new BusinessException("已归档的项目不能删除");
        }

        // 软删除
        projectRepository.delete(project);

        log.info("项目删除成功, projectId: {}", projectId);
    }

    /**
     * 更新项目状态
     */
    public void updateProjectStatus(UUID projectId, ProjectStatus targetStatus, UUID currentUserId) {
        log.info("更新项目状态, projectId: {}, targetStatus: {}", projectId, targetStatus);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("项目不存在"));

        validateProjectPermission(project, currentUserId, "project:update");

        updateProjectStatus(project, targetStatus);

        projectRepository.save(project);

        log.info("项目状态更新成功, projectId: {}, status: {} -> {}",
                projectId, project.getStatus(), targetStatus);
    }

    /**


     * 添加项目成员
     */
    public ProjectMemberResponseDTO addProjectMember(
            UUID projectId,
            AddProjectMemberDTO dto,
            UUID currentUserId
    ) {
        log.info("添加项目成员, projectId: {}, userId: {}", projectId, dto.getUserId());

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("项目不存在"));

        // 验证权限：必须有成员管理权限
        validateProjectPermission(project, currentUserId, "member:create");

        // 验证要添加的用户是否存在
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        // 验证用户是否属于同一组织
        if (!user.getOrganizationId().equals(project.getOrganizationId())) {
            throw new BusinessException("用户不属于该组织");
        }

        // 检查用户是否已是项目成员
        boolean isMember = project.getMembers().stream()
                .anyMatch(m -> m.getUser().getId().equals(dto.getUserId()));

        if (isMember) {
            throw new BusinessException("用户已是项目成员");
        }

        // 创建项目成员
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("当前用户不存在"));

        ProjectMember member = ProjectMember.builder()
                .project(project)
                .user(user)
                .role(dto.getRole() != null ? dto.getRole() : MemberRole.MEMBER)
                .permissions(dto.getPermissions() != null ? dto.getPermissions() : new String[0])
                .createdBy(currentUser)
                .build();

        project.addMember(member);

        projectRepository.save(project);

        log.info("项目成员添加成功, projectId: {}, userId: {}", projectId, dto.getUserId());

        return ProjectMemberResponseDTO.from(member);
    }

    /**
     * 移除项目成员
     */
    public void removeProjectMember(UUID projectId, UUID memberId, UUID currentUserId) {
        log.info("移除项目成员, projectId: {}, memberId: {}", projectId, memberId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("项目不存在"));

        validateProjectPermission(project, currentUserId, "member:delete");

        // 查找要移除的成员
        ProjectMember memberToRemove = project.getMembers().stream()
                .filter(m -> m.getUser().getId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("项目成员不存在"));

        // 不能移除项目所有者
        if (memberToRemove.getRole() == MemberRole.OWNER) {
            throw new BusinessException("不能移除项目所有者");
        }

        project.removeMember(memberToRemove);

        projectRepository.save(project);

        log.info("项目成员移除成功, projectId: {}, memberId: {}", projectId, memberId);
    }

    // ==================== 私有方法 ====================

    /**
     * 生成项目编号
     */
    private String generateProjectCode(Organization organization) {
        String prefix = organization.getShortName() != null ?
                organization.getShortName().toUpperCase() : "PRJ";

        String timestamp = String.valueOf(System.currentTimeMillis());
        String suffix = timestamp.substring(timestamp.length() - 6);

        return String.format("%s-%s", prefix, suffix);
    }

    /**
     * 更新项目状态（内部方法，包含状态流转验证）
     */
    private void updateProjectStatus(Project project, ProjectStatus targetStatus) {
        if (!project.canTransitionTo(targetStatus)) {
            throw new BusinessException(
                    String.format("项目状态不能从 %s 流转到 %s",
                            project.getStatus(), targetStatus)
            );
        }

        project.setStatus(targetStatus);
    }

    /**
     * 验证项目访问权限（是否可以查看项目）
     */
    private void validateProjectAccess(Project project, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        // 检查是否属于同一组织
        if (!user.getOrganizationId().equals(project.getOrganizationId())) {
            throw new BusinessException("无权访问该项目");
        }
    }

    /**
     * 验证项目操作权限（是否可以执行特定操作）
     */
    private void validateProjectPermission(Project project, UUID userId, String permission) {
        // 先检查是否可以访问项目
        validateProjectAccess(project, userId);

        // 检查是否是项目成员
        ProjectMember member = project.getMembers().stream()
                .filter(m -> m.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("不是项目成员，无权执行该操作"));

        // 检查权限
        if (!member.hasPermission(permission)) {
            throw new BusinessException("权限不足，无法执行该操作");
        }
    }
}
```
