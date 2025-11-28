# document_comments - 文档评论表

**文档类型**: 设计文档 - 数据库表设计
**表编号**: 14
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从02-数据库设计.md拆分独立表文档 |

---

### 14. document_comments (文档评论表)

```sql
CREATE TABLE document_comments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    document_id UUID NOT NULL,
    section_id UUID,
    parent_id UUID,
    content TEXT NOT NULL,
    comment_type VARCHAR(20) DEFAULT 'comment' CHECK (comment_type IN ('comment', 'suggestion', 'issue', 'approval')),
    status VARCHAR(20) DEFAULT 'open' CHECK (status IN ('open', 'resolved', 'archived')),
    selection_start INTEGER,
    selection_end INTEGER,
    position_data JSONB,
    is_resolved BOOLEAN DEFAULT FALSE,
    resolved_by UUID,
    resolved_at TIMESTAMP WITH TIME ZONE,
    created_by UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE,
    FOREIGN KEY (document_id) REFERENCES bid_documents(id) ON DELETE CASCADE,
    FOREIGN KEY (section_id) REFERENCES document_sections(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES document_comments(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (resolved_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 索引
CREATE INDEX idx_document_comments_document_id ON document_comments(document_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_document_comments_section_id ON document_comments(section_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_document_comments_parent_id ON document_comments(parent_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_document_comments_status ON document_comments(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_document_comments_created_at ON document_comments(created_at DESC);

-- 注释
COMMENT ON TABLE document_comments IS '文档评论表';
COMMENT ON COLUMN document_comments.comment_type IS '评论类型';
COMMENT ON COLUMN document_comments.position_data IS '位置数据(JSON)';
```

## 📋 模板域

---

## 相关表

请参考 [数据库设计总览](../INDEX.md) 查看所有相关表。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
