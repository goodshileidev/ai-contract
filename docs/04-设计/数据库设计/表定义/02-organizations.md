# organizations - 组织表

**文档类型**: 设计文档 - 数据库表设计
**表编号**: 2
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从02-数据库设计.md拆分独立表文档 |

---

### 2. organizations (组织表)

```sql
CREATE TABLE organizations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(200) NOT NULL,
    short_name VARCHAR(100),
    organization_type VARCHAR(50) DEFAULT 'company' CHECK (organization_type IN ('company', 'government', 'institution', 'individual')),
    tax_id VARCHAR(50),
    legal_person VARCHAR(100),
    contact_phone VARCHAR(20),
    contact_email VARCHAR(255),
    address TEXT,
    province VARCHAR(50),
    city VARCHAR(50),
    district VARCHAR(50),
    logo_url TEXT,
    website TEXT,
    industry VARCHAR(100),
    scale VARCHAR(50),
    established_date DATE,
    status VARCHAR(20) DEFAULT 'active' CHECK (status IN ('active', 'inactive', 'suspended')),
    settings JSONB DEFAULT '{}'::jsonb,
    metadata JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

-- 索引
CREATE INDEX idx_organizations_name ON organizations USING gin(name gin_trgm_ops);
CREATE INDEX idx_organizations_tax_id ON organizations(tax_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_organizations_status ON organizations(status) WHERE deleted_at IS NULL;

-- 注释
COMMENT ON TABLE organizations IS '组织/企业表';
COMMENT ON COLUMN organizations.organization_type IS '组织类型: company-公司, government-政府, institution-事业单位, individual-个人';
COMMENT ON COLUMN organizations.scale IS '企业规模: small-小型, medium-中型, large-大型, xlarge-超大型';
```

---

## 相关表

请参考 [数据库设计总览](../INDEX.md) 查看所有相关表。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
