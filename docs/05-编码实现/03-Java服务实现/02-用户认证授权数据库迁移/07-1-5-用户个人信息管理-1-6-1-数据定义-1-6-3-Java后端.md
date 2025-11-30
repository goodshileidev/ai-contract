# Java Spring Boot 服务任务详细计划 - JAVA-001 Part3 - 1.5: 用户个人信息管理 - 1.6.1 数据定义 - 1.6.3 Java后端

**任务描述**: 管理Flyway迁移脚本，确保数据库schema与JPA实体同步

**关键实现**:

##### 1.6.3.1 Flyway配置

**文件**: `backend-java/src/main/resources/application.yml`

```yaml
# Flyway配置
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true
    baseline-version: 0
    locations: classpath:db/migration
    sql-migration-prefix: V
    sql-migration-suffix: .sql
    validate-on-migrate: true
    out-of-order: false
    clean-disabled: true  # 生产环境禁用clean
```

##### 1.6.3.2 迁移脚本管理

**目录结构**:
```
backend-java/
└── src/
    └── main/
        └── resources/
            └── db/
                └── migration/
                    ├── V1__create_users_table.sql
                    ├── V2__create_roles_table.sql
                    ├── V3__create_permissions_table.sql
                    ├── V4__create_user_roles_table.sql
                    ├── V5__create_role_permissions_table.sql
                    ├── V6__create_refresh_tokens_table.sql
                    ├── V7__create_login_history_table.sql
                    └── V8__insert_initial_data.sql
```

##### 1.6.3.3 迁移验证工具类

**文件**: `backend-java/src/main/java/com/aibidcomposer/util/FlywayValidator.java`

```java
package com.aibidcomposer.util;

import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Flyway迁移验证器
 * 需求编号: REQ-JAVA-001
 *
 * 在应用启动时验证数据库迁移状态
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class FlywayValidator {

    private final Flyway flyway;

    /**
     * 验证迁移状态
     */
    @Bean
    @Profile("!test")  // 测试环境跳过
    public CommandLineRunner validateMigrations() {
        return args -> {
            log.info("开始验证Flyway迁移状态...");

            // 获取迁移信息
            var info = flyway.info();
            var all = info.all();
            var current = info.current();
            var pending = info.pending();

            log.info("当前版本: {}", current != null ? current.getVersion() : "无");
            log.info("已应用迁移数: {}", all.length - pending.length);
            log.info("待应用迁移数: {}", pending.length);

            if (pending.length > 0) {
                log.warn("存在{}个待应用的迁移脚本:", pending.length);
                for (var migration : pending) {
                    log.warn("  - {} : {}", migration.getVersion(), migration.getDescription());
                }
            }

            // 验证迁移
            var result = flyway.validateWithResult();
            if (result.validationSuccessful) {
                log.info("✓ Flyway迁移验证通过");
            } else {
                log.error("✗ Flyway迁移验证失败:");
                result.invalidMigrations.forEach(error -> {
                    log.error("  - {}", error.errorDetails.errorMessage);
                });
                throw new IllegalStateException("数据库迁移验证失败");
            }
        };
    }
}
```

##### 1.6.3.4 数据库初始化测试

**文件**: `backend-java/src/test/java/com/aibidcomposer/db/FlywayMigrationTest.java`

```java
package com.aibidcomposer.db;

import static org.assertj.core.api.Assertions.assertThat;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Flyway迁移测试
 * 需求编号: REQ-JAVA-001
 */
@SpringBootTest
@ActiveProfiles("test")
class FlywayMigrationTest {

    @Autowired
    private Flyway flyway;

    @Autowired
    private DataSource dataSource;

    @Test
    void testMigrationsApplied() {
        // 验证迁移已应用
        var info = flyway.info();
        var all = info.all();
        var pending = info.pending();

        assertThat(pending).isEmpty();
        assertThat(all).hasSizeGreaterThanOrEqualTo(8);  // 至少8个迁移脚本
    }

    @Test
    void testTablesExist() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            var metaData = conn.getMetaData();

            // 验证所有表存在
            String[] tables = {
                "users", "roles", "permissions",
                "user_roles", "role_permissions",
                "refresh_tokens", "login_history"
            };

            for (String tableName : tables) {
                try (ResultSet rs = metaData.getTables(null, "public", tableName, null)) {
                    assertThat(rs.next())
                        .as("表 %s 应该存在", tableName)
                        .isTrue();
                }
            }
        }
    }

    @Test
    void testInitialDataExists() throws Exception {
        try (Connection conn = dataSource.getConnection();
             var stmt = conn.createStatement()) {

            // 验证系统角色存在
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM roles WHERE is_system = true");
            rs.next();
            assertThat(rs.getInt(1))
                .as("应该有5个系统角色")
                .isEqualTo(5);

            // 验证系统权限存在
            rs = stmt.executeQuery("SELECT COUNT(*) FROM permissions WHERE is_system = true");
            rs.next();
            assertThat(rs.getInt(1))
                .as("应该有至少20个系统权限")
                .isGreaterThanOrEqualTo(20);

            // 验证超级管理员角色有权限
            rs = stmt.executeQuery(
                "SELECT COUNT(*) FROM role_permissions WHERE role_id = '00000000-0000-0000-0000-000000000001'"
            );
            rs.next();
            assertThat(rs.getInt(1))
                .as("超级管理员应该有所有权限")
                .isGreaterThan(0);
        }
    }

    @Test
    void testTriggersExist() throws Exception {
        try (Connection conn = dataSource.getConnection();
             var stmt = conn.createStatement()) {

            // 验证updated_at触发器存在
            ResultSet rs = stmt.executeQuery(
                "SELECT COUNT(*) FROM pg_trigger WHERE tgname LIKE '%updated_at%'"
            );
            rs.next();
            assertThat(rs.getInt(1))
                .as("应该有updated_at触发器")
                .isGreaterThan(0);
        }
    }
}
```

##### 1.6.3.5 Maven命令集成

**文件**: `backend-java/pom.xml` (Flyway插件配置)

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-maven-plugin</artifactId>
            <version>9.22.0</version>
            <configuration>
                <url>${spring.datasource.url}</url>
                <user>${spring.datasource.username}</user>
                <password>${spring.datasource.password}</password>
                <locations>
                    <location>filesystem:src/main/resources/db/migration</location>
                </locations>
            </configuration>
        </plugin>
    </plugins>
</build>
```

**常用命令**:
```bash
# 查看迁移状态
mvn flyway:info

# 执行迁移
mvn flyway:migrate

# 验证迁移
mvn flyway:validate

# 查看迁移历史
mvn flyway:info -Dflyway.showInstalledOn=true
```

**验证检查清单**:

1. Java后端 (100%)
   - [ ] Flyway配置正确
   - [ ] 所有迁移脚本在正确位置
   - [ ] 迁移脚本命名规范（V1, V2, ...）
   - [ ] FlywayValidator运行正常
   - [ ] 单元测试覆盖率>80%
   - [ ] Maven命令可正常执行

---
