# Java Spring Boot 服务任务详细计划 - JAVA-001 Part2 - 1.4: 权限控制 - 1.4.3 Java后端

**验证清单**:
- [ ] RoleRepository 实现完成
- [ ] PermissionRepository 实现完成
- [ ] RoleService 业务逻辑完成
- [ ] PermissionService 业务逻辑完成
- [ ] RoleController API 完成
- [ ] @PreAuthorize 权限注解配置
- [ ] 单元测试覆盖率>80%

#### Repository 层

```java
// backend-java/src/main/java/com/aibidcomposer/repository/RoleRepository.java
package com.aibidcomposer.repository;

import com.aibidcomposer.domain.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * 角色Repository
 * 需求编号: REQ-JAVA-001
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    /**
     * 根据代码查询角色
     */
    Optional<Role> findByCode(String code);

    /**
     * 检查角色代码是否存在
     */
    boolean existsByCode(String code);

    /**
     * 分页查询角色（未软删除）
     */
    @Query("SELECT r FROM Role r WHERE r.deletedAt IS NULL " +
           "AND (:name IS NULL OR r.name LIKE %:name%) " +
           "AND (:code IS NULL OR r.code LIKE %:code%) " +
           "AND (:isSystem IS NULL OR r.isSystem = :isSystem)")
    Page<Role> findAllActive(String name, String code, Boolean isSystem, Pageable pageable);

    /**
     * 根据组织ID查询角色
     */
    @Query("SELECT r FROM Role r WHERE r.deletedAt IS NULL " +
           "AND r.organization.id = :organizationId")
    Set<Role> findByOrganizationId(UUID organizationId);

    /**
     * 查询系统角色
     */
    @Query("SELECT r FROM Role r WHERE r.deletedAt IS NULL AND r.isSystem = true")
    Set<Role> findSystemRoles();
}
```

```java
// backend-java/src/main/java/com/aibidcomposer/repository/PermissionRepository.java
package com.aibidcomposer.repository;

import com.aibidcomposer.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * 权限Repository
 * 需求编号: REQ-JAVA-001
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    /**
     * 根据代码查询权限
     */
    Optional<Permission> findByCode(String code);

    /**
     * 检查权限代码是否存在
     */
    boolean existsByCode(String code);

    /**
     * 根据分类查询权限
     */
    List<Permission> findByCategory(String category);

    /**
     * 根据资源和操作查询权限
     */
    Optional<Permission> findByResourceAndAction(String resource, String action);

    /**
     * 根据ID列表查询权限
     */
    @Query("SELECT p FROM Permission p WHERE p.id IN :ids")
    Set<Permission> findByIdIn(Set<UUID> ids);

    /**
     * 查询所有权限（按分类分组）
     */
    @Query("SELECT p FROM Permission p ORDER BY p.category, p.resource, p.action")
    List<Permission> findAllOrderByCategory();
}
```

```java
// backend-java/src/main/java/com/aibidcomposer/repository/UserRoleRepository.java
package com.aibidcomposer.repository;

import com.aibidcomposer.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 用户角色Repository
 * 需求编号: REQ-JAVA-001
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {

    /**
     * 查询用户的所有角色（未过期）
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.user.id = :userId " +
           "AND (ur.expiresAt IS NULL OR ur.expiresAt > CURRENT_TIMESTAMP)")
    List<UserRole> findActiveByUserId(UUID userId);

    /**
     * 删除用户的所有角色
     */
    void deleteByUserId(UUID userId);

    /**
     * 删除用户的特定角色
     */
    void deleteByUserIdAndRoleId(UUID userId, UUID roleId);
}
```

#### Service 层

