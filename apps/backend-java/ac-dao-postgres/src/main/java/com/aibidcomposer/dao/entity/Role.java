package com.aibidcomposer.dao.entity;

import com.aibidcomposer.common.biz.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * 角色实体
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
@TableName(value = "roles", autoResultMap = true)
public class Role extends BaseEntity {

    /**
     * 角色名称
     */
    @TableField("name")
    private String name;

    /**
     * 角色代码
     */
    @TableField("code")
    private String code;

    /**
     * 角色描述
     */
    @TableField("description")
    private String description;

    /**
     * 所属组织ID
     */
    @TableField("organization_id")
    private Long organizationId;

    /**
     * 是否系统角色
     */
    @TableField("is_system")
    private Boolean isSystem;

    /**
     * 角色级别
     */
    @TableField("level")
    private Integer level;

    /**
     * 权限代码数组(JSON)
     */
    @TableField(value = "permissions", typeHandler = JacksonTypeHandler.class)
    private List<String> permissions;

    /**
     * 元数据(JSON)
     */
    @TableField(value = "metadata", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;
}
