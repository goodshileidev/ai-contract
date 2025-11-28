# approval_logs - 审批日志表

**文档类型**: 设计文档 - 数据库表设计
**表编号**: 31
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从02-数据库设计.md拆分独立表文档 |

---

### 31. approval_logs (审批日志表)

```sql
CREATE TABLE approval_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    task_id UUID NOT NULL,
    document_id UUID NOT NULL,
    user_id UUID NOT NULL,
    action VARCHAR(50) NOT NULL CHECK (action IN ('submit', 'approve', 'reject', 'reassign', 'cancel', 'comment')),
    decision VARCHAR(20),
    comments TEXT,
    metadata JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES approval_tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (document_id) REFERENCES bid_documents(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 索引
CREATE INDEX idx_approval_logs_task_id ON approval_logs(task_id);
CREATE INDEX idx_approval_logs_document_id ON approval_logs(document_id);
CREATE INDEX idx_approval_logs_user_id ON approval_logs(user_id);
CREATE INDEX idx_approval_logs_created_at ON approval_logs(created_at DESC);

-- 注释
COMMENT ON TABLE approval_logs IS '审批日志表';
```

## 📊 审计与日志域

---

## 相关表

请参考 [数据库设计总览](../INDEX.md) 查看所有相关表。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
