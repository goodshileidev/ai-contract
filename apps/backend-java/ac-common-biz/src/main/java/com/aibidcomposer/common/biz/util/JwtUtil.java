package com.aibidcomposer.common.biz.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
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
public class JwtUtil {

    /**
     * JWT密钥
     */
    @Value("${jwt.secret:aibidcomposer-jwt-secret-key-must-be-at-least-256-bits}")
    private String secret;

    /**
     * JWT过期时间（毫秒）
     */
    @Value("${jwt.expiration:86400000}") // 默认24小时
    private Long expiration;

    /**
     * 生成JWT Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return JWT Token
     */
    public String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        return createToken(claims, username);
    }

    /**
     * 生成JWT Token（带额外声明）
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param claims   额外声明
     * @return JWT Token
     */
    public String generateToken(Long userId, String username, Map<String, Object> claims) {
        claims.put("userId", userId);
        claims.put("username", username);
        return createToken(claims, username);
    }

    /**
     * 创建Token
     *
     * @param claims  声明
     * @param subject 主题
     * @return JWT Token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从Token中获取用户名
     *
     * @param token JWT Token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * 从Token中获取用户ID
     *
     * @param token JWT Token
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从Token中获取过期时间
     *
     * @param token JWT Token
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    /**
     * 从Token中获取所有声明
     *
     * @param token JWT Token
     * @return Claims
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证Token是否过期
     *
     * @param token JWT Token
     * @return 是否过期
     */
    public Boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            log.error("Token过期验证失败", e);
            return true;
        }
    }

    /**
     * 验证Token
     *
     * @param token    JWT Token
     * @param username 用户名
     * @return 是否有效
     */
    public Boolean validateToken(String token, String username) {
        try {
            String usernameFromToken = getUsernameFromToken(token);
            return (usernameFromToken.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            log.error("Token验证失败", e);
            return false;
        }
    }

    /**
     * 获取签名密钥
     *
     * @return SecretKey
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
