# Java Spring Boot - JAVA-002 Part1 (组织管理: 数据+前端) - JAVA-002: 组织和项目管理模块 - 2.2.3: Java后端 - ProjectRepository 接口

```java
package com.aibidcomposer.repository;

import com.aibidcomposer.entity.Project;
import com.aibidcomposer.entity.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 项目数据访问接口
 * 需求编号: REQ-JAVA-002-2.2
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID>,
        JpaSpecificationExecutor<Project> {

    /**
     * 根据项目编号查询项目
     */
    Optional<Project> findByCodeAndDeletedAtIsNull(String code);

    /**
     * 根据组织ID查询项目列表（分页）
     */
    Page<Project> findByOrganizationIdAndDeletedAtIsNull(
            UUID organizationId,
            Pageable pageable
    );

    /**
     * 根据组织ID和状态查询项目列表
     */
    Page<Project> findByOrganizationIdAndStatusAndDeletedAtIsNull(
            UUID organizationId,
            ProjectStatus status,
            Pageable pageable
    );

    /**
     * 根据创建人查询项目列表
     */
    Page<Project> findByCreatedByIdAndDeletedAtIsNull(
            UUID createdById,
            Pageable pageable
    );

    /**
     * 查询即将到期的项目（提交截止时间在指定天数内）
     */
    @Query("SELECT p FROM Project p WHERE p.deletedAt IS NULL " +
           "AND p.submissionDeadline BETWEEN :now AND :deadline " +
           "AND p.status IN :statuses")
    List<Project> findUpcomingProjects(
            @Param("now") LocalDateTime now,
            @Param("deadline") LocalDateTime deadline,
            @Param("statuses") List<ProjectStatus> statuses
    );

    /**
     * 统计组织的项目数量（按状态）
     */
    @Query("SELECT p.status, COUNT(p) FROM Project p " +
           "WHERE p.organizationId = :organizationId AND p.deletedAt IS NULL " +
           "GROUP BY p.status")
    List<Object[]> countProjectsByStatus(@Param("organizationId") UUID organizationId);

    /**
     * 检查项目编号是否存在
     */
    boolean existsByCodeAndDeletedAtIsNull(String code);

    /**
     * 查询组织的所有项目（用于统计分析）
     */
    @Query("SELECT p FROM Project p WHERE p.organizationId = :organizationId " +
           "AND p.deletedAt IS NULL ORDER BY p.createdAt DESC")
    List<Project> findAllByOrganization(@Param("organizationId") UUID organizationId);
}
```
