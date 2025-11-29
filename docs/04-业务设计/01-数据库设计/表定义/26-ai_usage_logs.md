# ai_usage_logs - AI使用日志表

**文档类型**: 设计文档 - 数据库表设计
**表编号**: 26
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从02-数据库设计.md拆分独立表文档 |

---

### 26. ai_usage_logs (AI使用日志表)

```sql
CREATE TABLE ai_usage_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    organization_id UUID,
    task_id UUID,
    model_name VARCHAR(100) NOT NULL,
    operation_type VARCHAR(50),
    prompt_tokens INTEGER,
    completion_tokens INTEGER,
    total_tokens INTEGER,
    cost DECIMAL(10, 4),
    latency_ms INTEGER,
    status VARCHAR(20),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    FOREIGN KEY (task_id) REFERENCES ai_tasks(id) ON DELETE SET NULL
);

-- 索引
CREATE INDEX idx_ai_usage_logs_user_id ON ai_usage_logs(user_id);
CREATE INDEX idx_ai_usage_logs_organization_id ON ai_usage_logs(organization_id);
CREATE INDEX idx_ai_usage_logs_created_at ON ai_usage_logs(created_at DESC);

-- 转换为时序表 (使用TimescaleDB)
SELECT create_hypertable('ai_usage_logs', 'created_at', if_not_exists => TRUE);

-- 注释
COMMENT ON TABLE ai_usage_logs IS 'AI使用日志表(时序数据)';
```

## 👥 协作域

---

## 相关表

请参考 [数据库设计总览](../INDEX.md) 查看所有相关表。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
