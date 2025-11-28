# products_services - 产品服务表

**文档类型**: 设计文档 - 数据库表设计
**表编号**: 19
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从02-数据库设计.md拆分独立表文档 |

---

### 19. products_services (产品服务表)

```sql
CREATE TABLE products_services (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID NOT NULL,
    name VARCHAR(200) NOT NULL,
    category VARCHAR(100),
    type VARCHAR(20) CHECK (type IN ('product', 'service', 'solution')),
    description TEXT,
    features TEXT[] DEFAULT ARRAY[]::TEXT[],
    specifications JSONB,
    advantages TEXT[] DEFAULT ARRAY[]::TEXT[],
    application_scenarios TEXT[] DEFAULT ARRAY[]::TEXT[],
    technology_stack TEXT[] DEFAULT ARRAY[]::TEXT[],
    price_range VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    images TEXT[] DEFAULT ARRAY[]::TEXT[],
    documents TEXT[] DEFAULT ARRAY[]::TEXT[],
    tags TEXT[] DEFAULT ARRAY[]::TEXT[],
    metadata JSONB DEFAULT '{}'::jsonb,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE,
    FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 索引
CREATE INDEX idx_products_services_organization_id ON products_services(organization_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_products_services_category ON products_services(category) WHERE deleted_at IS NULL;
CREATE INDEX idx_products_services_type ON products_services(type) WHERE deleted_at IS NULL;
CREATE INDEX idx_products_services_tags ON products_services USING gin(tags);

-- 注释
COMMENT ON TABLE products_services IS '产品服务表';
```

---

## 相关表

请参考 [数据库设计总览](../INDEX.md) 查看所有相关表。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
