# Java Spring Boot 服务任务详细计划 - JAVA-001 Part1

**文档类型**: 实施文档
**需求编号**: REQ-JAVA-001 (子任务 1.1-1.3)
**创建日期**: 2025-11-26
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**最后更新**: 2025-11-27
**更新者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 待开始

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 14:50 | 2.0 | claude-sonnet-4-5 (claude-sonnet-4-5-20250929) | 从 task-plan-java-详细.md 拆分出 Part1 (子任务1.1-1.3) |
| 2025-11-26 | 1.0 | claude-sonnet-4-5 (claude-sonnet-4-5-20250929) | 创建Java服务详细任务计划 |

---

## 📑 文档导航

**返回索引**: [task-plan-java-详细-INDEX.md](./task-plan-java-详细-INDEX.md)

**其他部分**:
- [Part2: 子任务 1.4](./task-plan-java-详细-JAVA-001-Part2.md)
- [Part3: 子任务 1.5-1.6](./task-plan-java-详细-JAVA-001-Part3.md)

---

## 模块概述

本文档包含 **JAVA-001: 用户认证授权模块** 的前3个子任务：
- 1.1 用户管理基础功能
- 1.2 Spring Security 集成
- 1.3 登录注册功能

**技术栈**: Java 17 LTS + Spring Boot 3.2.x + Spring Data JPA + Spring Security 6.x

---

## JAVA-001: 用户认证授权模块

**需求编号**: REQ-JAVA-001
**负责人**: Java 后端开发
**优先级**: P1 - 高优先级
**开始时间**: YYYY-MM-DD
**预计完成**: YYYY-MM-DD
**实际完成**: -
**当前状态**: ⏸️ 待开始
**完成进度**: 0% (0/6 子任务)

### 模块目标

实现完整的用户认证授权系统，包括：
- JWT Token 认证机制
- 基于角色的访问控制（RBAC）
- 用户注册登录流程
- 用户个人信息管理
- 密码安全管理
- Token 刷新和失效机制

### 技术架构

```
前端 (React)
    ↓ POST /api/auth/login
Java Controller (UserController, AuthController)
    ↓
Spring Security Filter Chain
    ↓
JWT Token Service
    ↓
UserDetailsService → PostgreSQL (users, roles, permissions)
    ↓
Redis (Token Storage, Blacklist)
```

### 核心技术挑战

1. **安全性**: 密码加密（BCrypt）、Token 安全存储、防止暴力破解
2. **性能**: Token 验证高频调用、Redis 缓存优化
3. **扩展性**: 支持多种认证方式（OAuth2、SSO）的预留接口
4. **可靠性**: Token 刷新机制、优雅的过期处理

---

## 1.1 用户管理基础功能

**预计工作量**: 5 人天
**优先级**: P1
**依赖**: 无

### 技术实现概述

实现用户实体模型、数据访问层和基础业务逻辑，为认证授权提供数据基础。

---

### 1.2.1 数据定义

#### Redis Token 存储结构

```bash
# Token存储格式
# Key: token:{token_hash}
# Value: {user_id, issued_at, expires_at}
# TTL: JWT_EXPIRATION (默认3600秒)

# Refresh Token存储格式
# Key: refresh_token:{token_hash}
# Value: {user_id, access_token_hash, issued_at, expires_at}
# TTL: JWT_REFRESH_EXPIRATION (默认604800秒)

# Token黑名单（用于登出）
# Key: token_blacklist:{token_hash}
# Value: {user_id, revoked_at, reason}
# TTL: 剩余有效期

# 用户活跃Token集合
# Key: user_tokens:{user_id}
# Value: Set[token_hash1, token_hash2, ...]
# TTL: 不过期，手动清理
```

#### JWT Payload 结构

```java
// JWT Token Payload 结构
{
  "sub": "user_id",                    // 主题（用户ID）
  "username": "johndoe",               // 用户名
  "email": "john@example.com",         // 邮箱
  "organizationId": "org_uuid",        // 组织ID
  "roles": ["ADMIN", "MEMBER"],        // 角色列表
  "permissions": [                      // 权限列表
    "user:read",
    "user:write",
    "project:read"
  ],
  "iat": 1700000000,                    // 签发时间
  "exp": 1700003600,                    // 过期时间
  "jti": "token_unique_id"              // Token唯一ID
}
```

