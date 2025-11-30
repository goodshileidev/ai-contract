# Java Spring Boot 服务任务详细计划 - JAVA-001 Part1 - 1.1 用户管理基础功能 - 1.1.3 Java后端

#### Repository 层

```java
// src/main/java/com/aibidcomposer/repository/UserRepository.java
package com.aibidcomposer.repository;

import com.aibidcomposer.domain.User;
import com.aibidcomposer.domain.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * 用户数据访问层
 * 需求编号: REQ-JAVA-001
 *
 * @author AIBidComposer Team
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    /**
     * 根据邮箱查找用户（忽略软删除）
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.deletedAt IS NULL")
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * 根据用户名查找用户（忽略软删除）
     */
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.deletedAt IS NULL")
    Optional<User> findByUsername(@Param("username") String username);

    /**
     * 检查邮箱是否已存在
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.deletedAt IS NULL")
    boolean existsByEmail(@Param("email") String email);

    /**
     * 检查用户名是否已存在
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username AND u.deletedAt IS NULL")
    boolean existsByUsername(@Param("username") String username);

    /**
     * 根据状态查询用户列表
     */
    Page<User> findByStatusAndDeletedAtIsNull(UserStatus status, Pageable pageable);

    /**
     * 根据组织ID查询用户列表
     */
    @Query("SELECT u FROM User u WHERE u.organization.id = :organizationId AND u.deletedAt IS NULL")
    Page<User> findByOrganizationId(@Param("organizationId") UUID organizationId, Pageable pageable);

    /**
     * 搜索用户（用户名或邮箱包含关键字）
     */
    @Query("SELECT u FROM User u WHERE " +
           "(LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "u.deletedAt IS NULL")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);
}
```

#### Service 层

```java
// src/main/java/com/aibidcomposer/service/UserService.java
package com.aibidcomposer.service;

import com.aibidcomposer.domain.User;
import com.aibidcomposer.domain.enums.UserStatus;
import com.aibidcomposer.dto.user.UpdateUserRequest;
import com.aibidcomposer.dto.user.UserResponse;
import com.aibidcomposer.exception.ResourceNotFoundException;
import com.aibidcomposer.exception.ValidationException;
import com.aibidcomposer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 用户业务逻辑服务
 * 需求编号: REQ-JAVA-001
 *
 * @author AIBidComposer Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 获取用户列表（分页）
     *
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @param status 状态筛选（可选）
     * @param keyword 搜索关键字（可选）
     * @param sortBy 排序字段
     * @param sortOrder 排序方向
     * @return 用户分页数据
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsers(
            int page,
            int pageSize,
            UserStatus status,
            String keyword,
            String sortBy,
            String sortOrder
    ) {
        log.debug("获取用户列表: page={}, pageSize={}, status={}, keyword={}",
                  page, pageSize, status, keyword);

        // 构建分页和排序
        Sort sort = Sort.by(
            "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC,
            sortBy != null ? sortBy : "createdAt"
        );
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        Page<User> userPage;

        // 根据条件查询
        if (keyword != null && !keyword.isEmpty()) {
            userPage = userRepository.searchUsers(keyword, pageable);
        } else if (status != null) {
            userPage = userRepository.findByStatusAndDeletedAtIsNull(status, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        return userPage.map(UserResponse::from);
    }

    /**
     * 根据ID获取用户详情
     *
     * @param userId 用户ID
     * @return 用户信息
     * @throws ResourceNotFoundException 用户不存在
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId) {
        log.debug("获取用户详情: userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        if (user.getDeletedAt() != null) {
            throw new ResourceNotFoundException("用户不存在");
        }

        return UserResponse.from(user);
    }

    /**
     * 根据邮箱获取用户
     *
     * @param email 邮箱地址
     * @return 用户信息
     * @throws ResourceNotFoundException 用户不存在
     */
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
    }

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户信息
     * @throws ResourceNotFoundException 用户不存在
     */
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
    }

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param request 更新请求
     * @return 更新后的用户信息
     * @throws ResourceNotFoundException 用户不存在
     * @throws ValidationException 验证失败
     */
    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
        log.info("更新用户信息: userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        // 检查邮箱唯一性
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ValidationException("邮箱已被使用");
            }
            user.setEmail(request.getEmail());
            user.setEmailVerified(false); // 重置邮箱验证状态
        }

        // 检查用户名唯一性
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new ValidationException("用户名已被使用");
            }
            user.setUsername(request.getUsername());
        }

        // 更新其他字段
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
            user.setPhoneVerified(false); // 重置手机验证状态
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        User savedUser = userRepository.save(user);
        log.info("用户信息更新成功: userId={}", userId);

        return UserResponse.from(savedUser);
    }

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param newStatus 新状态
     * @throws ResourceNotFoundException 用户不存在
     */
    public void updateUserStatus(UUID userId, UserStatus newStatus) {
        log.info("更新用户状态: userId={}, newStatus={}", userId, newStatus);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        user.setStatus(newStatus);
        userRepository.save(user);

        log.info("用户状态更新成功: userId={}, newStatus={}", userId, newStatus);
    }

    /**
     * 删除用户（软删除）
     *
     * @param userId 用户ID
     * @throws ResourceNotFoundException 用户不存在
     */
    public void deleteUser(UUID userId) {
        log.info("删除用户: userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("用户删除成功: userId={}", userId);
    }

    /**
     * 记录用户登录
     *
     * @param userId 用户ID
     * @param ipAddress IP地址
     */
    public void recordLogin(UUID userId, String ipAddress) {
        log.debug("记录用户登录: userId={}, ip={}", userId, ipAddress);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        user.recordLogin(ipAddress);
        userRepository.save(user);
    }

    /**
     * 增加失败登录次数
     *
     * @param userId 用户ID
     */
    public void incrementFailedLoginAttempts(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        user.incrementFailedLoginAttempts();
        userRepository.save(user);

        log.warn("用户登录失败次数增加: userId={}, attempts={}",
                 userId, user.getFailedLoginAttempts());
    }
}
```

