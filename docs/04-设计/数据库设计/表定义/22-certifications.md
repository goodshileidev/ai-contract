# certifications - 资质证书表

**文档类型**: 设计文档 - 数据库表设计
**表编号**: 22
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从02-数据库设计.md拆分独立表文档 |

---

### 22. certifications (资质证书表)

```sql
CREATE TABLE certifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID NOT NULL,
    certification_name VARCHAR(200) NOT NULL,
    certification_type VARCHAR(100),
    issuing_authority VARCHAR(200),
    certificate_number VARCHAR(100),
    issue_date DATE,
    expiry_date DATE,
    is_valid BOOLEAN DEFAULT TRUE,
    scope TEXT,
    level VARCHAR(50),
    certificate_url TEXT,
    metadata JSONB DEFAULT '{}'::jsonb,
    created_by UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE,
    FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 索引
CREATE INDEX idx_certifications_organization_id ON certifications(organization_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_certifications_type ON certifications(certification_type) WHERE deleted_at IS NULL;
CREATE INDEX idx_certifications_expiry_date ON certifications(expiry_date) WHERE deleted_at IS NULL;

-- 注释
COMMENT ON TABLE certifications IS '资质证书表';
```

---

## 相关表

请参考 [数据库设计总览](../INDEX.md) 查看所有相关表。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
