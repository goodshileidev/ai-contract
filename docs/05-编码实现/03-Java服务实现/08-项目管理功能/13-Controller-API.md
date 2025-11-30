# Java Spring Boot - JAVA-002 Part1 (组织管理: 数据+前端) - JAVA-002: 组织和项目管理模块 - 2.2.3: Java后端 - ProjectController REST API

```java
package com.aibidcomposer.controller;

import com.aibidcomposer.dto.project.*;
import com.aibidcomposer.entity.ProjectPriority;
import com.aibidcomposer.entity.ProjectStatus;
import com.aibidcomposer.security.CurrentUser;
import com.aibidcomposer.security.UserPrincipal;
import com.aibidcomposer.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 项目管理控制器
 * 需求编号: REQ-JAVA-002-2.2
 */
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "项目管理", description = "项目管理相关接口")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @Operation(summary = "创建项目", description = "创建新的投标项目")
    public ResponseEntity<ProjectResponseDTO> createProject(
            @Valid @RequestBody CreateProjectDTO dto,
            @CurrentUser UserPrincipal currentUser
    ) {
        ProjectResponseDTO response = projectService.createProject(dto, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "查询项目列表", description = "分页查询项目列表，支持筛选")
    public ResponseEntity<Page<ProjectSimpleDTO>> getProjects(
            @Parameter(description = "组织ID") @RequestParam UUID organizationId,
            @Parameter(description = "项目状态") @RequestParam(required = false) ProjectStatus status,
            @Parameter(description = "优先级") @RequestParam(required = false) ProjectPriority priority,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String search,
            @Parameter(description = "页码（从1开始）") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int pageSize,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection,
            @CurrentUser UserPrincipal currentUser
    ) {
        Pageable pageable = PageRequest.of(
                page - 1,
                pageSize,
                Sort.by(sortDirection, sortBy)
        );

        Page<ProjectSimpleDTO> projects = projectService.getProjects(
                organizationId,
                status,
                priority,
                search,
                pageable,
                currentUser.getId()
        );

        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询项目详情", description = "根据ID查询项目详细信息")
    public ResponseEntity<ProjectResponseDTO> getProjectById(
            @PathVariable UUID id,
            @CurrentUser UserPrincipal currentUser
    ) {
        ProjectResponseDTO response = projectService.getProjectById(id, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新项目", description = "更新项目信息")
    public ResponseEntity<ProjectResponseDTO> updateProject(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProjectDTO dto,
            @CurrentUser UserPrincipal currentUser
    ) {
        ProjectResponseDTO response = projectService.updateProject(id, dto, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除项目", description = "软删除项目")
    public ResponseEntity<Void> deleteProject(
            @PathVariable UUID id,
            @CurrentUser UserPrincipal currentUser
    ) {
        projectService.deleteProject(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "更新项目状态", description = "更新项目状态")
    public ResponseEntity<Void> updateProjectStatus(
            @PathVariable UUID id,
            @RequestParam ProjectStatus status,
            @CurrentUser UserPrincipal currentUser
    ) {
        projectService.updateProjectStatus(id, status, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/members")
    @Operation(summary = "添加项目成员", description = "向项目添加成员")
    public ResponseEntity<ProjectMemberResponseDTO> addProjectMember(
            @PathVariable UUID id,
            @Valid @RequestBody AddProjectMemberDTO dto,
            @CurrentUser UserPrincipal currentUser
    ) {
        ProjectMemberResponseDTO response = projectService.addProjectMember(
                id, dto, currentUser.getId()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{projectId}/members/{memberId}")
    @Operation(summary = "移除项目成员", description = "从项目中移除成员")
    public ResponseEntity<Void> removeProjectMember(
            @PathVariable UUID projectId,
            @PathVariable UUID memberId,
            @CurrentUser UserPrincipal currentUser
    ) {
        projectService.removeProjectMember(projectId, memberId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
```