#### Controller 层

```java
// src/main/java/com/aibidcomposer/controller/UserController.java
package com.aibidcomposer.controller;

import com.aibidcomposer.domain.enums.UserStatus;
import com.aibidcomposer.dto.common.ApiResponse;
import com.aibidcomposer.dto.common.PaginatedResponse;
import com.aibidcomposer.dto.user.UpdateUserRequest;
import com.aibidcomposer.dto.user.UpdateUserStatusRequest;
import com.aibidcomposer.dto.user.UserResponse;
import com.aibidcomposer.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 用户管理 REST API
 * 需求编号: REQ-JAVA-001
 *
 * @author AIBidComposer Team
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * 获取用户列表
     *
     * GET /api/v1/users
     *
     * @param page 页码（从1开始，默认1）
     * @param pageSize 每页大小（默认20）
     * @param status 状态筛选（可选）
     * @param keyword 搜索关键字（可选）
     * @param sortBy 排序字段（默认createdAt）
     * @param sortOrder 排序方向（asc/desc，默认desc）
     * @return 分页用户列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<PaginatedResponse<UserResponse>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        log.info("API请求: GET /api/v1/users, page={}, pageSize={}", page, pageSize);

        Page<UserResponse> userPage = userService.getUsers(
                page, pageSize, status, keyword, sortBy, sortOrder
        );

        return ResponseEntity.ok(PaginatedResponse.from(userPage, "获取用户列表成功"));
    }

    /**
     * 获取用户详情
     *
     * GET /api/v1/users/{userId}
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable UUID userId
    ) {
        log.info("API请求: GET /api/v1/users/{}", userId);

        UserResponse user = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(user, "获取用户详情成功"));
    }

    /**
     * 更新用户信息
     *
     * PUT /api/v1/users/{userId}
     *
     * @param userId 用户ID
     * @param request 更新请求
     * @return 更新后的用户信息
     */
    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        log.info("API请求: PUT /api/v1/users/{}", userId);

        UserResponse user = userService.updateUser(userId, request);
        return ResponseEntity.ok(ApiResponse.success(user, "用户信息更新成功"));
    }

    /**
     * 更新用户状态
     *
     * PATCH /api/v1/users/{userId}/status
     *
     * @param userId 用户ID
     * @param request 状态更新请求
     * @return 成功响应
     */
    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserStatusRequest request
    ) {
        log.info("API请求: PATCH /api/v1/users/{}/status, newStatus={}", userId, request.getStatus());

        userService.updateUserStatus(userId, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success(null, "用户状态更新成功"));
    }

    /**
     * 删除用户（软删除）
     *
     * DELETE /api/v1/users/{userId}
     *
     * @param userId 用户ID
     * @return 成功响应
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable UUID userId
    ) {
        log.info("API请求: DELETE /api/v1/users/{}", userId);

        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "用户删除成功"));
    }
}
```

**验证标准**:
- [ ] Repository 层所有查询方法测试通过
- [ ] Service 层业务逻辑单元测试覆盖率 >80%
- [ ] Controller 层 API 测试通过（使用 MockMvc）
- [ ] 软删除逻辑正确（deletedAt 字段）
- [ ] 分页和排序功能正常
- [ ] 异常处理正确（ResourceNotFoundException, ValidationException）
- [ ] 日志记录完整
- [ ] 权限注解正确配置
