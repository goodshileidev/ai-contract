# Java Spring Boot - JAVA-002 Part1 (组织管理: 数据+前端) - 📑 文档导航 - 2.1.3: Java后端 - Controller层

**OrganizationController** (`com.aibidcomposer.controller.OrganizationController`):

```java
package com.aibidcomposer.controller;

import com.aibidcomposer.dto.common.ApiResponse;
import com.aibidcomposer.dto.common.PageResponse;
import com.aibidcomposer.dto.organization.*;
import com.aibidcomposer.entity.OrganizationRole;
import com.aibidcomposer.entity.OrganizationStatus;
import com.aibidcomposer.security.CurrentUser;
import com.aibidcomposer.security.UserPrincipal;
import com.aibidcomposer.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 组织管理控制器
 * 需求编号: REQ-JAVA-002
 *
 * 提供组织管理的REST API接口
 */
@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "组织管理", description = "组织和成员管理相关接口")
public class OrganizationController {

    private final OrganizationService organizationService;

    // ==================== 组织CRUD接口 ====================

    /**
     * 获取组织列表
     * GET /api/v1/organizations
     */
    @GetMapping
    @Operation(summary = "获取组织列表", description = "分页查询组织列表，支持搜索和过滤")
    public ApiResponse<PageResponse<OrganizationResponseDTO>> getOrganizations(
            @Parameter(description = "搜索关键词")
            @RequestParam(required = false) String search,

            @Parameter(description = "组织状态")
            @RequestParam(required = false) OrganizationStatus status,

            @Parameter(description = "页码（从0开始）")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "每页数量")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "排序字段")
            @RequestParam(defaultValue = "createdAt") String sortBy,

            @Parameter(description = "排序方向")
            @RequestParam(defaultValue = "DESC") Sort.Direction direction
    ) {
        log.info("GET /api/v1/organizations - search: {}, status: {}, page: {}, size: {}",
                search, status, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<OrganizationResponseDTO> organizations = organizationService.getOrganizations(
                search,
                status,
                pageable
        );

        PageResponse<OrganizationResponseDTO> response = PageResponse.of(organizations);

        return ApiResponse.success(response, "获取组织列表成功");
    }

    /**
     * 获取组织详情
     * GET /api/v1/organizations/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取组织详情", description = "根据ID获取组织的详细信息")
    public ApiResponse<OrganizationResponseDTO> getOrganizationById(
            @Parameter(description = "组织ID")
            @PathVariable UUID id
    ) {
        log.info("GET /api/v1/organizations/{}", id);

        OrganizationResponseDTO organization = organizationService.getOrganizationById(id);

        return ApiResponse.success(organization, "获取组织详情成功");
    }

    /**
     * 创建组织
     * POST /api/v1/organizations
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "创建组织", description = "创建新的组织，创建者自动成为组织所有者")
    public ApiResponse<OrganizationResponseDTO> createOrganization(
            @Parameter(description = "组织创建信息")
            @Valid @RequestBody CreateOrganizationDTO dto,

            @Parameter(hidden = true)
            @CurrentUser UserPrincipal currentUser
    ) {
        log.info("POST /api/v1/organizations - name: {}, by user: {}",
                dto.getName(), currentUser.getId());

        OrganizationResponseDTO organization = organizationService.createOrganization(
                dto,
                currentUser.getId()
        );

        return ApiResponse.success(organization, "组织创建成功");
    }

    /**
     * 更新组织
     * PUT /api/v1/organizations/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "更新组织", description = "更新组织信息，需要管理员或所有者权限")
    public ApiResponse<OrganizationResponseDTO> updateOrganization(
            @Parameter(description = "组织ID")
            @PathVariable UUID id,

            @Parameter(description = "组织更新信息")
            @Valid @RequestBody UpdateOrganizationDTO dto,

            @Parameter(hidden = true)
            @CurrentUser UserPrincipal currentUser
    ) {
        log.info("PUT /api/v1/organizations/{} - by user: {}", id, currentUser.getId());

        OrganizationResponseDTO organization = organizationService.updateOrganization(
                id,
                dto,
                currentUser.getId()
        );

        return ApiResponse.success(organization, "组织更新成功");
    }

    /**
     * 删除组织
     * DELETE /api/v1/organizations/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "删除组织", description = "删除组织（软删除），需要所有者权限")
    public ApiResponse<Void> deleteOrganization(
            @Parameter(description = "组织ID")
            @PathVariable UUID id,

            @Parameter(hidden = true)
            @CurrentUser UserPrincipal currentUser
    ) {
        log.info("DELETE /api/v1/organizations/{} - by user: {}", id, currentUser.getId());

        organizationService.deleteOrganization(id, currentUser.getId());

        return ApiResponse.success(null, "组织删除成功");
    }

    // ==================== 组织成员管理接口 ====================

    /**
     * 获取组织成员列表
     * GET /api/v1/organizations/{id}/members
     */
    @GetMapping("/{id}/members")
    @Operation(summary = "获取组织成员", description = "获取指定组织的成员列表")
    public ApiResponse<PageResponse<OrganizationMemberResponseDTO>> getOrganizationMembers(
            @Parameter(description = "组织ID")
            @PathVariable UUID id,

            @Parameter(description = "页码")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "每页数量")
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("GET /api/v1/organizations/{}/members - page: {}, size: {}", id, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<OrganizationMemberResponseDTO> members = organizationService.getOrganizationMembers(
                id,
                pageable
        );

        PageResponse<OrganizationMemberResponseDTO> response = PageResponse.of(members);

        return ApiResponse.success(response, "获取组织成员成功");
    }

    /**
     * 添加组织成员
     * POST /api/v1/organizations/{id}/members
     */
    @PostMapping("/{id}/members")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "添加组织成员", description = "向组织添加新成员，需要管理员或所有者权限")
    public ApiResponse<OrganizationMemberResponseDTO> addOrganizationMember(
            @Parameter(description = "组织ID")
            @PathVariable UUID id,

            @Parameter(description = "成员添加信息")
            @Valid @RequestBody AddMemberDTO dto,

            @Parameter(hidden = true)
            @CurrentUser UserPrincipal currentUser
    ) {
        log.info("POST /api/v1/organizations/{}/members - userId: {}, by user: {}",
                id, dto.getUserId(), currentUser.getId());

        OrganizationMemberResponseDTO member = organizationService.addOrganizationMember(
                id,
                dto,
                currentUser.getId()
        );

        return ApiResponse.success(member, "成员添加成功");
    }

    /**
     * 移除组织成员
     * DELETE /api/v1/organizations/{organizationId}/members/{memberId}
     */
    @DeleteMapping("/{organizationId}/members/{memberId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "移除组织成员", description = "从组织中移除成员，需要管理员或所有者权限")
    public ApiResponse<Void> removeOrganizationMember(
            @Parameter(description = "组织ID")
            @PathVariable UUID organizationId,

            @Parameter(description = "成员ID")
            @PathVariable UUID memberId,

            @Parameter(hidden = true)
            @CurrentUser UserPrincipal currentUser
    ) {
        log.info("DELETE /api/v1/organizations/{}/members/{} - by user: {}",
                organizationId, memberId, currentUser.getId());

        organizationService.removeOrganizationMember(organizationId, memberId, currentUser.getId());

        return ApiResponse.success(null, "成员移除成功");
    }

    /**
     * 更新成员角色
     * PUT /api/v1/organizations/{organizationId}/members/{memberId}/role
     */
    @PutMapping("/{organizationId}/members/{memberId}/role")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "更新成员角色", description = "更新组织成员的角色，需要所有者权限")
    public ApiResponse<OrganizationMemberResponseDTO> updateMemberRole(
            @Parameter(description = "组织ID")
            @PathVariable UUID organizationId,

            @Parameter(description = "成员ID")
            @PathVariable UUID memberId,

            @Parameter(description = "新角色")
            @RequestParam OrganizationRole newRole,

            @Parameter(hidden = true)
            @CurrentUser UserPrincipal currentUser
    ) {
        log.info("PUT /api/v1/organizations/{}/members/{}/role - newRole: {}, by user: {}",
                organizationId, memberId, newRole, currentUser.getId());

        OrganizationMemberResponseDTO member = organizationService.updateMemberRole(
                organizationId,
                memberId,
                newRole,
                currentUser.getId()
        );

        return ApiResponse.success(member, "成员角色更新成功");
    }

    // ==================== 组织层级接口 ====================

    /**
     * 获取根组织列表
     * GET /api/v1/organizations/roots
     */
    @GetMapping("/roots")
    @Operation(summary = "获取根组织", description = "获取没有父组织的顶级组织列表")
    public ApiResponse<PageResponse<OrganizationSimpleDTO>> getRootOrganizations(
            @Parameter(description = "页码")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "每页数量")
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("GET /api/v1/organizations/roots - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        Page<OrganizationSimpleDTO> roots = organizationService.getRootOrganizations(pageable);

        PageResponse<OrganizationSimpleDTO> response = PageResponse.of(roots);

        return ApiResponse.success(response, "获取根组织成功");
    }

    /**
     * 获取子组织列表
     * GET /api/v1/organizations/{id}/children
     */
    @GetMapping("/{id}/children")
    @Operation(summary = "获取子组织", description = "获取指定组织的直接子组织列表")
    public ApiResponse<List<OrganizationSimpleDTO>> getChildOrganizations(
            @Parameter(description = "父组织ID")
            @PathVariable UUID id
    ) {
        log.info("GET /api/v1/organizations/{}/children", id);

        List<OrganizationSimpleDTO> children = organizationService.getChildOrganizations(id);

        return ApiResponse.success(children, "获取子组织成功");
    }
}
```
