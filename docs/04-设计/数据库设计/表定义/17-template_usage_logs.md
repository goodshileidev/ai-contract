# template_usage_logs - 模板使用日志表

**文档类型**: 设计文档 - 数据库表设计
**表编号**: 17
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从02-数据库设计.md拆分独立表文档 |

---

### 17. template_usage_logs (模板使用日志表)

```sql
CREATE TABLE template_usage_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    template_id UUID NOT NULL,
    document_id UUID,
    project_id UUID,
    user_id UUID NOT NULL,
    organization_id UUID,
    used_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (template_id) REFERENCES templates(id) ON DELETE CASCADE,
    FOREIGN KEY (document_id) REFERENCES bid_documents(id) ON DELETE SET NULL,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE
);

-- 索引
CREATE INDEX idx_template_usage_logs_template_id ON template_usage_logs(template_id);
CREATE INDEX idx_template_usage_logs_user_id ON template_usage_logs(user_id);
CREATE INDEX idx_template_usage_logs_used_at ON template_usage_logs(used_at DESC);

-- 注释
COMMENT ON TABLE template_usage_logs IS '模板使用日志表';
```

## 🏢 企业能力域

---

## 相关表

请参考 [数据库设计总览](../INDEX.md) 查看所有相关表。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
