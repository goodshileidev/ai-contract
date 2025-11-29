# roles - 角色表

**文档类型**: 设计文档 - 数据库表设计
**表编号**: 3
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从02-数据库设计.md拆分独立表文档 |

---

### 3. roles (角色表)

```sql
CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    organization_id UUID,
    is_system BOOLEAN DEFAULT FALSE,
    level INTEGER DEFAULT 0,
    permissions TEXT[] DEFAULT ARRAY[]::TEXT[],
    metadata JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE,
    FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE
);

-- 索引
CREATE INDEX idx_roles_code ON roles(code) WHERE deleted_at IS NULL;
CREATE INDEX idx_roles_organization_id ON roles(organization_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_roles_is_system ON roles(is_system);

-- 注释
COMMENT ON TABLE roles IS '角色表';
COMMENT ON COLUMN roles.code IS '角色代码';
COMMENT ON COLUMN roles.is_system IS '是否系统角色';
COMMENT ON COLUMN roles.level IS '角色级别';
COMMENT ON COLUMN roles.permissions IS '权限代码数组';
```

---

## 相关表

请参考 [数据库设计总览](../INDEX.md) 查看所有相关表。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
