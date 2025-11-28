# historical_bids - 历史标书表

**文档类型**: 设计文档 - 数据库表设计
**表编号**: 23
**创建日期**: 2025-11-27
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 设计中

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-27 | 1.0 | claude-sonnet-4-5 | 从02-数据库设计.md拆分独立表文档 |

---

### 23. historical_bids (历史标书表)

```sql
CREATE TABLE historical_bids (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID NOT NULL,
    project_name VARCHAR(200) NOT NULL,
    client_name VARCHAR(200),
    bidding_date DATE,
    submission_date DATE,
    result VARCHAR(20) CHECK (result IN ('won', 'lost', 'pending', 'withdrawn')),
    contract_amount DECIMAL(15, 2),
    bid_amount DECIMAL(15, 2),
    win_rate DECIMAL(5, 2),
    industry VARCHAR(100),
    category VARCHAR(100),
    key_points TEXT[] DEFAULT ARRAY[]::TEXT[],
    success_factors TEXT,
    lessons_learned TEXT,
    document_id UUID,
    is_reusable BOOLEAN DEFAULT TRUE,
    tags TEXT[] DEFAULT ARRAY[]::TEXT[],
    metadata JSONB DEFAULT '{}'::jsonb,
    created_by UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE,
    FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    FOREIGN KEY (document_id) REFERENCES bid_documents(id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 索引
CREATE INDEX idx_historical_bids_organization_id ON historical_bids(organization_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_historical_bids_result ON historical_bids(result) WHERE deleted_at IS NULL;
CREATE INDEX idx_historical_bids_industry ON historical_bids(industry) WHERE deleted_at IS NULL;
CREATE INDEX idx_historical_bids_bidding_date ON historical_bids(bidding_date DESC);
CREATE INDEX idx_historical_bids_tags ON historical_bids USING gin(tags);

-- 注释
COMMENT ON TABLE historical_bids IS '历史标书表';
COMMENT ON COLUMN historical_bids.win_rate IS '中标率(%)';
```

## 🤖 AI服务域

---

## 相关表

请参考 [数据库设计总览](../INDEX.md) 查看所有相关表。

---

**文档版本**: v1.0
**最后更新**: 2025-11-27
**维护者**: claude-sonnet-4-5
