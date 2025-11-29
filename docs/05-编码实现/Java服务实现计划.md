# Java Spring Boot 服务任务计划

**文档类型**: 实现文档
**需求编号**: REQ-JAVA-001 ~ REQ-JAVA-004
**创建日期**: 2025-11-26
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**最后更新**: 2025-11-26
**更新者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 待开始

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-26 | 1.0 | claude-sonnet-4-5 (claude-sonnet-4-5-20250929) | 从task-plan.md拆分出Java服务任务 |

---

## 模块概述

本模块包含 Java Spring Boot 服务的所有核心业务功能开发任务，负责：
- 用户认证和授权
- 组织和项目管理
- 模板管理
- 文档管理

**技术栈**: Java 17 + Spring Boot 3.2 + Spring Data JPA + Spring Security 6.x

**总体进度**: 0% (0/4 任务完成)

---

## JAVA-001: 用户认证授权模块

**需求编号**: REQ-JAVA-001
**负责人**: Java 后端开发
**优先级**: P1 - 高优先级
**开始时间**: YYYY-MM-DD
**预计完成**: YYYY-MM-DD
**实际完成**: -
**当前状态**: ⏸️ 待开始
**完成进度**: 0% (0/6 子任务)

### 子任务清单

#### 1. 用户管理基础功能
**完成进度**: 0/5
- [ ] User 实体设计（JPA Entity）
- [ ] UserRepository 接口
- [ ] UserService 业务逻辑
- [ ] UserController REST API
- [ ] 单元测试

#### 2. Spring Security 集成
**完成进度**: 0/4
- [ ] JWT Token 认证配置
- [ ] SecurityConfig 配置类
- [ ] UserDetailsService 实现
- [ ] 密码加密（BCrypt）

#### 3. 登录注册功能
**完成进度**: 0/4
- [ ] 用户注册 API（/api/auth/register）
- [ ] 用户登录 API（/api/auth/login）
- [ ] Token 刷新 API（/api/auth/refresh）
- [ ] 登出功能（Token 失效）

#### 4. 权限控制
**完成进度**: 0/4
- [ ] 角色管理（Role Entity）
- [ ] 基于角色的访问控制（RBAC）
- [ ] 权限注解（@PreAuthorize）
- [ ] API 权限拦截器

#### 5. 用户个人信息管理
**完成进度**: 0/4
- [ ] 获取当前用户信息 API
- [ ] 更新用户资料 API
- [ ] 修改密码 API
- [ ] 用户头像上传（MinIO集成）

#### 6. 数据库设计和迁移
**完成进度**: 0/4
- [ ] Flyway 迁移脚本：V1__create_user_tables.sql
- [ ] users 表设计
- [ ] roles 表设计
- [ ] user_roles 关联表设计

### 技术实现要点

**Spring Security 配置**:
- 使用 Spring Security 6.x + JWT
- 密码使用 BCryptPasswordEncoder
- Token 存储在 Redis（支持快速失效）
- 实现 RefreshToken 机制（7天有效期）

**API 接口设计**:
```
POST   /api/auth/register      # 用户注册
POST   /api/auth/login         # 用户登录
POST   /api/auth/refresh       # 刷新Token
POST   /api/auth/logout        # 登出
GET    /api/users/me           # 获取当前用户
PUT    /api/users/me           # 更新用户资料
PUT    /api/users/me/password  # 修改密码
```

### 遇到的问题

记录开发过程中遇到的问题和解决方案

### 相关文档

- 架构文档: `docs/03-架构/02-数据库设计.md`
- API文档: `docs/03-架构/03-API接口设计.md`
- 代码规范: `docs/99-知识/Java代码规范.md`

---

## JAVA-002: 组织和项目管理模块

**需求编号**: REQ-JAVA-002
**负责人**: Java 后端开发
**优先级**: P1 - 高优先级
**当前状态**: ⏸️ 待开始
**完成进度**: 0% (0/5 子任务)

### 子任务清单

#### 1. 组织管理功能
**完成进度**: 0/4
- [ ] Organization 实体设计
- [ ] 组织CRUD操作
- [ ] 组织成员管理
- [ ] 组织权限控制

#### 2. 项目管理功能
**完成进度**: 0/4
- [ ] Project 实体设计
- [ ] 项目CRUD操作
- [ ] 项目阶段管理
- [ ] 项目成员分配

#### 3. 招标项目管理
**完成进度**: 0/4
- [ ] BidProject 实体设计
- [ ] 招标文件关联
- [ ] 投标进度跟踪
- [ ] 项目归档

#### 4. 项目协作
**完成进度**: 0/3
- [ ] 项目成员权限
- [ ] 项目活动日志
- [ ] 项目通知机制

#### 5. 数据库设计
**完成进度**: 0/4
- [ ] Flyway 脚本：V2__create_org_project_tables.sql
- [ ] organizations 表
- [ ] projects 表
- [ ] bid_projects 表
- [ ] project_members 表

### 技术实现要点

**数据模型设计**:
- Organization: 企业组织信息
- Project: 投标项目信息
- BidProject: 招标项目详情
- ProjectMember: 项目成员和权限

**API 接口设计**:
```
# 组织管理
GET    /api/organizations           # 获取组织列表
POST   /api/organizations           # 创建组织
GET    /api/organizations/{id}      # 获取组织详情
PUT    /api/organizations/{id}      # 更新组织

# 项目管理
GET    /api/projects                # 获取项目列表
POST   /api/projects                # 创建项目
GET    /api/projects/{id}           # 获取项目详情
PUT    /api/projects/{id}           # 更新项目
POST   /api/projects/{id}/members   # 添加项目成员
```

---

## JAVA-003: 模板管理模块

