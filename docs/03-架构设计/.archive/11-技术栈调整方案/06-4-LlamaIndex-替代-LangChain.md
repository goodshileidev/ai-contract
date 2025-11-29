---
文档类型: 架构文档
需求编号: DOC-2025-11-001
创建日期: 2025-11-15
创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
最后更新: 2025-11-26
更新者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
状态: 已批准
---

# AI标书智能创作平台 - 技术栈调整方案 - 4️⃣ LlamaIndex 替代 LangChain

### 4.1 可行性分析

**✅ LlamaIndex更适合RAG场景**

```yaml
LlamaIndex优势:
  专注RAG:
    - 数据连接器丰富
    - 索引构建简单
    - 查询优化强大
    - 与检索深度集成

  Elasticsearch集成:
    - 原生支持ES作为向量存储
    - 混合检索内置
    - 索引管理便捷

  vs LangChain:
    - 更专注于检索增强
    - 代码更简洁
    - 学习曲线平缓
    - 适合数据索引场景

局限性:
  - 工作流编排能力弱于LangGraph
  - Agent功能相对简单

解决方案:
  - 使用LlamaIndex做RAG
  - 简单工作流用Python函数编排
  - 复杂逻辑用状态机模式
```

### 4.2 LlamaIndex架构

**核心组件**
```python
# app/services/llama_service.py
from llama_index.core import VectorStoreIndex, SimpleDirectoryReader, Settings
from llama_index.vector_stores.elasticsearch import ElasticsearchStore
from llama_index.embeddings.openai import OpenAIEmbedding
from llama_index.llms.openai import OpenAI

class LlamaIndexService:
    def __init__(self):
        # 配置全局设置
        Settings.llm = OpenAI(
            model="gpt-4-turbo-preview",
            temperature=0.7,
            api_key=settings.OPENAI_API_KEY
        )

        Settings.embed_model = OpenAIEmbedding(
            model="text-embedding-ada-002",
            api_key=settings.OPENAI_API_KEY
        )

        # 配置Elasticsearch向量存储
        self.vector_store = ElasticsearchStore(
            index_name="bid_documents",
            es_url="http://elasticsearch:9200",
            es_user="elastic",
            es_password=settings.ES_PASSWORD
        )

    async def index_document(
        self,
        document_id: str,
        document_path: str
    ):
        """
        索引文档到Elasticsearch
        """
        # 1. 加载文档
        documents = SimpleDirectoryReader(
            input_files=[document_path]
        ).load_data()

        # 2. 添加元数据
        for doc in documents:
            doc.metadata["document_id"] = document_id
            doc.metadata["indexed_at"] = datetime.now().isoformat()

        # 3. 创建索引
        index = VectorStoreIndex.from_documents(
            documents,
            vector_store=self.vector_store
        )

        return {
            "document_id": document_id,
            "chunks_indexed": len(documents)
        }

    async def query_documents(
        self,
        query: str,
        filters: dict = None,
        top_k: int = 5
    ):
        """
        查询文档
        """
        # 创建查询引擎
        index = VectorStoreIndex.from_vector_store(self.vector_store)

        query_engine = index.as_query_engine(
            similarity_top_k=top_k,
            filters=self._build_metadata_filters(filters)
        )

        # 执行查询
        response = await query_engine.aquery(query)

        return {
            "answer": response.response,
            "source_nodes": [
                {
                    "content": node.node.text,
                    "score": node.score,
                    "metadata": node.node.metadata
                }
                for node in response.source_nodes
            ]
        }

    async def generate_with_context(
        self,
        query: str,
        context_documents: list[str]
    ):
        """
        基于上下文生成内容
        """
        # 1. 构建提示词
        prompt = self._build_generation_prompt(query, context_documents)

        # 2. 调用LLM
        response = await Settings.llm.acomplete(prompt)

        return response.text
```

**混合检索实现**
```python
from llama_index.core import VectorStoreIndex
from llama_index.core.retrievers import VectorIndexRetriever
from llama_index.core.query_engine import RetrieverQueryEngine
from llama_index.core.postprocessor import SimilarityPostprocessor

class HybridRetriever:
    def __init__(self, vector_store):
        self.index = VectorStoreIndex.from_vector_store(vector_store)

    async def retrieve(
        self,
        query: str,
        filters: dict = None,
        similarity_cutoff: float = 0.7,
        top_k: int = 10
    ):
        """
        混合检索：向量检索 + 后处理
        """
        # 1. 向量检索
        retriever = VectorIndexRetriever(
            index=self.index,
            similarity_top_k=top_k,
            filters=filters
        )

        # 2. 相似度过滤
        postprocessor = SimilarityPostprocessor(
            similarity_cutoff=similarity_cutoff
        )

        # 3. 查询引擎
        query_engine = RetrieverQueryEngine(
            retriever=retriever,
            node_postprocessors=[postprocessor]
        )

        # 4. 执行查询
        response = await query_engine.aquery(query)

        return response.source_nodes
```

**RAG工作流**
```python
class BidContentGenerator:
    """
    标书内容生成器 - 使用RAG
    """
    def __init__(self):
        self.llama_service = LlamaIndexService()
        self.llm = Settings.llm

    async def generate_section(
        self,
        section_type: str,
        requirements: list[str],
        project_context: dict
    ) -> str:
        """
        生成标书章节内容
        """
        # 1. 检索相关案例
        similar_cases = await self._retrieve_similar_cases(
            requirements,
            top_k=5
        )

        # 2. 检索企业能力
        capabilities = await self._retrieve_capabilities(
            requirements,
            top_k=10
        )

        # 3. 构建上下文
        context = self._build_context(
            similar_cases=similar_cases,
            capabilities=capabilities,
            project_context=project_context
        )

        # 4. 生成内容
        prompt = self._build_generation_prompt(
            section_type=section_type,
            requirements=requirements,
            context=context
        )

        response = await self.llm.acomplete(prompt)

        return response.text

    async def _retrieve_similar_cases(
        self,
        requirements: list[str],
        top_k: int = 5
    ):
        """
        检索相似案例
        """
        # 合并需求为查询
        query = " ".join(requirements)

        # 查询案例库
        results = await self.llama_service.query_documents(
            query=query,
            filters={"index_name": "project_cases"},
            top_k=top_k
        )

        return results["source_nodes"]

    async def _retrieve_capabilities(
        self,
        requirements: list[str],
        top_k: int = 10
    ):
        """
        检索企业能力
        """
        query = " ".join(requirements)

        results = await self.llama_service.query_documents(
            query=query,
            filters={"index_name": "enterprise_capabilities"},
            top_k=top_k
        )

        return results["source_nodes"]

    def _build_generation_prompt(
        self,
        section_type: str,
        requirements: list[str],
        context: dict
    ) -> str:
        """
        构建生成提示词
        """
        prompt = f"""
你是一位专业的标书撰写专家。请根据以下信息撰写标书的{section_type}部分。

## 需求分析
{chr(10).join(f"- {req}" for req in requirements)}

## 相关案例
{self._format_cases(context['similar_cases'])}

## 企业能力
{self._format_capabilities(context['capabilities'])}

## 项目背景
- 项目名称: {context['project_context']['name']}
- 预算: {context['project_context']['budget']}万元
- 工期: {context['project_context']['duration']}个月

请撰写专业、详实的{section_type}内容，突出我们的优势和经验。
"""
        return prompt
```

---