```java
// backend-java/src/main/java/com/aibidcomposer/service/RoleService.java
package com.aibidcomposer.service;

import com.aibidcomposer.domain.Permission;
import com.aibidcomposer.domain.Role;
import com.aibidcomposer.domain.User;
import com.aibidcomposer.domain.UserRole;
import com.aibidcomposer.dto.role.AssignRoleRequest;
import com.aibidcomposer.dto.role.RoleRequest;
import com.aibidcomposer.dto.role.RoleResponse;
import com.aibidcomposer.exception.ResourceNotFoundException;
import com.aibidcomposer.exception.ValidationException;
import com.aibidcomposer.repository.PermissionRepository;
import com.aibidcomposer.repository.RoleRepository;
import com.aibidcomposer.repository.UserRepository;
import com.aibidcomposer.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 角色服务
 * 需求编号: REQ-JAVA-001
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    /**
     * 创建角色
     */
    public UUID createRole(RoleRequest request) {
        log.info("Creating role: {}", request.getCode());

        // 检查角色代码唯一性
        if (roleRepository.existsByCode(request.getCode())) {
            throw new ValidationException("角色代码已存在: " + request.getCode());
        }

        // 构建角色实体
        Role role = Role.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .level(request.getLevel() != null ? request.getLevel() : 0)
                .isSystem(false)
                .build();

        // 分配权限
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            Set<Permission> permissions = permissionRepository.findByIdIn(request.getPermissionIds());
            role.setPermissions(permissions);
        }

        Role savedRole = roleRepository.save(role);
        log.info("Role created successfully: {}", savedRole.getId());

        return savedRole.getId();
    }

    /**
     * 更新角色
     */
    public void updateRole(UUID roleId, RoleRequest request) {
        log.info("Updating role: {}", roleId);

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("角色不存在"));

        // 系统角色不可修改
        if (role.getIsSystem()) {
            throw new ValidationException("系统角色不可修改");
        }

        // 更新基本信息
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        if (request.getLevel() != null) {
            role.setLevel(request.getLevel());
        }

        // 更新权限
        if (request.getPermissionIds() != null) {
            Set<Permission> permissions = permissionRepository.findByIdIn(request.getPermissionIds());
            role.setPermissions(permissions);
        }

        roleRepository.save(role);
        log.info("Role updated successfully: {}", roleId);
    }

    /**
     * 删除角色（软删除）
     */
    public void deleteRole(UUID roleId) {
        log.info("Deleting role: {}", roleId);

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("角色不存在"));

        // 系统角色不可删除
        if (role.getIsSystem()) {
            throw new ValidationException("系统角色不可删除");
        }

        // 软删除
        role.setDeletedAt(LocalDateTime.now());
        roleRepository.save(role);

        log.info("Role deleted successfully: {}", roleId);
    }

    /**
     * 分配权限给角色
     */
    public void assignPermissions(UUID roleId, Set<UUID> permissionIds) {
        log.info("Assigning permissions to role: {}", roleId);

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("角色不存在"));

        Set<Permission> permissions = permissionRepository.findByIdIn(permissionIds);
        role.setPermissions(permissions);

        roleRepository.save(role);
        log.info("Permissions assigned successfully to role: {}", roleId);
    }

    /**
     * 分配角色给用户
     */
    public void assignRolesToUser(AssignRoleRequest request, UUID grantedBy) {
        log.info("Assigning roles to user: {}", request.getUserId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        User grantor = userRepository.findById(grantedBy)
                .orElseThrow(() -> new ResourceNotFoundException("授权人不存在"));

        // 删除用户现有角色
        userRoleRepository.deleteByUserId(request.getUserId());

        // 分配新角色
        for (UUID roleId : request.getRoleIds()) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new ResourceNotFoundException("角色不存在: " + roleId));

            UserRole userRole = UserRole.builder()
                    .user(user)
                    .role(role)
                    .grantedBy(grantor)
                    .grantedAt(LocalDateTime.now())
                    .expiresAt(request.getExpiresAt())
                    .build();

            userRoleRepository.save(userRole);
        }

        log.info("Roles assigned successfully to user: {}", request.getUserId());
    }

    /**
     * 分页查询角色
     */
    @Transactional(readOnly = true)
    public Page<RoleResponse> getRoles(String name, String code, Boolean isSystem, Pageable pageable) {
        Page<Role> roles = roleRepository.findAllActive(name, code, isSystem, pageable);
        return roles.map(RoleResponse::from);
    }

    /**
     * 根据ID查询角色
     */
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(UUID roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("角色不存在"));
        return RoleResponse.from(role);
    }

    /**
     * 查询用户的所有角色
     */
    @Transactional(readOnly = true)
    public Set<Role> getUserRoles(UUID userId) {
        List<UserRole> userRoles = userRoleRepository.findActiveByUserId(userId);
        return userRoles.stream()
                .map(UserRole::getRole)
                .collect(Collectors.toSet());
    }
}
```

