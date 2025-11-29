# Java Spring Boot - JAVA-002 Part1 (组织管理: 数据+前端) - 📑 文档导航 - 2.1.3: Java后端 - Repository层

**OrganizationRepository** (`com.aibidcomposer.repository.OrganizationRepository`):

```java
package com.aibidcomposer.repository;

import com.aibidcomposer.entity.Organization;
import com.aibidcomposer.entity.OrganizationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 组织数据访问接口
 * 需求编号: REQ-JAVA-002
 */
@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    /**
     * 根据名称查找组织（支持模糊搜索）
     */
    @Query("SELECT o FROM Organization o WHERE " +
           "LOWER(o.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(o.shortName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Organization> searchByName(@Param("search") String search, Pageable pageable);

    /**
     * 根据状态查找组织
     */
    Page<Organization> findByStatus(OrganizationStatus status, Pageable pageable);

    /**
     * 根据组织类型查找
     */
    @Query("SELECT o FROM Organization o WHERE o.organizationType = :type")
    Page<Organization> findByType(@Param("type") String type, Pageable pageable);

    /**
     * 根据父组织ID查找子组织
     */
    @Query("SELECT o FROM Organization o WHERE o.parent.id = :parentId")
    List<Organization> findByParentId(@Param("parentId") UUID parentId);

    /**
     * 查找根组织（没有父组织的组织）
     */
    @Query("SELECT o FROM Organization o WHERE o.parent IS NULL")
    Page<Organization> findRootOrganizations(Pageable pageable);

    /**
     * 根据税号查找组织
     */
    Optional<Organization> findByTaxId(String taxId);

    /**
     * 检查组织名称是否存在
     */
    boolean existsByName(String name);

    /**
     * 检查税号是否存在
     */
    boolean existsByTaxId(String taxId);

    /**
     * 统计组织数量（按状态）
     */
    @Query("SELECT COUNT(o) FROM Organization o WHERE o.status = :status")
    long countByStatus(@Param("status") OrganizationStatus status);

    /**
     * 获取组织及其所有子组织
     * 使用递归查询（PostgreSQL支持）
     */
    @Query(value = """
        WITH RECURSIVE org_tree AS (
            SELECT id, name, parent_id, 0 as depth
            FROM organizations
            WHERE id = :organizationId AND deleted_at IS NULL

            UNION ALL

            SELECT o.id, o.name, o.parent_id, ot.depth + 1
            FROM organizations o
            INNER JOIN org_tree ot ON o.parent_id = ot.id
            WHERE o.deleted_at IS NULL
        )
        SELECT * FROM org_tree
        """, nativeQuery = true)
    List<Object[]> findOrganizationTreeById(@Param("organizationId") UUID organizationId);

    /**
     * 获取组织的祖先链
     */
    @Query(value = """
        WITH RECURSIVE ancestors AS (
            SELECT id, name, parent_id, 0 as depth
            FROM organizations
            WHERE id = :organizationId AND deleted_at IS NULL

            UNION ALL

            SELECT o.id, o.name, o.parent_id, a.depth + 1
            FROM organizations o
            INNER JOIN ancestors a ON o.id = a.parent_id
            WHERE o.deleted_at IS NULL
        )
        SELECT * FROM ancestors ORDER BY depth DESC
        """, nativeQuery = true)
    List<Object[]> findAncestorsById(@Param("organizationId") UUID organizationId);
}
```

**OrganizationMemberRepository** (`com.aibidcomposer.repository.OrganizationMemberRepository`):

```java
package com.aibidcomposer.repository;

import com.aibidcomposer.entity.OrganizationMember;
import com.aibidcomposer.entity.OrganizationRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 组织成员数据访问接口
 * 需求编号: REQ-JAVA-002
 */
@Repository
public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, UUID> {

    /**
     * 根据组织ID查找成员
     */
    @Query("SELECT m FROM OrganizationMember m WHERE m.organization.id = :organizationId")
    Page<OrganizationMember> findByOrganizationId(
            @Param("organizationId") UUID organizationId,
            Pageable pageable
    );

    /**
     * 根据用户ID查找其所属的所有组织
     */
    @Query("SELECT m FROM OrganizationMember m WHERE m.userId = :userId")
    List<OrganizationMember> findByUserId(@Param("userId") UUID userId);

    /**
     * 查找用户在特定组织中的成员关系
     */
    @Query("SELECT m FROM OrganizationMember m WHERE " +
           "m.organization.id = :organizationId AND m.userId = :userId")
    Optional<OrganizationMember> findByOrganizationIdAndUserId(
            @Param("organizationId") UUID organizationId,
            @Param("userId") UUID userId
    );

    /**
     * 根据角色查找组织成员
     */
    @Query("SELECT m FROM OrganizationMember m WHERE " +
           "m.organization.id = :organizationId AND m.role = :role")
    List<OrganizationMember> findByOrganizationIdAndRole(
            @Param("organizationId") UUID organizationId,
            @Param("role") OrganizationRole role
    );

    /**
     * 检查用户是否为组织成员
     */
    @Query("SELECT COUNT(m) > 0 FROM OrganizationMember m WHERE " +
           "m.organization.id = :organizationId AND m.userId = :userId")
    boolean existsByOrganizationIdAndUserId(
            @Param("organizationId") UUID organizationId,
            @Param("userId") UUID userId
    );

    /**
     * 统计组织成员数量
     */
    @Query("SELECT COUNT(m) FROM OrganizationMember m WHERE m.organization.id = :organizationId")
    long countByOrganizationId(@Param("organizationId") UUID organizationId);

    /**
     * 统计组织管理员数量（OWNER + ADMIN）
     */
    @Query("SELECT COUNT(m) FROM OrganizationMember m WHERE " +
           "m.organization.id = :organizationId AND m.role IN ('OWNER', 'ADMIN')")
    long countAdminsByOrganizationId(@Param("organizationId") UUID organizationId);

    /**
     * 删除组织成员
     */
    @Query("DELETE FROM OrganizationMember m WHERE " +
           "m.organization.id = :organizationId AND m.userId = :userId")
    void deleteByOrganizationIdAndUserId(
            @Param("organizationId") UUID organizationId,
            @Param("userId") UUID userId
    );
}
```