**验证标准**:
- [ ] Redis 连接配置正确
- [ ] Token 数据结构设计合理
- [ ] TTL 过期机制正确
- [ ] JWT Payload 包含所有必需字段

### 1.2.2 前端

#### Token 存储和请求拦截器

```typescript
// frontend/src/utils/auth.ts
import axios, { AxiosError, AxiosRequestConfig } from 'axios';

/**
 * Token 存储服务
 */
export class TokenStorage {
  private static ACCESS_TOKEN_KEY = 'access_token';
  private static REFRESH_TOKEN_KEY = 'refresh_token';
  private static TOKEN_EXPIRY_KEY = 'token_expiry';

  /**
   * 保存Token
   */
  static saveTokens(accessToken: string, refreshToken: string, expiresIn: number): void {
    localStorage.setItem(this.ACCESS_TOKEN_KEY, accessToken);
    localStorage.setItem(this.REFRESH_TOKEN_KEY, refreshToken);

    const expiryTime = new Date().getTime() + expiresIn * 1000;
    localStorage.setItem(this.TOKEN_EXPIRY_KEY, expiryTime.toString());
  }

  /**
   * 获取Access Token
   */
  static getAccessToken(): string | null {
    return localStorage.getItem(this.ACCESS_TOKEN_KEY);
  }

  /**
   * 获取Refresh Token
   */
  static getRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY);
  }

  /**
   * 检查Token是否即将过期（剩余时间<5分钟）
   */
  static isTokenExpiring(): boolean {
    const expiryTime = localStorage.getItem(this.TOKEN_EXPIRY_KEY);
    if (!expiryTime) return true;

    const now = new Date().getTime();
    const timeLeft = parseInt(expiryTime) - now;
    return timeLeft < 5 * 60 * 1000; // 5分钟
  }

  /**
   * 清除所有Token
   */
  static clearTokens(): void {
    localStorage.removeItem(this.ACCESS_TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    localStorage.removeItem(this.TOKEN_EXPIRY_KEY);
  }
}

/**
 * 配置Axios请求拦截器（自动添加Token）
 */
axios.interceptors.request.use(
  async (config: AxiosRequestConfig) => {
    // 检查Token是否即将过期
    if (TokenStorage.isTokenExpiring()) {
      const refreshToken = TokenStorage.getRefreshToken();
      if (refreshToken) {
        try {
          // 刷新Token
          const response = await axios.post('http://localhost:8080/api/v1/auth/refresh', {
            refreshToken,
          });

          const { accessToken, refreshToken: newRefreshToken, expiresIn } = response.data.data;
          TokenStorage.saveTokens(accessToken, newRefreshToken, expiresIn);
        } catch (error) {
          // 刷新失败，清除Token并跳转到登录页
          TokenStorage.clearTokens();
          window.location.href = '/login';
          return Promise.reject(error);
        }
      }
    }

    // 添加Authorization header
    const token = TokenStorage.getAccessToken();
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

/**
 * 配置Axios响应拦截器（处理401错误）
 */
axios.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    if (error.response?.status === 401) {
      // Token无效或过期
      TokenStorage.clearTokens();
      window.location.href = '/login';
    }

    return Promise.reject(error);
  }
);
```

**验证标准**:
- [ ] Token 自动添加到请求头
- [ ] Token 即将过期时自动刷新
- [ ] 401 错误自动跳转登录页
- [ ] 刷新Token失败正确处理
- [ ] Token 存储安全（使用 localStorage）

### 1.2.3 Java后端

#### JWT Token 工具类

