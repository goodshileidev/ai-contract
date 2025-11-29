# bidding_documents - 招标文件表

**文档类型**: 设计文档 - 数据库表设计
**表编号**: 9
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从02-数据库设计.md拆分独立表文档 |

---

### 9. bidding_documents (招标文件表)

```sql
CREATE TABLE bidding_documents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    project_id UUID NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path TEXT NOT NULL,
    file_size BIGINT NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    mime_type VARCHAR(100),
    storage_key VARCHAR(500) NOT NULL,
    parsed_status VARCHAR(20) DEFAULT 'pending' CHECK (parsed_status IN ('pending', 'processing', 'success', 'failed')),
    parsed_content JSONB,
    parsed_at TIMESTAMP WITH TIME ZONE,
    parse_error TEXT,
    document_hash VARCHAR(64),
    uploaded_by UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    FOREIGN KEY (uploaded_by) REFERENCES users(id) ON DELETE RESTRICT
);

-- 索引
CREATE INDEX idx_bidding_documents_project_id ON bidding_documents(project_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_bidding_documents_parsed_status ON bidding_documents(parsed_status);
CREATE INDEX idx_bidding_documents_document_hash ON bidding_documents(document_hash);
CREATE INDEX idx_bidding_documents_created_at ON bidding_documents(created_at DESC);

-- 注释
COMMENT ON TABLE bidding_documents IS '招标文件表';
COMMENT ON COLUMN bidding_documents.parsed_status IS '解析状态';
COMMENT ON COLUMN bidding_documents.parsed_content IS '解析后的内容(JSON)';
COMMENT ON COLUMN bidding_documents.document_hash IS '文件哈希(用于去重)';
```

---

## 相关表

请参考 [数据库设计总览](../INDEX.md) 查看所有相关表。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
