# Python FastAPI AI 服务任务计划

**文档类型**: 实现文档
**需求编号**: REQ-AI-001 ~ REQ-AI-004
**创建日期**: 2025-11-26
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**最后更新**: 2025-11-26
**更新者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**状态**: 待开始

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-26 | 1.0 | claude-sonnet-4-5 (claude-sonnet-4-5-20250929) | 从task-plan.md拆分出Python AI服务任务 |

---

## 模块概述

本模块包含 Python FastAPI AI 服务的所有 AI 能力开发任务，负责：
- 招标文件智能解析
- 智能内容生成
- 企业能力库向量化
- 智能匹配分析

**技术栈**: Python 3.11 + FastAPI + LlamaIndex (主力) + LangChain (备用) + Elasticsearch

**总体进度**: 0% (0/4 任务完成)

---

## AI-001: 招标文件智能解析模块

**需求编号**: REQ-AI-001
**负责人**: Python AI 开发
**优先级**: P1 - 高优先级
**开始时间**: YYYY-MM-DD
**预计完成**: YYYY-MM-DD
**实际完成**: -
**当前状态**: ⏸️ 待开始
**完成进度**: 0% (0/6 子任务)

### 子任务清单

#### 1. 文档解析引擎
**完成进度**: 0/4
- [ ] PDF 文档解析（PyPDF2/PDFPlumber）
- [ ] Word 文档解析（python-docx）
- [ ] Excel 表格解析（openpyxl）
- [ ] 文档结构化提取

#### 2. 关键信息提取
**完成进度**: 0/6
- [ ] 使用 LlamaIndex 构建提取 Pipeline
- [ ] GPT-4 调用（OpenAI SDK）
- [ ] 项目基本信息提取
- [ ] 技术要求提取
- [ ] 商务条款提取
- [ ] 评分标准提取

#### 3. 需求分析引擎
**完成进度**: 0/4
- [ ] 需求分类（强制/可选/优先）
- [ ] 需求优先级分析
- [ ] 需求复杂度评估
- [ ] 需求依赖关系识别

#### 4. 风险评估系统
**完成进度**: 0/4
- [ ] 技术风险识别
- [ ] 商务风险识别
- [ ] 合规风险识别
- [ ] 风险评分和建议

#### 5. 向量化和检索
**完成进度**: 0/4
- [ ] 文档内容向量化（OpenAI Embeddings）
- [ ] Elasticsearch 向量存储
- [ ] 语义搜索功能
- [ ] 相似文档推荐

#### 6. API 接口实现
**完成进度**: 0/4
- [ ] POST /api/ai/parse-document
- [ ] POST /api/ai/extract-requirements
- [ ] POST /api/ai/analyze-risks
- [ ] GET /api/ai/similar-documents

### 技术实现要点

**文档解析**:
```python
from fastapi import FastAPI, UploadFile
from llama_index import VectorStoreIndex, ServiceContext
from llama_index.vector_stores import ElasticsearchStore
from llama_index.embeddings import OpenAIEmbedding
import openai

app = FastAPI()

@app.post("/api/ai/parse-document")
async def parse_document(file: UploadFile):
    # 1. 文档解析
    # 2. 内容提取
    # 3. 结构化输出
    pass

@app.post("/api/ai/extract-requirements")
async def extract_requirements(document_id: str):
    # 使用 LlamaIndex + GPT-4 提取需求
    pass
```

**LlamaIndex Pipeline**:
- 使用 LlamaIndex 作为主力 RAG 框架（80%任务）
- OpenAI Embeddings 向量化
- Elasticsearch 作为向量存储
- GPT-4 进行智能提取和分析

### API 接口设计

```
POST   /api/ai/parse-document          # 解析招标文件
POST   /api/ai/extract-requirements    # 提取需求
POST   /api/ai/analyze-risks           # 风险分析
GET    /api/ai/similar-documents       # 相似文档
```

### 相关文档

- AI设计文档: `docs/03-架构/05-AI能力层设计.md`
- API文档: `docs/03-架构/03-API接口设计.md`

---

## AI-002: 智能内容生成引擎

