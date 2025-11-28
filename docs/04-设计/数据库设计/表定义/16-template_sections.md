# template_sections - 模板章节表

**文档类型**: 设计文档 - 数据库表设计
**表编号**: 16
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从02-数据库设计.md拆分独立表文档 |

---

### 16. template_sections (模板章节表)

```sql
CREATE TABLE template_sections (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    template_id UUID NOT NULL,
    parent_id UUID,
    title VARCHAR(200) NOT NULL,
    section_number VARCHAR(50),
    level INTEGER DEFAULT 1,
    content_template TEXT,
    content_type VARCHAR(20) DEFAULT 'text',
    order_index INTEGER NOT NULL,
    is_required BOOLEAN DEFAULT FALSE,
    is_editable BOOLEAN DEFAULT TRUE,
    variables TEXT[] DEFAULT ARRAY[]::TEXT[],
    hints TEXT,
    metadata JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (template_id) REFERENCES templates(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES template_sections(id) ON DELETE CASCADE
);

-- 索引
CREATE INDEX idx_template_sections_template_id ON template_sections(template_id);
CREATE INDEX idx_template_sections_parent_id ON template_sections(parent_id);
CREATE INDEX idx_template_sections_order_index ON template_sections(template_id, order_index);

-- 注释
COMMENT ON TABLE template_sections IS '模板章节表';
COMMENT ON COLUMN template_sections.content_template IS '内容模板(包含变量占位符)';
COMMENT ON COLUMN template_sections.hints IS '填写提示';
```

---

## 相关表

请参考 [数据库设计总览](../INDEX.md) 查看所有相关表。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
