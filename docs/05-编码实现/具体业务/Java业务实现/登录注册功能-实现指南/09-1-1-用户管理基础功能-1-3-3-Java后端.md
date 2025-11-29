# Java Spring Boot 服务任务详细计划 - JAVA-001 Part1 - 1.1 用户管理基础功能 - 1.3.3 Java后端

#### AuthService（认证服务）

```java
// src/main/java/com/aibidcomposer/service/AuthService.java
package com.aibidcomposer.service;

import com.aibidcomposer.domain.Organization;
import com.aibidcomposer.domain.User;
import com.aibidcomposer.domain.enums.UserStatus;
import com.aibidcomposer.dto.auth.*;
import com.aibidcomposer.exception.AuthenticationFailedException;
import com.aibidcomposer.exception.ValidationException;
import com.aibidcomposer.repository.OrganizationRepository;
import com.aibidcomposer.repository.UserRepository;
import com.aibidcomposer.security.CustomUserDetailsService.CustomUserDetails;
import com.aibidcomposer.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 认证服务
 * 需求编号: REQ-JAVA-001
 *
 * @author AIBidComposer Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 用户ID
     * @throws ValidationException 验证失败
     */
    public UUID register(RegisterRequest request) {
        log.info("用户注册: email={}, username={}", request.getEmail(), request.getUsername());

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("邮箱已被使用");
        }

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ValidationException("用户名已被使用");
        }

        // 创建或获取组织
        Organization organization = null;
        if (request.getOrganizationName() != null && !request.getOrganizationName().isEmpty()) {
            organization = Organization.builder()
                    .name(request.getOrganizationName())
                    .status(OrganizationStatus.ACTIVE)
                    .build();
            organization = organizationRepository.save(organization);
        }

        // 创建用户
        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .fullName(request.getFullName())
                .hashedPassword(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.INACTIVE) // 需要邮箱验证后激活
                .emailVerified(false)
                .organization(organization)
                .build();

        user = userRepository.save(user);

        log.info("用户注册成功: userId={}, email={}", user.getId(), user.getEmail());

        // TODO: 发送邮箱验证邮件

        return user.getId();
    }

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @param ipAddress 客户端IP地址
     * @return 登录响应（包含Token）
     * @throws AuthenticationFailedException 认证失败
     */
    public LoginResponse login(LoginRequest request, String ipAddress) {
        log.info("用户登录: emailOrUsername={}", request.getEmailOrUsername());

        try {
            // 查找用户
            User user = findUserByEmailOrUsername(request.getEmailOrUsername());

            // 检查账号状态
            if (user.isLocked()) {
                throw new AuthenticationFailedException(
                        "账号已被锁定，请在" + user.getLockedUntil() + "后重试"
                );
            }

            if (user.getStatus() == UserStatus.SUSPENDED) {
                throw new AuthenticationFailedException("账号已被暂停");
            }

            // 执行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getId().toString(),
                            request.getPassword()
                    )
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            List<? extends GrantedAuthority> authorities = (List<? extends GrantedAuthority>) userDetails.getAuthorities();

            // 生成Token
            String accessToken = tokenProvider.generateAccessToken(user, authorities);
            String refreshToken = tokenProvider.generateRefreshToken(user.getId());

            // 记录登录信息
            userService.recordLogin(user.getId(), ipAddress);

            log.info("用户登录成功: userId={}, email={}", user.getId(), user.getEmail());

            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(3600L) // 1小时
                    .user(UserResponse.from(user))
                    .build();

        } catch (Exception ex) {
            log.error("用户登录失败: {}", ex.getMessage());

            // 增加失败登录次数
            try {
                User user = findUserByEmailOrUsername(request.getEmailOrUsername());
                userService.incrementFailedLoginAttempts(user.getId());
            } catch (Exception ignored) {
            }

            throw new AuthenticationFailedException("用户名或密码错误");
        }
    }

    /**
     * 刷新Token
     *
     * @param request 刷新Token请求
     * @return 新的Token
     * @throws AuthenticationFailedException Token无效
     */
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        log.debug("刷新Token");

        // 验证Refresh Token
        if (!tokenProvider.validateToken(request.getRefreshToken())) {
            throw new AuthenticationFailedException("Refresh Token无效");
        }

        // 提取用户ID
        UUID userId = tokenProvider.getUserIdFromToken(request.getRefreshToken());

        // 加载用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationFailedException("用户不存在"));

        // 生成新的Access Token
        // TODO: 从用户角色中提取权限
        List<GrantedAuthority> authorities = List.of();
        String accessToken = tokenProvider.generateAccessToken(user, authorities);

        log.info("Token刷新成功: userId={}", userId);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(request.getRefreshToken()) // 保持Refresh Token不变
                .tokenType("Bearer")
                .expiresIn(3600L)
                .user(UserResponse.from(user))
                .build();
    }

    /**
     * 用户登出
     *
     * @param userId 用户ID
     */
    public void logout(UUID userId) {
        log.info("用户登出: userId={}", userId);

        // TODO: 将Token加入黑名单（Redis）

        log.info("用户登出成功: userId={}", userId);
    }

    /**
     * 根据邮箱或用户名查找用户
     *
     * @param emailOrUsername 邮箱或用户名
     * @return 用户对象
     */
    private User findUserByEmailOrUsername(String emailOrUsername) {
        // 尝试作为邮箱查找
        if (emailOrUsername.contains("@")) {
            return userRepository.findByEmail(emailOrUsername)
                    .orElseThrow(() -> new AuthenticationFailedException("用户不存在"));
        }

        // 作为用户名查找
        return userRepository.findByUsername(emailOrUsername)
                .orElseThrow(() -> new AuthenticationFailedException("用户不存在"));
    }
}
```