```java
// src/main/java/com/aibidcomposer/security/JwtTokenProvider.java
package com.aibidcomposer.security;

import com.aibidcomposer.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * JWT Token 生成和验证工具类
 * 需求编号: REQ-JAVA-001
 */
@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成Access Token
     *
     * @param user 用户对象
     * @param authorities 权限列表
     * @return JWT Token
     */
    public String generateAccessToken(User user, List<? extends GrantedAuthority> authorities) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);

        List<String> permissions = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .claim("organizationId", user.getOrganization() != null ?
                       user.getOrganization().getId().toString() : null)
                .claim("permissions", permissions)
                .setId(UUID.randomUUID().toString()) // JWT ID
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成Refresh Token
     *
     * @param userId 用户ID
     * @return Refresh Token
     */
    public String generateRefreshToken(UUID userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration * 1000);

        return Jwts.builder()
                .setSubject(userId.toString())
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从Token中提取用户ID
     *
     * @param token JWT Token
     * @return 用户ID
     */
    public UUID getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return UUID.fromString(claims.getSubject());
    }

    /**
     * 从Token中提取所有Claims
     *
     * @param token JWT Token
     * @return Claims对象
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证Token是否有效
     *
     * @param token JWT Token
     * @return true-有效, false-无效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * 检查Token是否即将过期（剩余时间<5分钟）
     *
     * @param token JWT Token
     * @return true-即将过期, false-未过期
     */
    public boolean isTokenExpiring(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            Date now = new Date();
            long timeLeft = expiration.getTime() - now.getTime();
            return timeLeft < 5 * 60 * 1000; // 5分钟
        } catch (Exception e) {
            return true;
        }
    }
}
```

#### Spring Security 配置类

```java
// src/main/java/com/aibidcomposer/config/SecurityConfig.java
package com.aibidcomposer.config;

import com.aibidcomposer.security.JwtAuthenticationEntryPoint;
import com.aibidcomposer.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security 配置
 * 需求编号: REQ-JAVA-001
 *
 * @author AIBidComposer Team
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 密码编码器（BCrypt）
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // 强度12
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 认证提供者
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * CORS 配置
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:3000",
                "https://www.aibidcomposer.com"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 安全过滤器链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF（使用JWT，无需CSRF保护）
                .csrf(csrf -> csrf.disable())

                // CORS 配置
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 无状态会话管理
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 异常处理
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )

                // 授权配置
                .authorizeHttpRequests(auth -> auth
                        // 公开端点（无需认证）
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // 静态资源
                        .requestMatchers("/static/**", "/public/**").permitAll()

                        // OPTIONS 请求（CORS 预检）
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 所有其他请求需要认证
                        .anyRequest().authenticated()
                )

                // 认证提供者
                .authenticationProvider(authenticationProvider())

                // JWT 过滤器（在 UsernamePasswordAuthenticationFilter 之前）
                .addFilterBefore(jwtAuthenticationFilter,
                                 UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

#### JWT 认证过滤器

```java
// src/main/java/com/aibidcomposer/security/JwtAuthenticationFilter.java
package com.aibidcomposer.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * JWT 认证过滤器
 * 需求编号: REQ-JAVA-001
 *
 * 从请求头中提取JWT Token，验证并设置SecurityContext
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // 从请求头中提取Token
            String token = extractTokenFromRequest(request);

            if (token != null && tokenProvider.validateToken(token)) {
                // 从Token中提取用户ID
                UUID userId = tokenProvider.getUserIdFromToken(token);

                // 加载用户详情
                UserDetails userDetails = userDetailsService.loadUserByUsername(userId.toString());

                // 创建认证对象
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 设置SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("JWT认证成功: userId={}", userId);
            }
        } catch (Exception ex) {
            log.error("无法设置用户认证: {}", ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中提取Token
     *
     * @param request HTTP请求
     * @return JWT Token，如果不存在返回null
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // 移除"Bearer "前缀
        }

        return null;
    }
}
```

#### 认证入口点（处理未认证请求）

```java
// src/main/java/com/aibidcomposer/security/JwtAuthenticationEntryPoint.java
package com.aibidcomposer.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.aibidcomposer.dto.common.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * JWT 认证入口点
 * 需求编号: REQ-JAVA-001
 *
 * 处理未认证的请求，返回401错误
 */
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        log.error("未认证访问: {}", authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("UNAUTHORIZED")
                .message("未认证，请先登录")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
```

#### UserDetailsService 实现

```java
// src/main/java/com/aibidcomposer/security/CustomUserDetailsService.java
package com.aibidcomposer.security;

import com.aibidcomposer.domain.User;
import com.aibidcomposer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 自定义 UserDetailsService 实现
 * 需求编号: REQ-JAVA-001
 *
 * @author AIBidComposer Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 根据用户名（这里是用户ID）加载用户详情
     *
     * @param username 用户ID字符串
     * @return UserDetails对象
     * @throws UsernameNotFoundException 用户不存在
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UUID userId;
        try {
            userId = UUID.fromString(username);
        } catch (IllegalArgumentException e) {
            throw new UsernameNotFoundException("无效的用户ID: " + username);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UsernameNotFoundException("用户不存在: " + userId)
                );

        if (user.getDeletedAt() != null) {
            throw new UsernameNotFoundException("用户已被删除: " + userId);
        }

        return new CustomUserDetails(user);
    }

    /**
     * 自定义 UserDetails 实现
     */
    public static class CustomUserDetails implements UserDetails {
        private final User user;
        private final Collection<? extends GrantedAuthority> authorities;

        public CustomUserDetails(User user) {
            this.user = user;
            // TODO: 从用户的角色中提取权限
            // 暂时返回基础权限
            this.authorities = List.of(
                    new SimpleGrantedAuthority("user:read"),
                    new SimpleGrantedAuthority("user:write")
            );
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public String getPassword() {
            return user.getHashedPassword();
        }

        @Override
        public String getUsername() {
            return user.getId().toString();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return !user.isLocked();
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return user.isActive();
        }

        public User getUser() {
            return user;
        }
    }
}
```

**验证标准**:
- [ ] JWT Token 生成和验证正确
- [ ] Token 包含所有必需的 Claims
- [ ] SecurityFilterChain 配置正确
- [ ] JWT 过滤器在正确位置执行
- [ ] 未认证请求返回 401 错误
- [ ] CORS 配置正确
- [ ] UserDetailsService 正确加载用户
- [ ] 密码使用 BCrypt 加密
- [ ] 权限注解生效（@PreAuthorize）

### 1.2.4 Python后端

> **说明**: Python AI 服务不直接实现认证，但需要验证来自前端的 JWT Token。

#### JWT Token 验证（Python）

```python
# backend/fastapi-ai-service/app/core/security.py
"""
JWT Token验证
需求编号: REQ-JAVA-001
"""
from typing import Optional, Dict, Any
from datetime import datetime, timedelta
from jose import jwt, JWTError
from fastapi import HTTPException, Security
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from app.core.config import settings
from app.core.logging import logger

