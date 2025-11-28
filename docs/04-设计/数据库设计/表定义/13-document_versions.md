# document_versions - 文档版本表

**文档类型**: 设计文档 - 数据库表设计
**表编号**: 13
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从02-数据库设计.md拆分独立表文档 |

---

### 13. document_versions (文档版本表)

```sql
CREATE TABLE document_versions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    document_id UUID NOT NULL,
    version VARCHAR(20) NOT NULL,
    version_number INTEGER NOT NULL,
    title VARCHAR(200) NOT NULL,
    content JSONB NOT NULL,
    plain_content TEXT,
    change_summary TEXT,
    change_type VARCHAR(20) DEFAULT 'minor' CHECK (change_type IN ('major', 'minor', 'patch', 'draft')),
    word_count INTEGER DEFAULT 0,
    created_by UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (document_id) REFERENCES bid_documents(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT,
    UNIQUE (document_id, version_number)
);

-- 索引
CREATE INDEX idx_document_versions_document_id ON document_versions(document_id);
CREATE INDEX idx_document_versions_version_number ON document_versions(document_id, version_number DESC);
CREATE INDEX idx_document_versions_created_at ON document_versions(created_at DESC);

-- 注释
COMMENT ON TABLE document_versions IS '文档版本表';
COMMENT ON COLUMN document_versions.version_number IS '版本号(递增)';
COMMENT ON COLUMN document_versions.change_type IS '变更类型';
```

---

## 相关表

请参考 [数据库设计总览](../INDEX.md) 查看所有相关表。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