**需求编号**: REQ-AI-002
**优先级**: P1 - 高优先级
**当前状态**: ⏸️ 待开始
**完成进度**: 0% (0/5 子任务)

### 子任务清单

#### 1. RAG 系统构建
**完成进度**: 0/4
- [ ] LlamaIndex 索引构建
- [ ] 企业知识库向量化
- [ ] 项目案例向量化
- [ ] 检索增强生成 Pipeline

#### 2. 智能生成引擎
**完成进度**: 0/4
- [ ] 技术方案生成
- [ ] 项目实施方案生成
- [ ] 团队介绍生成
- [ ] 案例引用生成

#### 3. AI助手矩阵
**完成进度**: 0/4
- [ ] 技术专家助手
- [ ] 商务专家助手
- [ ] 合规专家助手
- [ ] 质量审查助手

#### 4. 内容优化
**完成进度**: 0/4
- [ ] 内容润色
- [ ] 专业术语检查
- [ ] 格式统一
- [ ] 内容去重

#### 5. API 接口实现
**完成进度**: 0/4
- [ ] POST /api/ai/generate-content
- [ ] POST /api/ai/optimize-content
- [ ] POST /api/ai/expert-review
- [ ] POST /api/ai/suggest-improvements

### 技术实现要点

**RAG 系统设计**:
```python
from llama_index import VectorStoreIndex, SimpleDirectoryReader
from llama_index.vector_stores import ElasticsearchStore
from llama_index.llms import OpenAI

# 1. 构建索引
documents = SimpleDirectoryReader("knowledge_base/").load_data()
vector_store = ElasticsearchStore(...)
index = VectorStoreIndex.from_documents(
    documents,
    vector_store=vector_store
)

# 2. RAG 查询
query_engine = index.as_query_engine(
    llm=OpenAI(model="gpt-4"),
    similarity_top_k=10
)
response = query_engine.query("技术方案如何生成？")
```

**AI 助手矩阵**:
- 每个助手使用不同的 System Prompt
- 技术专家助手：专注技术方案
- 商务专家助手：专注商务条款
- 合规专家助手：检查合规性
- 质量审查助手：内容质量评估

### API 接口设计

```
POST   /api/ai/generate-content        # 生成内容
POST   /api/ai/optimize-content        # 优化内容
POST   /api/ai/expert-review           # 专家审查
POST   /api/ai/suggest-improvements    # 改进建议
```

---

## AI-003: 企业能力库向量化

**需求编号**: REQ-AI-003
**优先级**: P2 - 中优先级
**当前状态**: ⏸️ 待开始
**完成进度**: 0% (0/4 子任务)

### 子任务清单

#### 1. 产品服务向量化
**完成进度**: 0/3
- [ ] 产品信息向量化
- [ ] 服务描述向量化
- [ ] 技术能力向量化

#### 2. 项目经验向量化
**完成进度**: 0/3
- [ ] 历史项目案例向量化
- [ ] 项目成果向量化
- [ ] 客户评价向量化

#### 3. 资质证书向量化
**完成进度**: 0/3
- [ ] 企业资质向量化
- [ ] 人员证书向量化
- [ ] 专利技术向量化

#### 4. 向量检索优化
**完成进度**: 0/3
- [ ] Elasticsearch 索引优化
- [ ] 混合检索（向量+关键词）
- [ ] 检索结果重排序

### 技术实现要点

**Elasticsearch 向量存储**:
```python
from llama_index.vector_stores import ElasticsearchStore

vector_store = ElasticsearchStore(
    es_url="http://elasticsearch:9200",
    index_name="capabilities",
    embedding_dimension=1536  # OpenAI embeddings
)
```

**混合检索**:
- 向量检索：语义相似度
- 关键词检索：精确匹配
- 结果融合：RRF (Reciprocal Rank Fusion)

---

## AI-004: 智能匹配分析

**需求编号**: REQ-AI-004
**优先级**: P2 - 中优先级
**当前状态**: ⏸️ 待开始
**完成进度**: 0% (0/3 子任务)

### 子任务清单

#### 1. 需求匹配分析
**完成进度**: 0/3
- [ ] 招标需求与企业能力匹配
- [ ] 匹配度评分算法
- [ ] 匹配可视化

