# document_sections - 文档章节表

**文档类型**: 设计文档 - 数据库表设计
**表编号**: 12
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从02-数据库设计.md拆分独立表文档 |

---

### 12. document_sections (文档章节表)

```sql
CREATE TABLE document_sections (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    document_id UUID NOT NULL,
    parent_id UUID,
    title VARCHAR(200) NOT NULL,
    section_number VARCHAR(50),
    level INTEGER DEFAULT 1,
    content TEXT,
    content_type VARCHAR(20) DEFAULT 'text' CHECK (content_type IN ('text', 'table', 'image', 'chart', 'list', 'mixed')),
    order_index INTEGER NOT NULL,
    word_count INTEGER DEFAULT 0,
    is_required BOOLEAN DEFAULT FALSE,
    is_generated BOOLEAN DEFAULT FALSE,
    generated_by VARCHAR(50),
    generation_prompt TEXT,
    metadata JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (document_id) REFERENCES bid_documents(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES document_sections(id) ON DELETE CASCADE
);

-- 索引
CREATE INDEX idx_document_sections_document_id ON document_sections(document_id);
CREATE INDEX idx_document_sections_parent_id ON document_sections(parent_id);
CREATE INDEX idx_document_sections_order_index ON document_sections(document_id, order_index);

-- 注释
COMMENT ON TABLE document_sections IS '文档章节表';
COMMENT ON COLUMN document_sections.level IS '章节层级';
COMMENT ON COLUMN document_sections.is_generated IS '是否AI生成';
```

---

## 相关表

请参考 [数据库设计总览](../INDEX.md) 查看所有相关表。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
