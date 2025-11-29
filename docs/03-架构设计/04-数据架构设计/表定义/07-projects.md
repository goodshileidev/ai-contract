# projects - 项目表

**文档类型**: 设计文档 - 数据库表设计
**表编号**: 7
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从02-数据库设计.md拆分独立表文档 |

---

### 7. projects (项目表)

```sql
CREATE TABLE projects (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(200) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    organization_id UUID NOT NULL,
    bidding_type VARCHAR(50) CHECK (bidding_type IN ('government', 'enterprise', 'international', 'other')),
    industry VARCHAR(100),
    budget_amount DECIMAL(15, 2),
    currency VARCHAR(10) DEFAULT 'CNY',
    start_date DATE,
    end_date DATE,
    submission_deadline TIMESTAMP WITH TIME ZONE,
    status VARCHAR(20) DEFAULT 'draft' CHECK (status IN ('draft', 'in_progress', 'review', 'submitted', 'won', 'lost', 'archived')),
    priority VARCHAR(20) DEFAULT 'medium' CHECK (priority IN ('low', 'medium', 'high', 'urgent')),
    win_probability INTEGER CHECK (win_probability >= 0 AND win_probability <= 100),
    tags TEXT[] DEFAULT ARRAY[]::TEXT[],
    settings JSONB DEFAULT '{}'::jsonb,
    metadata JSONB DEFAULT '{}'::jsonb,
    created_by UUID NOT NULL,
    updated_by UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE,
    FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 索引
CREATE INDEX idx_projects_organization_id ON projects(organization_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_projects_status ON projects(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_projects_created_by ON projects(created_by);
CREATE INDEX idx_projects_submission_deadline ON projects(submission_deadline) WHERE deleted_at IS NULL;
CREATE INDEX idx_projects_tags ON projects USING gin(tags);
CREATE INDEX idx_projects_created_at ON projects(created_at DESC);

-- 注释
COMMENT ON TABLE projects IS '项目表';
COMMENT ON COLUMN projects.bidding_type IS '招标类型';
COMMENT ON COLUMN projects.status IS '项目状态';
COMMENT ON COLUMN projects.priority IS '优先级';
COMMENT ON COLUMN projects.win_probability IS '中标概率(0-100)';
```

---

## 相关表

请参考 [数据库设计总览](../INDEX.md) 查看所有相关表。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
