# bid_documents - 标书文档表

**文档类型**: 设计文档 - 数据库表设计
**表编号**: 11
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从02-数据库设计.md拆分独立表文档 |

---

### 11. bid_documents (标书文档表)

```sql
CREATE TABLE bid_documents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    project_id UUID NOT NULL,
    template_id UUID,
    title VARCHAR(200) NOT NULL,
    version VARCHAR(20) DEFAULT '1.0',
    document_type VARCHAR(50) DEFAULT 'main' CHECK (document_type IN ('main', 'technical', 'commercial', 'qualification', 'other')),
    status VARCHAR(20) DEFAULT 'draft' CHECK (status IN ('draft', 'editing', 'review', 'approved', 'submitted', 'archived')),
    content_type VARCHAR(20) DEFAULT 'structured' CHECK (content_type IN ('structured', 'freeform', 'mixed')),
    content JSONB,
    plain_content TEXT,
    toc JSONB,
    word_count INTEGER DEFAULT 0,
    page_count INTEGER DEFAULT 0,
    last_edited_by UUID,
    last_edited_at TIMESTAMP WITH TIME ZONE,
    locked_by UUID,
    locked_at TIMESTAMP WITH TIME ZONE,
    metadata JSONB DEFAULT '{}'::jsonb,
    created_by UUID NOT NULL,
    updated_by UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    FOREIGN KEY (template_id) REFERENCES templates(id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (last_edited_by) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (locked_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 索引
CREATE INDEX idx_bid_documents_project_id ON bid_documents(project_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_bid_documents_template_id ON bid_documents(template_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_bid_documents_status ON bid_documents(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_bid_documents_created_by ON bid_documents(created_by);
CREATE INDEX idx_bid_documents_locked_by ON bid_documents(locked_by) WHERE locked_by IS NOT NULL;
CREATE INDEX idx_bid_documents_updated_at ON bid_documents(updated_at DESC);

-- 全文搜索索引
CREATE INDEX idx_bid_documents_plain_content_fts ON bid_documents USING gin(to_tsvector('chinese', plain_content));

-- 注释
COMMENT ON TABLE bid_documents IS '标书文档表';
COMMENT ON COLUMN bid_documents.content IS '结构化内容(JSON)';
COMMENT ON COLUMN bid_documents.plain_content IS '纯文本内容(用于搜索)';
COMMENT ON COLUMN bid_documents.toc IS '目录结构(JSON)';
```

---

## 相关表

请参考 [数据库设计总览](../INDEX.md) 查看所有相关表。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
