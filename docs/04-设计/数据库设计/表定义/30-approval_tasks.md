# approval_tasks - 审批任务表

**文档类型**: 设计文档 - 数据库表设计
**表编号**: 30
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从02-数据库设计.md拆分独立表文档 |

---

### 30. approval_tasks (审批任务表)

```sql
CREATE TABLE approval_tasks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    workflow_id UUID NOT NULL,
    document_id UUID NOT NULL,
    task_name VARCHAR(200) NOT NULL,
    step_number INTEGER NOT NULL,
    assignee_id UUID NOT NULL,
    assignee_type VARCHAR(20) DEFAULT 'user' CHECK (assignee_type IN ('user', 'role')),
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'approved', 'rejected', 'cancelled')),
    decision VARCHAR(20),
    comments TEXT,
    deadline TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    metadata JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (workflow_id) REFERENCES approval_workflows(id) ON DELETE CASCADE,
    FOREIGN KEY (document_id) REFERENCES bid_documents(id) ON DELETE CASCADE,
    FOREIGN KEY (assignee_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 索引
CREATE INDEX idx_approval_tasks_workflow_id ON approval_tasks(workflow_id);
CREATE INDEX idx_approval_tasks_document_id ON approval_tasks(document_id);
CREATE INDEX idx_approval_tasks_assignee_id ON approval_tasks(assignee_id);
CREATE INDEX idx_approval_tasks_status ON approval_tasks(status);
CREATE INDEX idx_approval_tasks_deadline ON approval_tasks(deadline) WHERE status = 'pending';

-- 注释
COMMENT ON TABLE approval_tasks IS '审批任务表';
```

---

## 相关表

请参考 [数据库设计总览](../INDEX.md) 查看所有相关表。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