# HTTP Bearer 认证方案
security = HTTPBearer()

class JWTValidator:
    """
    JWT Token验证器
    验证来自前端的JWT Token（由Java服务签发）
    """

    def __init__(self):
        self.secret_key = settings.JWT_SECRET
        self.algorithm = "HS256"

    def verify_token(self, token: str) -> Dict[str, Any]:
        """
        验证JWT Token

        Args:
            token: JWT Token字符串

        Returns:
            Token payload（包含user_id, username, permissions等）

        Raises:
            HTTPException: Token无效或过期
        """
        try:
            payload = jwt.decode(
                token,
                self.secret_key,
                algorithms=[self.algorithm]
            )

            # 检查过期时间
            exp = payload.get("exp")
            if exp is None:
                raise HTTPException(
                    status_code=401,
                    detail="Token缺少过期时间"
                )

            if datetime.fromtimestamp(exp) < datetime.utcnow():
                raise HTTPException(
                    status_code=401,
                    detail="Token已过期"
                )

            return payload

        except JWTError as e:
            logger.error(f"JWT验证失败: {str(e)}")
            raise HTTPException(
                status_code=401,
                detail="无效的Token"
            )

    def extract_user_id(self, token: str) -> str:
        """
        从Token中提取用户ID

        Args:
            token: JWT Token

        Returns:
            用户ID字符串
        """
        payload = self.verify_token(token)
        user_id = payload.get("sub")

        if not user_id:
            raise HTTPException(
                status_code=401,
                detail="Token缺少用户ID"
            )

        return user_id

    def extract_permissions(self, token: str) -> list[str]:
        """
        从Token中提取权限列表

        Args:
            token: JWT Token

        Returns:
            权限列表
        """
        payload = self.verify_token(token)
        return payload.get("permissions", [])

    def check_permission(self, token: str, required_permission: str) -> bool:
        """
        检查Token是否具有指定权限

        Args:
            token: JWT Token
            required_permission: 所需权限

        Returns:
            True-有权限, False-无权限
        """
        permissions = self.extract_permissions(token)
        return required_permission in permissions

# 全局实例
jwt_validator = JWTValidator()

# FastAPI Dependency
async def get_current_user_id(
    credentials: HTTPAuthorizationCredentials = Security(security)
) -> str:
    """
    FastAPI依赖注入：获取当前用户ID

    使用方式:
        @app.get("/api/ai/tasks")
        async def get_tasks(user_id: str = Depends(get_current_user_id)):
            ...
    """
    token = credentials.credentials
    return jwt_validator.extract_user_id(token)

