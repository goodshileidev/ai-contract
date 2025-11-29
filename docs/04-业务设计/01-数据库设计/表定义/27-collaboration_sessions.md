# collaboration_sessions - 协作会话表

**文档类型**: 设计文档 - 数据库表设计
**表编号**: 27
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从02-数据库设计.md拆分独立表文档 |

---

### 27. collaboration_sessions (协作会话表)

```sql
CREATE TABLE collaboration_sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    document_id UUID NOT NULL,
    session_key VARCHAR(100) UNIQUE NOT NULL,
    active_users JSONB DEFAULT '[]'::jsonb,
    cursor_positions JSONB DEFAULT '{}'::jsonb,
    selections JSONB DEFAULT '{}'::jsonb,
    awareness_state JSONB DEFAULT '{}'::jsonb,
    is_active BOOLEAN DEFAULT TRUE,
    started_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP WITH TIME ZONE,
    last_activity_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (document_id) REFERENCES bid_documents(id) ON DELETE CASCADE
);

-- 索引
CREATE INDEX idx_collaboration_sessions_document_id ON collaboration_sessions(document_id);
CREATE INDEX idx_collaboration_sessions_is_active ON collaboration_sessions(is_active);
CREATE INDEX idx_collaboration_sessions_last_activity_at ON collaboration_sessions(last_activity_at DESC);

-- 注释
COMMENT ON TABLE collaboration_sessions IS '协作会话表';
COMMENT ON COLUMN collaboration_sessions.awareness_state IS '用户感知状态(Yjs Awareness)';
```

---

## 相关表

请参考 [数据库设计总览](../INDEX.md) 查看所有相关表。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