```java
// backend-java/src/main/java/com/aibidcomposer/service/PermissionService.java
package com.aibidcomposer.service;

import com.aibidcomposer.domain.Permission;
import com.aibidcomposer.dto.permission.PermissionResponse;
import com.aibidcomposer.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限服务
 * 需求编号: REQ-JAVA-001
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PermissionService {

    private final PermissionRepository permissionRepository;

    /**
     * 查询所有权限
     */
    public List<PermissionResponse> getAllPermissions() {
        List<Permission> permissions = permissionRepository.findAllOrderByCategory();
        return permissions.stream()
                .map(PermissionResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 根据分类查询权限
     */
    public List<PermissionResponse> getPermissionsByCategory(String category) {
        List<Permission> permissions = permissionRepository.findByCategory(category);
        return permissions.stream()
                .map(PermissionResponse::from)
                .collect(Collectors.toList());
    }
}
```

#### Controller 层

```java
// backend-java/src/main/java/com/aibidcomposer/controller/RoleController.java
package com.aibidcomposer.controller;

import com.aibidcomposer.dto.common.ApiResponse;
import com.aibidcomposer.dto.common.PagedResponse;
import com.aibidcomposer.dto.role.AssignRoleRequest;
import com.aibidcomposer.dto.role.RoleRequest;
import com.aibidcomposer.dto.role.RoleResponse;
import com.aibidcomposer.service.RoleService;
import com.aibidcomposer.security.CurrentUser;
import com.aibidcomposer.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * 角色管理Controller
 * 需求编号: REQ-JAVA-001
 */
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Slf4j
public class RoleController {

    private final RoleService roleService;

    /**
     * 获取角色列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<ApiResponse<PagedResponse<RoleResponse>>> getRoles(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Boolean isSystem,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        Sort.Direction direction = sortOrder.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(direction, sortBy));

        Page<RoleResponse> roles = roleService.getRoles(name, code, isSystem, pageable);

        PagedResponse<RoleResponse> pagedResponse = new PagedResponse<>(
                roles.getContent(),
                roles.getTotalElements(),
                page,
                pageSize,
                roles.getTotalPages()
        );

        return ResponseEntity.ok(ApiResponse.success(pagedResponse, "获取成功"));
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleById(@PathVariable UUID id) {
        RoleResponse role = roleService.getRoleById(id);
        return ResponseEntity.ok(ApiResponse.success(role));
    }

    /**
     * 创建角色
     */
    @PostMapping
    @PreAuthorize("hasAuthority('role:create')")
    public ResponseEntity<ApiResponse<UUID>> createRole(
            @Valid @RequestBody RoleRequest request
    ) {
        UUID roleId = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(roleId, "角色创建成功"));
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<ApiResponse<Void>> updateRole(
            @PathVariable UUID id,
            @Valid @RequestBody RoleRequest request
    ) {
        roleService.updateRole(id, request);
        return ResponseEntity.ok(ApiResponse.success(null, "角色更新成功"));
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role:delete')")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success(null, "角色删除成功"));
    }

    /**
     * 分配权限给角色
     */
    @PostMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<ApiResponse<Void>> assignPermissions(
            @PathVariable UUID id,
            @RequestBody Set<UUID> permissionIds
    ) {
        roleService.assignPermissions(id, permissionIds);
        return ResponseEntity.ok(ApiResponse.success(null, "权限分配成功"));
    }

    /**
     * 分配角色给用户
     */
    @PostMapping("/assign")
    @PreAuthorize("hasAuthority('role:assign')")
    public ResponseEntity<ApiResponse<Void>> assignRolesToUser(
            @Valid @RequestBody AssignRoleRequest request,
            @CurrentUser UserPrincipal currentUser
    ) {
        roleService.assignRolesToUser(request, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "角色分配成功"));
    }
}
```

#### 权限注解使用示例

```java
// 在需要权限控制的Controller方法上使用@PreAuthorize

// 示例1: 检查是否有特定权限
@PreAuthorize("hasAuthority('user:create')")
public ResponseEntity<ApiResponse<UUID>> createUser(...) { }

// 示例2: 检查是否有任一权限
@PreAuthorize("hasAnyAuthority('user:update', 'user:admin')")
public ResponseEntity<ApiResponse<Void>> updateUser(...) { }

// 示例3: 检查是否有特定角色
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ApiResponse<Void>> deleteUser(...) { }

// 示例4: 复杂权限表达式
@PreAuthorize("hasAuthority('project:read') and #userId == principal.id")
public ResponseEntity<ApiResponse<ProjectResponse>> getProject(@PathVariable UUID userId) { }
```
