# approval_workflows - 审批流程表

**文档类型**: 设计文档 - 数据库表设计
**表编号**: 29
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从02-数据库设计.md拆分独立表文档 |

---

### 29. approval_workflows (审批流程表)

```sql
CREATE TABLE approval_workflows (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(200) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    workflow_type VARCHAR(50) DEFAULT 'sequential' CHECK (workflow_type IN ('sequential', 'parallel', 'conditional', 'custom')),
    organization_id UUID,
    document_type VARCHAR(50),
    definition JSONB NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    is_default BOOLEAN DEFAULT FALSE,
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
CREATE INDEX idx_approval_workflows_code ON approval_workflows(code) WHERE deleted_at IS NULL;
CREATE INDEX idx_approval_workflows_organization_id ON approval_workflows(organization_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_approval_workflows_is_active ON approval_workflows(is_active);

-- 注释
COMMENT ON TABLE approval_workflows IS '审批流程表';
COMMENT ON COLUMN approval_workflows.definition IS '流程定义(JSON)';
```

---

## 相关表

请参考 [数据库设计总览](../INDEX.md) 查看所有相关表。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
