# Java Spring Boot - JAVA-002 Part1 (组织管理: 数据+前端) - JAVA-002: 组织和项目管理模块 - 2.2: 项目管理功能 - 2.2.1: 数据定义 - ProjectMilestone 实体类

```java
package com.aibidcomposer.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 项目里程碑实体
 * 需求编号: REQ-JAVA-002
 *
 * 管理项目关键节点和进度跟踪
 */
@Entity
@Table(name = "project_milestones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ProjectMilestone {

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
     * 里程碑名称
     */
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /**
     * 里程碑描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 计划开始日期
     */
    @Column(name = "planned_start_date")
    private LocalDate plannedStartDate;

    /**
     * 计划完成日期
     */
    @Column(name = "planned_end_date")
    private LocalDate plannedEndDate;

    /**
     * 实际开始日期
     */
    @Column(name = "actual_start_date")
    private LocalDate actualStartDate;

    /**
     * 实际完成日期
     */
    @Column(name = "actual_end_date")
    private LocalDate actualEndDate;

    /**
     * 里程碑状态
     * pending - 待开始
     * in_progress - 进行中
     * completed - 已完成
     * overdue - 已逾期
     */
    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MilestoneStatus status = MilestoneStatus.PENDING;

    /**
     * 权重（用于计算项目进度，总和为100）
     */
    @Column(name = "weight")
    @Builder.Default
    private Integer weight = 10;

    /**
     * 排序顺序
     */
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

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

    // ==================== 业务方法 ====================

    /**
     * 检查里程碑是否已完成
     */
    public boolean isCompleted() {
        return status == MilestoneStatus.COMPLETED;
    }

    /**
     * 检查里程碑是否逾期
     */
    public boolean isOverdue() {
        if (isCompleted()) {
            return false;
        }

        LocalDate today = LocalDate.now();
        return plannedEndDate != null && today.isAfter(plannedEndDate);
    }

    /**
     * 标记为已完成
     */
    public void markAsCompleted() {
        this.status = MilestoneStatus.COMPLETED;
        this.actualEndDate = LocalDate.now();
    }

    /**
     * 开始里程碑
     */
    public void start() {
        this.status = MilestoneStatus.IN_PROGRESS;
        this.actualStartDate = LocalDate.now();
    }
}

/**
 * 里程碑状态枚举
 */
enum MilestoneStatus {
    PENDING,        // 待开始
    IN_PROGRESS,    // 进行中
    COMPLETED,      // 已完成
    OVERDUE         // 已逾期
}
```
