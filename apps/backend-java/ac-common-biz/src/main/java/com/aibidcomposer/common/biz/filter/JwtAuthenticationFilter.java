package com.aibidcomposer.common.biz.filter;

import com.aibidcomposer.common.biz.util.JwtUtil;
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

/**
 * JWT认证过滤器
 *
 * 需求编号: REQ-JAVA-COMMON-BIZ-001
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // 从请求头中获取JWT Token
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                // 验证Token并设置认证信息
                authenticateUser(jwt, request);
            }
        } catch (Exception e) {
            log.error("无法设置用户认证信息", e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中获取JWT Token
     *
     * @param request HTTP请求
     * @return JWT Token
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * 验证用户并设置认证信息
     *
     * @param jwt     JWT Token
     * @param request HTTP请求
     */
    private void authenticateUser(String jwt, HttpServletRequest request) {
        try {
            // 从Token中获取用户名
            String username = jwtUtil.getUsernameFromToken(jwt);

            // 加载用户详情
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 验证Token
            if (jwtUtil.validateToken(jwt, username)) {
                // 创建认证对象
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 设置认证信息到Security上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("已为用户 '{}' 设置认证信息", username);
            }
        } catch (Exception e) {
            log.error("JWT认证失败", e);
        }
    }
}