**需求编号**: REQ-JAVA-003
**优先级**: P2 - 中优先级
**当前状态**: ⏸️ 待开始
**完成进度**: 0% (0/4 子任务)

### 子任务清单

#### 1. 模板基础管理
**完成进度**: 0/4
- [ ] Template 实体设计
- [ ] 模板CRUD操作
- [ ] 模板分类管理
- [ ] 模板版本控制

#### 2. 模板内容管理
**完成进度**: 0/4
- [ ] 模板结构定义（JSON Schema）
- [ ] 模板变量管理
- [ ] 模板片段管理
- [ ] 模板预览功能

#### 3. 模板共享
**完成进度**: 0/4
- [ ] 公共模板库
- [ ] 组织私有模板
- [ ] 模板权限控制
- [ ] 模板收藏和评分

#### 4. 数据库设计
**完成进度**: 0/3
- [ ] Flyway 脚本：V3__create_template_tables.sql
- [ ] templates 表
- [ ] template_versions 表
- [ ] template_categories 表

### 技术实现要点

**模板结构设计**:
- 使用 JSON Schema 定义模板结构
- 支持模板变量和占位符
- 模板版本控制（Git-like）

**API 接口设计**:
```
GET    /api/templates               # 获取模板列表
POST   /api/templates               # 创建模板
GET    /api/templates/{id}          # 获取模板详情
PUT    /api/templates/{id}          # 更新模板
POST   /api/templates/{id}/versions # 创建版本
```

---

## JAVA-004: 文档管理模块

**需求编号**: REQ-JAVA-004
**优先级**: P2 - 中优先级
**当前状态**: ⏸️ 待开始
**完成进度**: 0% (0/5 子任务)

### 子任务清单

#### 1. 文档基础管理
**完成进度**: 0/4
- [ ] Document 实体设计
- [ ] 文档CRUD操作
- [ ] 文档状态管理
- [ ] 文档关联管理

#### 2. 文件存储
**完成进度**: 0/4
- [ ] MinIO 集成
- [ ] 文件上传下载
- [ ] 文件预览
- [ ] 文件版本管理

#### 3. 文档版本控制
**完成进度**: 0/4
- [ ] Git-like 版本系统
- [ ] 版本比较
- [ ] 版本回退
- [ ] 版本分支管理

#### 4. 文档导出
**完成进度**: 0/4
- [ ] 导出为 PDF
- [ ] 导出为 Word
- [ ] 自定义导出格式
- [ ] 批量导出

#### 5. 数据库设计
**完成进度**: 0/3
- [ ] Flyway 脚本：V4__create_document_tables.sql
- [ ] documents 表
- [ ] document_versions 表
- [ ] document_files 表

### 技术实现要点

**MinIO 集成**:
- 文件上传到 MinIO 对象存储
- 生成预签名 URL 用于文件下载
- 文件版本管理

**文档导出**:
- 使用 Apache POI 导出 Word
- 使用 iText 或 Flying Saucer 导出 PDF
- 异步任务队列处理导出

**API 接口设计**:
```
GET    /api/documents               # 获取文档列表
POST   /api/documents               # 创建文档
GET    /api/documents/{id}          # 获取文档详情
PUT    /api/documents/{id}          # 更新文档
POST   /api/documents/{id}/versions # 创建版本
POST   /api/export/pdf              # 导出PDF
POST   /api/export/word             # 导出Word
```

---

## 技术栈和工具

### 核心框架
- **Java**: 17 LTS
- **Spring Boot**: 3.2.x
- **Spring Data JPA**: 3.2.x
- **Spring Security**: 6.x
- **Maven**: 3.9+

### 数据库
- **PostgreSQL**: 14+
- **Flyway**: 数据库迁移
- **HikariCP**: 连接池

### 工具库
- **Lombok**: 简化代码
- **MapStruct**: 对象映射
- **Apache Commons**: 工具类
- **Guava**: Google 工具类

### 测试
- **JUnit 5**: 单元测试
- **Mockito**: Mock 框架
- **Spring Boot Test**: 集成测试
- **TestContainers**: 容器化测试

### 文档和 API
- **Swagger/SpringDoc**: API 文档
- **Actuator**: 监控端点

---

## 里程碑

### M2: 用户和认证功能完成（2026-01-15）
- [ ] JAVA-001: 用户认证授权模块完成
- [ ] JAVA-002: 组织项目管理完成
- [ ] 所有 API 接口通过测试
- [ ] API 文档完成

### M3: 核心业务功能完成（2026-02-15）
- [ ] JAVA-003: 模板管理完成
- [ ] JAVA-004: 文档管理完成
- [ ] 集成测试通过
- [ ] 性能测试通过

---

## 开发规范

### 代码规范
参考: `docs/99-知识/Java代码规范.md`

### 命名规范
- **包名**: 全小写，如 `com.aibidcomposer.service`
- **类名**: 大驼峰，如 `UserService`
- **方法名**: 小驼峰，如 `findById`
- **常量**: 全大写+下划线，如 `MAX_RETRY_COUNT`

### 分层规范
```
Controller 层: 处理 HTTP 请求，参数验证
Service 层: 业务逻辑，事务管理
Repository 层: 数据访问
Entity 层: 数据模型
DTO 层: 数据传输对象
```

### 测试要求
- 单元测试覆盖率 > 80%
- 所有 Service 方法必须有单元测试
- Controller 层需要集成测试

---

## 常用命令

### Maven
```bash
mvn clean install      # 清理并构建
mvn test              # 运行单元测试
mvn verify            # 运行所有测试
mvn spring-boot:run   # 启动服务
```

### Flyway
```bash
mvn flyway:migrate    # 执行数据库迁移
mvn flyway:info       # 查看迁移状态
mvn flyway:clean      # 清理数据库
```

---

**返回**: [任务计划总览](./task-plan.md)
