# ai_tasks - AI任务表

**文档类型**: 设计文档 - 数据库表设计
**表编号**: 24
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从02-数据库设计.md拆分独立表文档 |

---

### 24. ai_tasks (AI任务表)

```sql
CREATE TABLE ai_tasks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    task_type VARCHAR(50) NOT NULL CHECK (task_type IN ('parse', 'analyze', 'match', 'generate', 'review', 'other')),
    task_name VARCHAR(200) NOT NULL,
    description TEXT,
    project_id UUID,
    document_id UUID,
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'running', 'success', 'failed', 'cancelled')),
    priority INTEGER DEFAULT 0,
    input_data JSONB,
    output_data JSONB,
    error_message TEXT,
    model_name VARCHAR(100),
    model_version VARCHAR(50),
    prompt_template TEXT,
    prompt_tokens INTEGER,
    completion_tokens INTEGER,
    total_tokens INTEGER,
    cost DECIMAL(10, 4),
    started_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    duration_seconds INTEGER,
    retry_count INTEGER DEFAULT 0,
    max_retries INTEGER DEFAULT 3,
    metadata JSONB DEFAULT '{}'::jsonb,
    created_by UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    FOREIGN KEY (document_id) REFERENCES bid_documents(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT
);

-- 索引
CREATE INDEX idx_ai_tasks_task_type ON ai_tasks(task_type);
CREATE INDEX idx_ai_tasks_status ON ai_tasks(status);
CREATE INDEX idx_ai_tasks_project_id ON ai_tasks(project_id);
CREATE INDEX idx_ai_tasks_document_id ON ai_tasks(document_id);
CREATE INDEX idx_ai_tasks_created_by ON ai_tasks(created_by);
CREATE INDEX idx_ai_tasks_created_at ON ai_tasks(created_at DESC);

-- 注释
COMMENT ON TABLE ai_tasks IS 'AI任务表';
COMMENT ON COLUMN ai_tasks.task_type IS '任务类型: parse-解析, analyze-分析, match-匹配, generate-生成, review-审核';
```

---

## 相关表

请参考 [数据库设计总览](../INDEX.md) 查看所有相关表。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