#### 2. 竞争优势分析
**完成进度**: 0/3
- [ ] 优势识别
- [ ] 差距分析
- [ ] 改进建议

#### 3. 智能推荐
**完成进度**: 0/3
- [ ] 相似项目推荐
- [ ] 案例推荐
- [ ] 团队推荐

### 技术实现要点

**匹配算法**:
```python
def calculate_match_score(requirement, capability):
    # 1. 向量相似度（60%权重）
    vector_score = cosine_similarity(
        requirement.embedding,
        capability.embedding
    )

    # 2. 关键词匹配（40%权重）
    keyword_score = keyword_match(
        requirement.keywords,
        capability.keywords
    )

    # 3. 综合评分
    final_score = vector_score * 0.6 + keyword_score * 0.4

    return final_score
```

---

## 技术栈和工具

### 核心框架
- **Python**: 3.11+
- **FastAPI**: 0.104+
- **Uvicorn**: ASGI 服务器
- **Pydantic**: 数据验证

### AI/ML 框架
- **LlamaIndex**: 0.9+ (主力 RAG 框架)
- **LangChain**: 0.1+ (备用，复杂 Agent 场景)
- **OpenAI SDK**: 1.0+ (GPT-4 调用)
- **Anthropic SDK**: 0.7+ (Claude 3 调用)

### 向量和检索
- **Elasticsearch**: 8.11+ (向量存储+全文检索)
- **OpenAI Embeddings**: text-embedding-ada-002
- **HuggingFace Transformers**: Sentence-BERT (备用)

### 文档处理
- **PyPDF2 / PDFPlumber**: PDF 解析
- **python-docx**: Word 文档解析
- **openpyxl**: Excel 解析

### 异步任务
- **Celery**: 异步任务队列
- **RabbitMQ**: 消息队列
- **Redis**: Celery 后端

### 测试
- **pytest**: 单元测试
- **pytest-asyncio**: 异步测试
- **httpx**: HTTP 客户端测试

---

## 开发规范

### 代码规范
- 使用 Black 格式化代码
- 使用 Flake8 检查代码质量
- 使用 mypy 进行类型检查
- 使用 isort 排序导入

### 命名规范
- **文件名**: 小写+下划线，如 `document_parser.py`
- **类名**: 大驼峰，如 `DocumentParser`
- **函数名**: 小写+下划线，如 `parse_document`
- **常量**: 全大写+下划线，如 `MAX_FILE_SIZE`

### 异步编程
- 所有 IO 操作使用 async/await
- 使用 asyncio 进行并发
- FastAPI 路由使用 async def

### 测试要求
- 单元测试覆盖率 > 80%
- 所有 API 端点需要测试
- AI 功能需要集成测试

---

## 里程碑

### M3: 核心AI功能完成（2026-02-15）
- [ ] AI-001: 招标文件解析完成
- [ ] AI-002: 智能内容生成完成
- [ ] 所有 AI API 接口通过测试
- [ ] AI 服务性能达标

### M4: MVP 发布（2026-02-28）
- [ ] AI-003: 企业能力库向量化完成
- [ ] AI-004: 智能匹配分析完成
- [ ] 端到端 AI 流程测试通过
- [ ] AI 服务部署到生产

---

## 常用命令

### Python 环境
```bash
# 创建虚拟环境
python -m venv venv
source venv/bin/activate  # Linux/Mac
venv\Scripts\activate     # Windows

# 安装依赖
pip install -r requirements.txt

# 启动服务
uvicorn app.main:app --reload --port 8001
```

### 测试
```bash
# 运行所有测试
pytest

# 运行特定测试
pytest tests/test_document_parser.py

# 查看覆盖率
pytest --cov=app --cov-report=html
```

### Celery
```bash
# 启动 Worker
celery -A app.tasks worker --loglevel=info

# 启动 Beat（定时任务）
celery -A app.tasks beat --loglevel=info

# 查看任务状态
celery -A app.tasks inspect active
```

### 代码质量
```bash
# 格式化代码
black app/

# 检查代码
flake8 app/

# 类型检查
mypy app/

# 排序导入
isort app/
```

---

**返回**: [任务计划总览](./task-plan.md)