#### AuthController（认证控制器）

```java
// src/main/java/com/aibidcomposer/controller/AuthController.java
package com.aibidcomposer.controller;

import com.aibidcomposer.dto.auth.*;
import com.aibidcomposer.dto.common.ApiResponse;
import com.aibidcomposer.dto.user.UserResponse;
import com.aibidcomposer.security.CustomUserDetailsService.CustomUserDetails;
import com.aibidcomposer.service.AuthService;
import com.aibidcomposer.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 认证API控制器
 * 需求编号: REQ-JAVA-001
 *
 * @author AIBidComposer Team
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    /**
     * 用户注册
     *
     * POST /api/v1/auth/register
     *
     * @param request 注册请求
     * @return 注册成功响应
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UUID>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        log.info("API请求: POST /api/v1/auth/register, email={}", request.getEmail());

        UUID userId = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(userId, "注册成功，请验证邮箱后登录"));
    }

    /**
     * 用户登录
     *
     * POST /api/v1/auth/login
     *
     * @param request 登录请求
     * @param httpRequest HTTP请求（用于获取IP地址）
     * @return 登录响应（包含Token）
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("API请求: POST /api/v1/auth/login, emailOrUsername={}",
                 request.getEmailOrUsername());

        String ipAddress = getClientIpAddress(httpRequest);
        LoginResponse response = authService.login(request, ipAddress);

        return ResponseEntity.ok(ApiResponse.success(response, "登录成功"));
    }

    /**
     * 刷新Token
     *
     * POST /api/v1/auth/refresh
     *
     * @param request 刷新Token请求
     * @return 新的Token
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        log.info("API请求: POST /api/v1/auth/refresh");

        LoginResponse response = authService.refreshToken(request);

        return ResponseEntity.ok(ApiResponse.success(response, "Token刷新成功"));
    }

    /**
     * 用户登出
     *
     * POST /api/v1/auth/logout
     *
     * @param userDetails 当前用户
     * @return 成功响应
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID userId = userDetails.getUser().getId();
        log.info("API请求: POST /api/v1/auth/logout, userId={}", userId);

        authService.logout(userId);

        return ResponseEntity.ok(ApiResponse.success(null, "登出成功"));
    }

    /**
     * 获取当前用户信息
     *
     * GET /api/v1/auth/me
     *
     * @param userDetails 当前用户
     * @return 用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID userId = userDetails.getUser().getId();
        log.debug("API请求: GET /api/v1/auth/me, userId={}", userId);

        UserResponse user = userService.getUserById(userId);

        return ResponseEntity.ok(ApiResponse.success(user, "获取用户信息成功"));
    }

    /**
     * 获取客户端IP地址
     *
     * @param request HTTP请求
     * @return IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
```

**验证标准**:
- [ ] 注册API测试通过
- [ ] 登录API测试通过
- [ ] Token刷新API测试通过
- [ ] 登出API测试通过
- [ ] 获取当前用户API测试通过
- [ ] 邮箱/用户名唯一性验证正确
- [ ] 密码加密正确
- [ ] IP地址记录正确
- [ ] 失败登录次数统计正确
- [ ] 账号锁定机制正常
