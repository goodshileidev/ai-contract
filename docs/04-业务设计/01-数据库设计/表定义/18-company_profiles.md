# company_profiles - 企业档案表

**文档类型**: 设计文档 - 数据库表设计
**表编号**: 18
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从02-数据库设计.md拆分独立表文档 |

---

### 18. company_profiles (企业档案表)

```sql
CREATE TABLE company_profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID UNIQUE NOT NULL,
    brief_introduction TEXT,
    core_business TEXT,
    core_competencies TEXT[] DEFAULT ARRAY[]::TEXT[],
    competitive_advantages TEXT[] DEFAULT ARRAY[]::TEXT[],
    key_technologies TEXT[] DEFAULT ARRAY[]::TEXT[],
    major_clients TEXT[] DEFAULT ARRAY[]::TEXT[],
    annual_revenue DECIMAL(15, 2),
    employee_count INTEGER,
    rd_personnel_count INTEGER,
    patents_count INTEGER,
    certifications TEXT[] DEFAULT ARRAY[]::TEXT[],
    honors TEXT[] DEFAULT ARRAY[]::TEXT[],
    metadata JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE
);

-- 索引
CREATE INDEX idx_company_profiles_organization_id ON company_profiles(organization_id);
CREATE INDEX idx_company_profiles_core_competencies ON company_profiles USING gin(core_competencies);
CREATE INDEX idx_company_profiles_key_technologies ON company_profiles USING gin(key_technologies);

-- 注释
COMMENT ON TABLE company_profiles IS '企业档案表';
```

---

## 相关表

请参考 [数据库设计总览](../INDEX.md) 查看所有相关表。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
