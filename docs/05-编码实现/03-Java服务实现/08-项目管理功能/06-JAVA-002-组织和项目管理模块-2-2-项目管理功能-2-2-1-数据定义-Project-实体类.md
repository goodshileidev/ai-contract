# Java Spring Boot - JAVA-002 Part1 (组织管理: 数据+前端) - JAVA-002: 组织和项目管理模块 - 2.2: 项目管理功能 - 2.2.1: 数据定义 - Project 实体类

```java
package com.aibidcomposer.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 项目实体
 * 需求编号: REQ-JAVA-002
 *
 * 投标项目管理，包括项目信息、状态跟踪、成员管理
 * 软删除设计，使用 deleted_at 字段
 */
@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE projects SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * 项目名称（必填，最大200字符）
     */
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /**
     * 项目编号（唯一，最大50字符）
     * 自动生成，格式：PRJ-YYYYMMDD-序号
     */
    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    /**
     * 项目描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 所属组织ID（必填）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    /**
     * 招标类型
     * government - 政府采购
     * enterprise - 企业采购
     * international - 国际招标
     * other - 其他
     */
    @Column(name = "bidding_type", length = 50)
    @Enumerated(EnumType.STRING)
    private BiddingType biddingType;

    /**
     * 所属行业
     */
    @Column(name = "industry", length = 100)
    private String industry;

    /**
     * 项目预算金额
     */
    @Column(name = "budget_amount", precision = 15, scale = 2)
    private BigDecimal budgetAmount;

    /**
     * 货币单位（默认CNY）
     */
    @Column(name = "currency", length = 10)
    @Builder.Default
    private String currency = "CNY";

    /**
     * 项目开始日期
     */
    @Column(name = "start_date")
    private LocalDate startDate;

    /**
     * 项目结束日期
     */
    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * 投标截止时间
     */
    @Column(name = "submission_deadline")
    private LocalDateTime submissionDeadline;

    /**
     * 项目状态
     * draft - 草稿
     * in_progress - 进行中
     * review - 审核中
     * submitted - 已提交
     * won - 中标
     * lost - 落标
     * archived - 已归档
     */
    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.DRAFT;

    /**
     * 项目优先级
     * low - 低
     * medium - 中
     * high - 高
     * urgent - 紧急
     */
    @Column(name = "priority", length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProjectPriority priority = ProjectPriority.MEDIUM;

    /**
     * 中标概率（0-100）
     */
    @Column(name = "win_probability")
    private Integer winProbability;

    /**
     * 项目标签（数组）
     */
    @Column(name = "tags", columnDefinition = "text[]")
    private String[] tags;

    /**
     * 项目设置（JSONB格式）
     * 存储项目配置参数等
     */
    @Column(name = "settings", columnDefinition = "jsonb DEFAULT '{}'::jsonb")
    private String settings;

    /**
     * 元数据（JSONB格式）
     * 存储扩展信息
     */
    @Column(name = "metadata", columnDefinition = "jsonb DEFAULT '{}'::jsonb")
    private String metadata;

    /**
     * 项目成员列表
     */
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ProjectMember> members = new HashSet<>();

    /**
     * 项目里程碑列表
     */
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ProjectMilestone> milestones = new HashSet<>();

    /**
     * 创建人ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false, updatable = false)
    private User createdBy;

    /**
     * 更新人ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    /**
     * 创建时间（自动填充）
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间（自动更新）
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 软删除时间
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // ==================== 业务方法 ====================

    /**
     * 添加项目成员
     */
    public void addMember(ProjectMember member) {
        members.add(member);
        member.setProject(this);
    }

    /**
     * 移除项目成员
     */
    public void removeMember(ProjectMember member) {
        members.remove(member);
        member.setProject(null);
    }

    /**
     * 添加里程碑
     */
    public void addMilestone(ProjectMilestone milestone) {
        milestones.add(milestone);
        milestone.setProject(this);
    }

    /**
     * 移除里程碑
     */
    public void removeMilestone(ProjectMilestone milestone) {
        milestones.remove(milestone);
        milestone.setProject(null);
    }

    /**
     * 检查项目是否可编辑
     */
    public boolean isEditable() {
        return status != ProjectStatus.ARCHIVED && status != ProjectStatus.SUBMITTED;
    }

    /**
     * 检查项目状态是否允许流转到目标状态
     */
    public boolean canTransitionTo(ProjectStatus targetStatus) {
        if (this.status == targetStatus) {
            return false;
        }

        // 状态流转规则
        return switch (this.status) {
            case DRAFT -> targetStatus == ProjectStatus.IN_PROGRESS;
            case IN_PROGRESS -> targetStatus == ProjectStatus.REVIEW ||
                               targetStatus == ProjectStatus.DRAFT;
            case REVIEW -> targetStatus == ProjectStatus.SUBMITTED ||
                          targetStatus == ProjectStatus.IN_PROGRESS;
            case SUBMITTED -> targetStatus == ProjectStatus.WON ||
                             targetStatus == ProjectStatus.LOST;
            case WON, LOST -> targetStatus == ProjectStatus.ARCHIVED;
            case ARCHIVED -> false;
        };
    }

    /**
     * 更新项目状态
     */
    public void updateStatus(ProjectStatus newStatus) {
        if (!canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("无法从状态 %s 流转到 %s", this.status, newStatus)
            );
        }
        this.status = newStatus;
    }

    /**
     * 计算项目进度百分比（0-100）
     */
    public int calculateProgress() {
        if (milestones.isEmpty()) {
            return 0;
        }

        long completedCount = milestones.stream()
            .filter(ProjectMilestone::isCompleted)
            .count();

        return (int) ((completedCount * 100) / milestones.size());
    }
}

/**
 * 招标类型枚举
 */
enum BiddingType {
    GOVERNMENT,      // 政府采购
    ENTERPRISE,      // 企业采购
    INTERNATIONAL,   // 国际招标
    OTHER            // 其他
}

/**
 * 项目状态枚举
 */
enum ProjectStatus {
    DRAFT,          // 草稿
    IN_PROGRESS,    // 进行中
    REVIEW,         // 审核中
    SUBMITTED,      // 已提交
    WON,            // 中标
    LOST,           // 落标
    ARCHIVED        // 已归档
}

/**
 * 项目优先级枚举
 */
enum ProjectPriority {
    LOW,            // 低
    MEDIUM,         // 中
    HIGH,           // 高
    URGENT          // 紧急
}
```
