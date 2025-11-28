# templates - 模板表

**文档类型**: 设计文档 - 数据库表设计
**表编号**: 15
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从02-数据库设计.md拆分独立表文档 |

---

### 15. templates (模板表)

```sql
CREATE TABLE templates (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(200) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    category VARCHAR(100),
    industry VARCHAR(100),
    template_type VARCHAR(50) DEFAULT 'standard' CHECK (template_type IN ('standard', 'industry', 'custom')),
    scope VARCHAR(20) DEFAULT 'public' CHECK (scope IN ('public', 'organization', 'private')),
    organization_id UUID,
    version VARCHAR(20) DEFAULT '1.0',
    language VARCHAR(10) DEFAULT 'zh-CN',
    content JSONB NOT NULL,
    structure JSONB,
    variables JSONB DEFAULT '[]'::jsonb,
    placeholders JSONB DEFAULT '[]'::jsonb,
    tags TEXT[] DEFAULT ARRAY[]::TEXT[],
    is_active BOOLEAN DEFAULT TRUE,
    usage_count INTEGER DEFAULT 0,
    rating DECIMAL(3, 2) DEFAULT 0,
    rating_count INTEGER DEFAULT 0,
    thumbnail_url TEXT,
    preview_url TEXT,
    metadata JSONB DEFAULT '{}'::jsonb,
    created_by UUID NOT NULL,
    updated_by UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE,
    FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 索引
CREATE INDEX idx_templates_code ON templates(code) WHERE deleted_at IS NULL;
CREATE INDEX idx_templates_category ON templates(category) WHERE deleted_at IS NULL;
CREATE INDEX idx_templates_industry ON templates(industry) WHERE deleted_at IS NULL;
CREATE INDEX idx_templates_scope ON templates(scope) WHERE deleted_at IS NULL;
CREATE INDEX idx_templates_organization_id ON templates(organization_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_templates_tags ON templates USING gin(tags);
CREATE INDEX idx_templates_rating ON templates(rating DESC) WHERE is_active = TRUE;
CREATE INDEX idx_templates_usage_count ON templates(usage_count DESC);

-- 注释
COMMENT ON TABLE templates IS '模板表';
COMMENT ON COLUMN templates.scope IS '作用域: public-公开, organization-组织, private-私有';
COMMENT ON COLUMN templates.variables IS '模板变量定义(JSON)';
COMMENT ON COLUMN templates.placeholders IS '占位符定义(JSON)';
```

---

## 相关表

请参考 [数据库设计总览](../INDEX.md) 查看所有相关表。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
