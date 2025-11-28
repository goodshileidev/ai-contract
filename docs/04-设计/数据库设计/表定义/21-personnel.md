# personnel - 人员资质表

**文档类型**: 设计文档 - 数据库表设计
**表编号**: 21
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从02-数据库设计.md拆分独立表文档 |

---

### 21. personnel (人员资质表)

```sql
CREATE TABLE personnel (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    employee_id VARCHAR(50),
    position VARCHAR(100),
    department VARCHAR(100),
    education VARCHAR(50),
    major VARCHAR(100),
    years_of_experience INTEGER,
    specialties TEXT[] DEFAULT ARRAY[]::TEXT[],
    certifications TEXT[] DEFAULT ARRAY[]::TEXT[],
    project_experience_count INTEGER DEFAULT 0,
    skills JSONB DEFAULT '[]'::jsonb,
    is_available BOOLEAN DEFAULT TRUE,
    resume_url TEXT,
    metadata JSONB DEFAULT '{}'::jsonb,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE,
    FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 索引
CREATE INDEX idx_personnel_organization_id ON personnel(organization_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_personnel_position ON personnel(position) WHERE deleted_at IS NULL;
CREATE INDEX idx_personnel_specialties ON personnel USING gin(specialties);

-- 注释
COMMENT ON TABLE personnel IS '人员资质表';
COMMENT ON COLUMN personnel.skills IS '技能列表(JSON)';
```

---

## 相关表

请参考 [数据库设计总览](../INDEX.md) 查看所有相关表。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
