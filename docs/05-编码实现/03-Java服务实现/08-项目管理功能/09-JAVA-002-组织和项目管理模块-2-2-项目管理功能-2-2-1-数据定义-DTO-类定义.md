# Java Spring Boot - JAVA-002 Part1 (组织管理: 数据+前端) - JAVA-002: 组织和项目管理模块 - 2.2: 项目管理功能 - 2.2.1: 数据定义 - DTO 类定义

```java
package com.aibidcomposer.dto.project;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 创建项目 DTO
 * 需求编号: REQ-JAVA-002
 */
@Data
public class CreateProjectDTO {

    @NotBlank(message = "项目名称不能为空")
    @Size(max = 200, message = "项目名称最多200字符")
    private String name;

    @Size(max = 2000, message = "项目描述最多2000字符")
    private String description;

    @NotNull(message = "组织ID不能为空")
    private UUID organizationId;

    private String biddingType;  // GOVERNMENT, ENTERPRISE, INTERNATIONAL, OTHER

    @Size(max = 100, message = "行业最多100字符")
    private String industry;

    @DecimalMin(value = "0.0", message = "预算金额不能为负数")
    private BigDecimal budgetAmount;

    @Size(max = 10, message = "货币单位最多10字符")
    private String currency;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDateTime submissionDeadline;

    private String priority;  // LOW, MEDIUM, HIGH, URGENT

    @Min(value = 0, message = "中标概率最小为0")
    @Max(value = 100, message = "中标概率最大为100")
    private Integer winProbability;

    private String[] tags;
}

/**
 * 更新项目 DTO
 * 需求编号: REQ-JAVA-002
 */
@Data
public class UpdateProjectDTO {

    @Size(max = 200, message = "项目名称最多200字符")
    private String name;

    @Size(max = 2000, message = "项目描述最多2000字符")
    private String description;

    private String biddingType;

    @Size(max = 100, message = "行业最多100字符")
    private String industry;

    @DecimalMin(value = "0.0", message = "预算金额不能为负数")
    private BigDecimal budgetAmount;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDateTime submissionDeadline;

    private String status;  // DRAFT, IN_PROGRESS, REVIEW, SUBMITTED, WON, LOST, ARCHIVED

    private String priority;

    @Min(value = 0, message = "中标概率最小为0")
    @Max(value = 100, message = "中标概率最大为100")
    private Integer winProbability;

    private String[] tags;
}

/**
 * 项目响应 DTO
 * 需求编号: REQ-JAVA-002
 */
@Data
public class ProjectResponseDTO {

    private UUID id;
    private String name;
    private String code;
    private String description;

    // 组织信息
    private UUID organizationId;
    private String organizationName;

    private String biddingType;
    private String industry;
    private BigDecimal budgetAmount;
    private String currency;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime submissionDeadline;

    private String status;
    private String priority;
    private Integer winProbability;

    private String[] tags;

    // 统计信息
    private Integer memberCount;
    private Integer milestoneCount;
    private Integer documentCount;
    private Integer progress;  // 0-100

    // 创建和更新信息
    private UUID createdBy;
    private String createdByName;
    private LocalDateTime createdAt;

    private UUID updatedBy;
    private String updatedByName;
    private LocalDateTime updatedAt;

    /**
     * 从实体转换为DTO
     */
    public static ProjectResponseDTO from(Project project) {
        ProjectResponseDTO dto = new ProjectResponseDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setCode(project.getCode());
        dto.setDescription(project.getDescription());

        if (project.getOrganization() != null) {
            dto.setOrganizationId(project.getOrganization().getId());
            dto.setOrganizationName(project.getOrganization().getName());
        }

        dto.setBiddingType(project.getBiddingType() != null ?
            project.getBiddingType().name() : null);
        dto.setIndustry(project.getIndustry());
        dto.setBudgetAmount(project.getBudgetAmount());
        dto.setCurrency(project.getCurrency());

        dto.setStartDate(project.getStartDate());
        dto.setEndDate(project.getEndDate());
        dto.setSubmissionDeadline(project.getSubmissionDeadline());

        dto.setStatus(project.getStatus().name());
        dto.setPriority(project.getPriority() != null ?
            project.getPriority().name() : null);
        dto.setWinProbability(project.getWinProbability());

        dto.setTags(project.getTags());

        // 统计信息
        dto.setMemberCount(project.getMembers().size());
        dto.setMilestoneCount(project.getMilestones().size());
        dto.setProgress(project.calculateProgress());

        // 创建和更新信息
        if (project.getCreatedBy() != null) {
            dto.setCreatedBy(project.getCreatedBy().getId());
            dto.setCreatedByName(project.getCreatedBy().getFullName());
        }
        dto.setCreatedAt(project.getCreatedAt());

        if (project.getUpdatedBy() != null) {
            dto.setUpdatedBy(project.getUpdatedBy().getId());
            dto.setUpdatedByName(project.getUpdatedBy().getFullName());
        }
        dto.setUpdatedAt(project.getUpdatedAt());

        return dto;
    }
}

/**
 * 项目简要信息 DTO（列表展示）
 * 需求编号: REQ-JAVA-002
 */
@Data
public class ProjectSimpleDTO {

    private UUID id;
    private String name;
    private String code;
    private String organizationName;
    private String status;
    private String priority;
    private Integer winProbability;
    private BigDecimal budgetAmount;
    private String currency;
    private LocalDateTime submissionDeadline;
    private Integer progress;
    private LocalDateTime createdAt;

    public static ProjectSimpleDTO from(Project project) {
        ProjectSimpleDTO dto = new ProjectSimpleDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setCode(project.getCode());
        dto.setOrganizationName(project.getOrganization() != null ?
            project.getOrganization().getName() : null);
        dto.setStatus(project.getStatus().name());
        dto.setPriority(project.getPriority() != null ?
            project.getPriority().name() : null);
        dto.setWinProbability(project.getWinProbability());
        dto.setBudgetAmount(project.getBudgetAmount());
        dto.setCurrency(project.getCurrency());
        dto.setSubmissionDeadline(project.getSubmissionDeadline());
        dto.setProgress(project.calculateProgress());
        dto.setCreatedAt(project.getCreatedAt());
        return dto;
    }
}

/**
 * 添加项目成员 DTO
 * 需求编号: REQ-JAVA-002
 */
@Data
public class AddProjectMemberDTO {

    @NotNull(message = "用户ID不能为空")
    private UUID userId;

    @NotBlank(message = "角色不能为空")
    private String role;  // OWNER, MANAGER, MEMBER, VIEWER

    private String[] permissions;  // 额外权限
}

/**
 * 项目成员响应 DTO
 * 需求编号: REQ-JAVA-002
 */
@Data
public class ProjectMemberResponseDTO {

    private UUID id;

    // 用户信息
    private UUID userId;
    private String username;
    private String fullName;
    private String email;
    private String avatarUrl;

    // 角色和权限
    private String role;
    private String roleDisplayName;
    private String[] permissions;

    private LocalDateTime joinedAt;
    private LocalDateTime createdAt;

    public static ProjectMemberResponseDTO from(ProjectMember member) {
        ProjectMemberResponseDTO dto = new ProjectMemberResponseDTO();
        dto.setId(member.getId());

        if (member.getUser() != null) {
            dto.setUserId(member.getUser().getId());
            dto.setUsername(member.getUser().getUsername());
            dto.setFullName(member.getUser().getFullName());
            dto.setEmail(member.getUser().getEmail());
            dto.setAvatarUrl(member.getUser().getAvatarUrl());
        }

        dto.setRole(member.getRole().name());
        dto.setRoleDisplayName(member.getRole().getDisplayName());
        dto.setPermissions(member.getPermissions());

        dto.setJoinedAt(member.getJoinedAt());
        dto.setCreatedAt(member.getCreatedAt());

        return dto;
    }
}
```