async def require_permission(required_permission: str):
    """
    FastAPI依赖注入：检查权限

    使用方式:
        @app.post("/api/ai/tasks")
        async def create_task(
            _: None = Depends(require_permission("ai:generate"))
        ):
            ...
    """
    def permission_checker(
        credentials: HTTPAuthorizationCredentials = Security(security)
    ):
        token = credentials.credentials
        if not jwt_validator.check_permission(token, required_permission):
            raise HTTPException(
                status_code=403,
                detail=f"缺少权限: {required_permission}"
            )
        return None

    return permission_checker
```

#### Python API 路由（使用JWT认证）

```python
# backend/fastapi-ai-service/app/api/v1/endpoints/ai_tasks.py
"""
AI任务API端点
需求编号: REQ-AI-001, REQ-JAVA-001
"""
from fastapi import APIRouter, Depends, HTTPException
from typing import List
from app.core.security import get_current_user_id, require_permission
from app.schemas.ai_task import AITaskCreate, AITaskResponse
from app.services.ai.task_service import ai_task_service

router = APIRouter()

@router.get("/tasks", response_model=List[AITaskResponse])
async def get_ai_tasks(
    user_id: str = Depends(get_current_user_id)  # JWT认证
):
    """
    获取AI任务列表
    需要认证
    """
    tasks = await ai_task_service.get_user_tasks(user_id)
    return tasks

@router.post("/tasks", response_model=AITaskResponse)
async def create_ai_task(
    task_data: AITaskCreate,
    user_id: str = Depends(get_current_user_id),
    _: None = Depends(require_permission("ai:generate"))  # 权限检查
):
    """
    创建AI任务
    需要认证和"ai:generate"权限
    """
    task = await ai_task_service.create_task(task_data, user_id)
    return task
```

**验证标准**:
- [ ] JWT Token 验证正确
- [ ] Token 过期检查正确
- [ ] 用户ID 提取正确
- [ ] 权限检查正确
- [ ] 401/403 错误正确返回
- [ ] FastAPI Depends 正确使用

### 1.2.5 部署

#### application.yml 更新（JWT配置）

```yaml
# backend-java/src/main/resources/application-dev.yml
# JWT配置
jwt:
  secret: ${JWT_SECRET:default_secret_key_for_development_only_min_32_chars}
  expiration: ${JWT_EXPIRATION:3600}           # 1小时
  refresh-expiration: ${JWT_REFRESH_EXPIRATION:604800}  # 7天
```

#### 环境变量更新

```bash
# .env
# JWT配置
JWT_SECRET=your_jwt_secret_key_must_be_at_least_32_characters_long_here
JWT_EXPIRATION=3600
JWT_REFRESH_EXPIRATION=604800
```

**验证标准**:
- [ ] JWT_SECRET 环境变量正确配置（长度>=32）
- [ ] Token 过期时间配置生效
- [ ] Docker 容器环境变量正确传递

### 子任务总结

#### 完成标准

**1.2 Spring Security 集成** 被认为完成需要满足：

1. **数据定义** (100%)
   - [ ] Redis Token 存储结构设计合理
   - [ ] JWT Payload 结构完整

2. **前端** (100%)
   - [ ] Token 自动添加到请求头
   - [ ] Token 自动刷新机制正常
   - [ ] 401错误正确处理

3. **Java后端** (100%)
   - [ ] JWT Token 生成和验证测试通过
   - [ ] Security Filter Chain 配置正确
   - [ ] 认证和授权流程测试通过
   - [ ] 密码加密正确（BCrypt）

4. **Python后端** (100%)
   - [ ] JWT Token 验证正确
   - [ ] FastAPI Depends 认证正常
   - [ ] 权限检查功能正常

5. **部署** (100%)
   - [ ] JWT 配置正确加载
   - [ ] 环境变量配置完整

---

## 1.3 登录注册功能

**预计工作量**: 4 人天
**优先级**: P1
**依赖**: 1.1 用户管理基础功能, 1.2 Spring Security 集成

### 技术实现概述

实现用户注册、登录、登出和 Token 刷新功能，集成邮箱验证、验证码、防暴力破解等安全机制。
