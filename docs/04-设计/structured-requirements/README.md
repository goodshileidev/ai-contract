# AIBidComposer 结构化需求定义

本目录包含AIBidComposer（AI标书智能创作平台）按照标准软件开发层级结构拆解的需求定义。

## 目录结构

```
structured-requirements/
├── 0-需求层/
│   ├── user-requirements.json      # 用户需求
│   ├── requirement-pieces.json     # 需求分片
│   └── function-points.json        # 功能点
├── 1-管理层/
│   ├── project.json                # 项目定义
│   ├── modules.json                # 模块定义
│   └── functions.json              # 功能定义
├── 2-功能层/
│   ├── pages.json                  # 页面定义
│   ├── processes.json              # 处理定义
│   └── reports.json                # 报表定义
├── 3-UI详细层/
│   ├── page-sections.json          # 页面区块
│   ├── page-fields.json            # 页面字段
│   ├── page-actions.json           # 页面动作
│   └── page-field-actions.json     # 字段动作
├── 4-处理详细层/
│   ├── data-transformations.json   # 数据变换处理
│   ├── business-sql.json           # 业务SQL
│   ├── internal-apis.json          # 内部接口
│   └── external-apis.json          # 外部接口
└── 5-报表详细层/
    └── report-metrics.json          # 报表指标
```

## 层级说明

### 0. 需求层
定义原始用户需求、需求拆分和功能点

### 1. 管理层
定义项目、模块、功能的组织结构

### 2. 功能层
定义具体的页面、处理逻辑和报表

### 3. UI详细层
定义页面的详细UI元素

### 4. 处理详细层
定义数据处理、接口等技术实现

### 5. 报表详细层
定义报表的具体指标

## 更新历史

| 日期 | 版本 | 修改者 | 修改内容 |
|------|------|--------|----------|
| 2025-11-27 | 1.0 | claude-opus-4-1 | 初始创建结构化需求目录 |