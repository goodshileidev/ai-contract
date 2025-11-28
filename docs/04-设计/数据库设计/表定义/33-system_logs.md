# system_logs - 系统日志表

**文档类型**: 设计文档 - 数据库表设计
**表编号**: 33
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从02-数据库设计.md拆分独立表文档 |

---

### 33. system_logs (系统日志表)

```sql
CREATE TABLE system_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    log_level VARCHAR(20) NOT NULL CHECK (log_level IN ('DEBUG', 'INFO', 'WARNING', 'ERROR', 'CRITICAL')),
    service VARCHAR(50),
    module VARCHAR(100),
    message TEXT NOT NULL,
    details JSONB,
    trace_id VARCHAR(100),
    span_id VARCHAR(100),
    user_id UUID,
    ip_address INET,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 索引
CREATE INDEX idx_system_logs_log_level ON system_logs(log_level);
CREATE INDEX idx_system_logs_service ON system_logs(service);
CREATE INDEX idx_system_logs_trace_id ON system_logs(trace_id);
CREATE INDEX idx_system_logs_created_at ON system_logs(created_at DESC);

-- 转换为时序表
SELECT create_hypertable('system_logs', 'created_at', if_not_exists => TRUE);

-- 注释
COMMENT ON TABLE system_logs IS '系统日志表(时序数据)';
```

## 🔄 触发器和函数

### 更新时间戳触发器

```sql
-- 创建更新时间戳函数
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 为所有包含updated_at字段的表添加触发器
DO $$
DECLARE
    t text;
BEGIN
    FOR t IN
        SELECT tablename FROM pg_tables
        WHERE schemaname = 'public'
        AND tablename IN (
            'users', 'organizations', 'roles', 'projects', 'bidding_documents',
            'project_requirements', 'bid_documents', 'document_sections',
            'document_versions', 'document_comments', 'templates',
            'template_sections', 'company_profiles', 'products_services',
            'project_cases', 'personnel', 'certifications', 'historical_bids',
            'ai_tasks', 'ai_prompts', 'collaboration_sessions',
            'approval_workflows', 'approval_tasks'
        )
    LOOP
        EXECUTE format('
            CREATE TRIGGER update_%I_updated_at
            BEFORE UPDATE ON %I
            FOR EACH ROW
            EXECUTE FUNCTION update_updated_at_column();
        ', t, t);
    END LOOP;
END;
$$ LANGUAGE plpgsql;
```

### 软删除查询视图

```sql
-- 创建活跃用户视图
CREATE OR REPLACE VIEW active_users AS
SELECT * FROM users WHERE deleted_at IS NULL;

-- 创建活跃项目视图
CREATE OR REPLACE VIEW active_projects AS
SELECT * FROM projects WHERE deleted_at IS NULL;

-- 创建活跃文档视图
CREATE OR REPLACE VIEW active_bid_documents AS
SELECT * FROM bid_documents WHERE deleted_at IS NULL;
```

## 📈 性能优化

### 分区表配置

```sql
-- 为大表配置分区 (以audit_logs为例)
-- 按月分区
CREATE TABLE audit_logs_partitioned (
    LIKE audit_logs INCLUDING ALL
) PARTITION BY RANGE (created_at);

-- 创建分区
CREATE TABLE audit_logs_2025_01 PARTITION OF audit_logs_partitioned
    FOR VALUES FROM ('2025-01-01') TO ('2025-02-01');

CREATE TABLE audit_logs_2025_02 PARTITION OF audit_logs_partitioned
    FOR VALUES FROM ('2025-02-01') TO ('2025-03-01');

-- 继续创建其他月份分区...
```

### 物化视图

```sql
-- 项目统计物化视图
CREATE MATERIALIZED VIEW project_statistics AS
SELECT
    organization_id,
    COUNT(*) as total_projects,
    COUNT(*) FILTER (WHERE status = 'won') as won_projects,
    COUNT(*) FILTER (WHERE status = 'lost') as lost_projects,
    AVG(budget_amount) as avg_budget,
    AVG(win_probability) as avg_win_probability
FROM projects
WHERE deleted_at IS NULL
GROUP BY organization_id;

CREATE UNIQUE INDEX ON project_statistics (organization_id);

-- 刷新物化视图(定期执行)
REFRESH MATERIALIZED VIEW CONCURRENTLY project_statistics;
```

## 🔐 权限配置

```sql
-- 创建应用用户
CREATE USER aibidcomposer_app WITH PASSWORD 'your_secure_password';

-- 授予权限
GRANT CONNECT ON DATABASE aibidcomposer TO aibidcomposer_app;
GRANT USAGE ON SCHEMA public TO aibidcomposer_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO aibidcomposer_app;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO aibidcomposer_app;

-- 默认权限(新建表也自动授权)
ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO aibidcomposer_app;

ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT USAGE, SELECT ON SEQUENCES TO aibidcomposer_app;
```

## 📝 初始数据

### 初始权限数据

```sql
-- 插入系统权限
INSERT INTO permissions (name, code, resource, action, category, is_system) VALUES
('用户管理-查看', 'user:read', 'user', 'read', '用户管理', TRUE),
('用户管理-创建', 'user:create', 'user', 'create', '用户管理', TRUE),
('用户管理-编辑', 'user:update', 'user', 'update', '用户管理', TRUE),
('用户管理-删除', 'user:delete', 'user', 'delete', '用户管理', TRUE),
('项目管理-查看', 'project:read', 'project', 'read', '项目管理', TRUE),
('项目管理-创建', 'project:create', 'project', 'create', '项目管理', TRUE),
('项目管理-编辑', 'project:update', 'project', 'update', '项目管理', TRUE),
('项目管理-删除', 'project:delete', 'project', 'delete', '项目管理', TRUE),
('文档管理-查看', 'document:read', 'document', 'read', '文档管理', TRUE),
('文档管理-创建', 'document:create', 'document', 'create', '文档管理', TRUE),
('文档管理-编辑', 'document:update', 'document', 'update', '文档管理', TRUE),
('文档管理-删除', 'document:delete', 'document', 'delete', '文档管理', TRUE);

-- 插入系统角色
INSERT INTO roles (name, code, description, is_system, level, permissions) VALUES
('超级管理员', 'super_admin', '系统超级管理员，拥有所有权限', TRUE, 100, ARRAY['*']),
('管理员', 'admin', '组织管理员', TRUE, 80, ARRAY['user:*', 'project:*', 'document:*']),
('项目经理', 'project_manager', '项目经理', TRUE, 60, ARRAY['project:*', 'document:*']),
('普通成员', 'member', '普通成员', TRUE, 40, ARRAY['project:read', 'document:read', 'document:create', 'document:update']),
('访客', 'guest', '访客', TRUE, 20, ARRAY['project:read', 'document:read']);
```

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-26 | 1.1 | claude-sonnet-4-5 (claude-sonnet-4-5-20250929) | 更新数据库选型（Elasticsearch替代Pinecone和Neo4j），添加元信息头部和修改历史 |
| 2025-11-15 | 1.0 | claude-sonnet-4-5 (claude-sonnet-4-5-20250929) | 创建数据库设计文档，完成33张表设计 |

---

**文档状态**: ✅ 已批准
**文档版本**: v1.1
**最后更新**: 2025-11-26
**数据库版本**: PostgreSQL 14+
**总表数**: 33张表
**总索引数**: 150+ 个索引

---

## 相关表

请参考 [数据库设计总览](../INDEX.md) 查看所有相关表。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
