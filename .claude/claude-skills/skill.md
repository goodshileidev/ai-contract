# 文档到JSON抽取 Skill

**Skill名称**: doc-to-json-extractor
**版本**: 1.0.0
**用途**: 从设计文档中抽取结构化业务定义，生成符合JSON Schema的定义文件
**适用场景**: 任何需要将业务设计文档转换为结构化JSON定义的项目

---

## 📋 Skill功能

这个Skill可以从Markdown设计文档中抽取以下类型的业务定义：

1. **API接口定义** (internal_api)
2. **业务SQL定义** (program_sql)
3. **业务流程定义** (biz_flow)
4. **报表定义** (report)
5. **数据实体定义** (entity)
6. **页面定义** (page)

## 🎯 使用方法

### 基本调用

```
使用 doc-to-json-extractor skill，从 {文档路径} 抽取 {定义类型}
```

### 示例

```
使用 doc-to-json-extractor skill，从 docs/04-设计/API接口设计/ 抽取 program_sql 定义
```

## 📖 工作流程

当调用这个skill时，AI会执行以下步骤：

### 1. 理解任务参数

从用户输入中提取：
- **源文档目录**: 要读取的设计文档位置
- **目标类型**: 要生成的JSON类型 (program_sql / biz_flow / report 等)
- **Schema路径**: JSON Schema定义文件位置（可选，默认查找项目配置）
- **输出目录**: JSON文件保存位置（可选，使用默认路径）

### 2. 读取和分析文档

```markdown
步骤A: 扫描目录，列出所有Markdown文档
步骤B: 逐个读取文档内容
步骤C: 识别文档中的业务定义元素：
  - API端点定义
  - 数据查询需求
  - 业务流程描述
  - 统计报表需求
  - 数据表结构
```

### 3. 抽取结构化信息

根据目标类型，从文档中抽取关键信息：

#### 抽取 program_sql

从文档中识别：
- 复杂查询场景（多表关联、聚合统计、分组查询）
- 业务需求描述
- 输入参数
- 涉及的数据表
- 输出说明

#### 抽取 biz_flow

从文档中识别：
- Mermaid流程图
- sequenceDiagram时序图
- 流程步骤描述
- 业务场景说明

#### 抽取 report

从文档中识别：
- 报表需求描述
- 数据源表
- 统计维度
- 筛选条件

#### 抽取 internal_api

从文档中识别：
- API路径和方法
- 请求参数
- 响应结构
- 错误码定义

### 4. 生成JSON文件

```markdown
步骤A: 加载目标Schema定义
步骤B: 将抽取的信息映射到Schema字段
步骤C: 生成JSON文件并保存
步骤D: 添加数据溯源信息（source_file字段）
```

### 5. 验证和报告

```markdown
步骤A: 使用jsonschema验证生成的JSON
步骤B: 生成抽取报告：
  - 读取了哪些文档
  - 生成了多少个定义
  - 验证结果
  - 数据来源清单
```

## 📁 项目配置文件

在项目根目录创建 `.claude/skills/doc-to-json-extractor/config.yaml`：

```yaml
# 文档抽取配置
extractor:
  # 源文档目录
  source_docs_dir: "docs/04-设计"

  # Schema定义目录（可选，优先级高于默认路径）
  schema_dir: null  # 如: "/path/to/schemas"

  # 输出目录
  output_dir: "structured-requirements/individual-jsons"

  # 模块ID映射
  module_map:
    "认证授权": 1
    "项目管理": 2
    "文档管理": 3
    "模板管理": 4
    "企业能力": 5
    "AI服务": 6
    "协作": 7
    "审批": 8
    "系统管理": 9
    "报表统计": 10
    "搜索": 11

# JSON类型配置
json_types:
  program_sql:
    schema: "program_sql-schema.json"
    output_subdir: "16-业务SQL"
    description: "业务SQL定义"

  biz_flow:
    schema: "biz_flow-schema.json"
    output_subdir: "17-业务流程"
    description: "业务流程定义"

  report:
    schema: "report-schema.json"
    output_subdir: "18-报表定义"
    description: "报表定义"

  internal_api:
    schema: "internal_api-schema.json"
    output_subdir: "11-内部API"
    description: "内部API定义"
```

## 🔧 高级选项

### 自定义抽取规则

可以通过配置文件定义特定的抽取规则：

```yaml
extraction_rules:
  program_sql:
    # 从哪些章节抽取
    target_sections:
      - "数据查询"
      - "统计分析"
      - "报表数据源"

    # 识别关键词
    keywords:
      - "查询"
      - "统计"
      - "聚合"
      - "JOIN"
      - "GROUP BY"

    # 必需字段
    required_fields:
      - "business_requirement"
      - "input_tables"
```

## 📝 输出示例

### program_sql输出

```json
{
  "program_sql_id": 1,
  "program_sql_name": "项目状态统计",
  "program_sql_no": "SQL-001",
  "source_type": "business_requirement",
  "sql_text": "-- 业务需求: 统计各状态项目数量\n-- 数据来源: docs/04-设计/API接口设计/02-项目管理API.md",
  "sql_param_list": [...],
  "input_table_list": [...],
  "_metadata": {
    "extracted_from": "docs/04-设计/API接口设计/02-项目管理API.md",
    "extraction_date": "2025-11-28",
    "extractor_version": "1.0.0"
  }
}
```

## ✅ 最佳实践

1. **数据溯源**: 每个生成的JSON都应包含 `source_file` 或 `_metadata` 字段，标注数据来源
2. **业务描述优先**: 只记录业务需求，不生成具体实现代码
3. **Schema验证**: 生成后立即验证JSON符合Schema
4. **增量抽取**: 支持只抽取新增或修改的文档
5. **人工审核**: 生成后需要人工review和调整

## 🔄 在其他项目中使用

### 步骤1: 复制Skill到新项目

```bash
cp -r .claude/skills/doc-to-json-extractor /path/to/new-project/.claude/skills/
```

### 步骤2: 修改配置文件

编辑 `config.yaml`，设置新项目的：
- 文档目录路径
- Schema目录路径
- 模块映射关系

### 步骤3: 调用Skill

```
使用 doc-to-json-extractor skill，从 docs/design/ 抽取 api 定义
```

## 📚 相关资源

- [JSON Schema 规范](https://json-schema.org/)
- [Markdown 解析库](https://python-markdown.github.io/)
- [YAML 配置文件语法](https://yaml.org/)

---

**Skill作者**: Claude Code
**创建日期**: 2025-11-28
**适用版本**: Claude Code 0.8+
**许可证**: MIT
