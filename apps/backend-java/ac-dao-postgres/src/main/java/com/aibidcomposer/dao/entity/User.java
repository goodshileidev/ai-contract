package com.aibidcomposer.dao.entity;

import com.aibidcomposer.common.biz.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户实体
 *
 * 需求编号: REQ-JAVA-DAO-001
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "users", autoResultMap = true)
public class User extends BaseEntity {

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 全名
     */
    @TableField("full_name")
    private String fullName;

    /**
     * 加密后的密码
     */
    @TableField("hashed_password")
    private String hashedPassword;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 头像URL
     */
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 用户状态: active-活跃, inactive-未激活, suspended-已暂停
     */
    @TableField("status")
    private String status;

    /**
     * 邮箱是否已验证
     */
    @TableField("email_verified")
    private Boolean emailVerified;

    /**
     * 手机号是否已验证
     */
    @TableField("phone_verified")
    private Boolean phoneVerified;

    /**
     * 最后登录时间
     */
    @TableField("last_login_at")
    private LocalDateTime lastLoginAt;

    /**
     * 最后登录IP
     */
    @TableField("last_login_ip")
    private String lastLoginIp;

    /**
     * 登录次数
     */
    @TableField("login_count")
    private Integer loginCount;

    /**
     * 所属组织ID
     */
    @TableField("organization_id")
    private Long organizationId;

    /**
     * 用户设置(JSON)
     */
    @TableField(value = "settings", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> settings;

    /**
     * 元数据(JSON)
     */
    @TableField(value = "metadata", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;
}
