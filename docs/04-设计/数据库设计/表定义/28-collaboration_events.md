# collaboration_events - 协作事件表

**文档类型**: 设计文档 - 数据库表设计
**表编号**: 28
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从02-数据库设计.md拆分独立表文档 |

---

### 28. collaboration_events (协作事件表)

```sql
CREATE TABLE collaboration_events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    session_id UUID NOT NULL,
    document_id UUID NOT NULL,
    user_id UUID NOT NULL,
    event_type VARCHAR(50) NOT NULL CHECK (event_type IN ('join', 'leave', 'edit', 'comment', 'cursor_move', 'selection', 'other')),
    event_data JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES collaboration_sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (document_id) REFERENCES bid_documents(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 索引
CREATE INDEX idx_collaboration_events_session_id ON collaboration_events(session_id);
CREATE INDEX idx_collaboration_events_document_id ON collaboration_events(document_id);
CREATE INDEX idx_collaboration_events_user_id ON collaboration_events(user_id);
CREATE INDEX idx_collaboration_events_created_at ON collaboration_events(created_at DESC);

-- 转换为时序表
SELECT create_hypertable('collaboration_events', 'created_at', if_not_exists => TRUE);

-- 注释
COMMENT ON TABLE collaboration_events IS '协作事件表(时序数据)';
```

## ✅ 审批域

---

## 相关表

请参考 [数据库设计总览](../INDEX.md) 查看所有相关表。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
