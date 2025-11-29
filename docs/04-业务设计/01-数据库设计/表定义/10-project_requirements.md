# project_requirements - 项目需求表

**文档类型**: 设计文档 - 数据库表设计
**表编号**: 10
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从02-数据库设计.md拆分独立表文档 |

---

### 10. project_requirements (项目需求表)

```sql
CREATE TABLE project_requirements (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    project_id UUID NOT NULL,
    requirement_type VARCHAR(50) CHECK (requirement_type IN ('technical', 'business', 'compliance', 'resource', 'other')),
    category VARCHAR(100),
    title VARCHAR(200) NOT NULL,
    description TEXT,
    priority VARCHAR(20) DEFAULT 'medium' CHECK (priority IN ('low', 'medium', 'high', 'critical')),
    is_mandatory BOOLEAN DEFAULT TRUE,
    score_weight DECIMAL(5, 2),
    match_status VARCHAR(20) DEFAULT 'pending' CHECK (match_status IN ('pending', 'matched', 'partial', 'unmatched')),
    match_score DECIMAL(5, 2),
    match_details JSONB,
    source VARCHAR(50),
    source_page INTEGER,
    extracted_by VARCHAR(20) DEFAULT 'ai' CHECK (extracted_by IN ('ai', 'manual')),
    metadata JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

-- 索引
CREATE INDEX idx_project_requirements_project_id ON project_requirements(project_id);
CREATE INDEX idx_project_requirements_type ON project_requirements(requirement_type);
CREATE INDEX idx_project_requirements_match_status ON project_requirements(match_status);

-- 注释
COMMENT ON TABLE project_requirements IS '项目需求表';
COMMENT ON COLUMN project_requirements.match_status IS '匹配状态';
COMMENT ON COLUMN project_requirements.match_score IS '匹配分数(0-100)';
```

## 📄 标书域

---

## 相关表

请参考 [数据库设计总览](../INDEX.md) 查看所有相关表。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
