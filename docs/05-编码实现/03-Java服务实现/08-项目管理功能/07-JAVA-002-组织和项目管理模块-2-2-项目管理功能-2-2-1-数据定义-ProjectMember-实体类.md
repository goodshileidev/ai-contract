# Java Spring Boot - JAVA-002 Part1 (组织管理: 数据+前端) - JAVA-002: 组织和项目管理模块 - 2.2: 项目管理功能 - 2.2.1: 数据定义 - ProjectMember 实体类

```java
package com.aibidcomposer.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 项目成员实体
 * 需求编号: REQ-JAVA-002
 *
 * 管理项目成员及其角色权限
 */
@Entity
@Table(name = "project_members", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"project_id", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * 所属项目
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    /**
     * 用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 项目角色
     * owner - 项目所有者
     * manager - 项目经理
     * member - 普通成员
     * viewer - 只读查看者
     */
    @Column(name = "role", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MemberRole role = MemberRole.MEMBER;

    /**
     * 额外权限（数组）
     * 除角色默认权限外的额外权限
     */
    @Column(name = "permissions", columnDefinition = "text[]")
    private String[] permissions;

    /**
     * 加入时间
     */
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    /**
     * 创建人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    /**
     * 创建时间（自动填充）
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ==================== 业务方法 ====================

    /**
     * 检查是否有指定权限
     */
    public boolean hasPermission(String permission) {
        // 先检查角色默认权限
        if (role.hasPermission(permission)) {
            return true;
        }

        // 再检查额外权限
        if (permissions != null) {
            for (String perm : permissions) {
                if (perm.equals(permission) || perm.equals("*")) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 检查是否为项目所有者
     */
    public boolean isOwner() {
        return role == MemberRole.OWNER;
    }

    /**
     * 检查是否为项目管理员
     */
    public boolean isManager() {
        return role == MemberRole.MANAGER || role == MemberRole.OWNER;
    }
}

/**
 * 项目成员角色枚举
 */
enum MemberRole {
    OWNER("项目所有者", new String[]{"*"}),
    MANAGER("项目经理", new String[]{
        "project:read", "project:update", "project:delete",
        "member:read", "member:create", "member:delete",
        "document:read", "document:create", "document:update", "document:delete"
    }),
    MEMBER("普通成员", new String[]{
        "project:read",
        "member:read",
        "document:read", "document:create", "document:update"
    }),
    VIEWER("只读查看者", new String[]{
        "project:read",
        "member:read",
        "document:read"
    });

    private final String displayName;
    private final String[] defaultPermissions;

    MemberRole(String displayName, String[] defaultPermissions) {
        this.displayName = displayName;
        this.defaultPermissions = defaultPermissions;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String[] getDefaultPermissions() {
        return defaultPermissions;
    }

    /**
     * 检查角色是否有指定权限
     */
    public boolean hasPermission(String permission) {
        for (String perm : defaultPermissions) {
            if (perm.equals(permission) || perm.equals("*")) {
                return true;
            }
        }
        return false;
    }
}
```
