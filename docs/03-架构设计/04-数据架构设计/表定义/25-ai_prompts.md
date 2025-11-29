# ai_prompts - Prompt模板表

**文档类型**: 设计文档 - 数据库表设计
**表编号**: 25
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从02-数据库设计.md拆分独立表文档 |

---

### 25. ai_prompts (Prompt模板表)

```sql
CREATE TABLE ai_prompts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(200) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    category VARCHAR(100),
    description TEXT,
    prompt_template TEXT NOT NULL,
    system_prompt TEXT,
    variables JSONB DEFAULT '[]'::jsonb,
    model_name VARCHAR(100),
    model_params JSONB DEFAULT '{}'::jsonb,
    is_active BOOLEAN DEFAULT TRUE,
    version VARCHAR(20) DEFAULT '1.0',
    usage_count INTEGER DEFAULT 0,
    average_tokens INTEGER,
    average_cost DECIMAL(10, 4),
    tags TEXT[] DEFAULT ARRAY[]::TEXT[],
    metadata JSONB DEFAULT '{}'::jsonb,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 索引
CREATE INDEX idx_ai_prompts_code ON ai_prompts(code) WHERE deleted_at IS NULL;
CREATE INDEX idx_ai_prompts_category ON ai_prompts(category) WHERE deleted_at IS NULL;
CREATE INDEX idx_ai_prompts_is_active ON ai_prompts(is_active);
CREATE INDEX idx_ai_prompts_tags ON ai_prompts USING gin(tags);

-- 注释
COMMENT ON TABLE ai_prompts IS 'AI Prompt模板表';
COMMENT ON COLUMN ai_prompts.variables IS '变量列表(JSON)';
COMMENT ON COLUMN ai_prompts.model_params IS '模型参数(temperature, max_tokens等)';
```

---

## 相关表

请参考 [数据库设计总览](../INDEX.md) 查看所有相关表。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
