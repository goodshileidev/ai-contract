# project_cases - 项目案例表

**文档类型**: 设计文档 - 数据库表设计
**表编号**: 20
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从02-数据库设计.md拆分独立表文档 |

---

### 20. project_cases (项目案例表)

```sql
CREATE TABLE project_cases (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID NOT NULL,
    project_name VARCHAR(200) NOT NULL,
    client_name VARCHAR(200),
    client_industry VARCHAR(100),
    project_category VARCHAR(100),
    project_type VARCHAR(50),
    contract_amount DECIMAL(15, 2),
    start_date DATE,
    end_date DATE,
    duration_months INTEGER,
    project_description TEXT,
    challenges TEXT,
    solutions TEXT,
    achievements TEXT[] DEFAULT ARRAY[]::TEXT[],
    technologies_used TEXT[] DEFAULT ARRAY[]::TEXT[],
    team_size INTEGER,
    project_role VARCHAR(100),
    customer_satisfaction DECIMAL(3, 2),
    is_reference BOOLEAN DEFAULT TRUE,
    is_public BOOLEAN DEFAULT FALSE,
    images TEXT[] DEFAULT ARRAY[]::TEXT[],
    documents TEXT[] DEFAULT ARRAY[]::TEXT[],
    tags TEXT[] DEFAULT ARRAY[]::TEXT[],
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
CREATE INDEX idx_project_cases_organization_id ON project_cases(organization_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_project_cases_client_industry ON project_cases(client_industry) WHERE deleted_at IS NULL;
CREATE INDEX idx_project_cases_project_category ON project_cases(project_category) WHERE deleted_at IS NULL;
CREATE INDEX idx_project_cases_tags ON project_cases USING gin(tags);
CREATE INDEX idx_project_cases_technologies_used ON project_cases USING gin(technologies_used);
CREATE INDEX idx_project_cases_end_date ON project_cases(end_date DESC);

-- 注释
COMMENT ON TABLE project_cases IS '项目案例表';
COMMENT ON COLUMN project_cases.is_reference IS '是否可作为参考案例';
COMMENT ON COLUMN project_cases.customer_satisfaction IS '客户满意度(0-5)';
```

---

## 相关表

请参考 [数据库设计总览](../INDEX.md) 查看所有相关表。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
