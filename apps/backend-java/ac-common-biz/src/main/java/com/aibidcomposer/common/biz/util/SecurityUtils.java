package com.aibidcomposer.common.biz.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

/**
 * 安全工具类
 * 用于获取当前登录用户信息
 *
 * 需求编号: REQ-JAVA-COMMON-BIZ-001
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
public class SecurityUtils {

    private SecurityUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 获取当前认证信息
     *
     * @return Authentication
     */
    public static Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * 获取当前登录用户名
     *
     * @return 用户名
     */
    public static Optional<String> getCurrentUsername() {
        return getAuthentication()
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof UserDetails)
                .map(principal -> ((UserDetails) principal).getUsername());
    }

    /**
     * 获取当前登录用户详情
     *
     * @return UserDetails
     */
    public static Optional<UserDetails> getCurrentUserDetails() {
        return getAuthentication()
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof UserDetails)
                .map(principal -> (UserDetails) principal);
    }

    /**
     * 判断当前用户是否已认证
     *
     * @return 是否已认证
     */
    public static boolean isAuthenticated() {
        return getAuthentication()
                .map(Authentication::isAuthenticated)
                .orElse(false);
    }

    /**
     * 判断当前用户是否拥有指定角色
     *
     * @param role 角色名称
     * @return 是否拥有角色
     */
    public static boolean hasRole(String role) {
        return getAuthentication()
                .map(auth -> auth.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role)))
                .orElse(false);
    }

    /**
     * 判断当前用户是否拥有指定权限
     *
     * @param permission 权限标识
     * @return 是否拥有权限
     */
    public static boolean hasPermission(String permission) {
        return getAuthentication()
                .map(auth -> auth.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals(permission)))
                .orElse(false);
    }
}
