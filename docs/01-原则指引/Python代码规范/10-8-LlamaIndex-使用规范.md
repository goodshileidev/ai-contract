---
文档类型: 知识库文档
需求编号: DOC-2025-11-004
创建日期: 2025-11-26
创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
最后更新: 2025-11-26
更新者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
状态: 已批准
---

# Python 代码规范 - 8. LlamaIndex 使用规范

### 8.1 索引构建

```python
from llama_index import (
    VectorStoreIndex,
    ServiceContext,
    StorageContext,
    SimpleDirectoryReader
)
from llama_index.vector_stores import ElasticsearchStore
from llama_index.embeddings import OpenAIEmbedding

class RAGService:
    """RAG 服务"""

    def __init__(self):
        # ✅ 配置向量存储
        self.vector_store = ElasticsearchStore(
            index_name="bid_documents",
            es_url="http://localhost:9200"
        )

        # ✅ 配置服务上下文
        self.service_context = ServiceContext.from_defaults(
            embed_model=OpenAIEmbedding(),
            chunk_size=512,
            chunk_overlap=50
        )

    async def build_index(
        self,
        document_path: str
    ) -> VectorStoreIndex:
        """构建文档索引

        需求编号: REQ-AI-002

        Args:
            document_path: 文档路径

        Returns:
            构建好的索引
        """
        # 加载文档
        documents = SimpleDirectoryReader(document_path).load_data()

        # 创建存储上下文
        storage_context = StorageContext.from_defaults(
            vector_store=self.vector_store
        )

        # 构建索引
        index = VectorStoreIndex.from_documents(
            documents,
            service_context=self.service_context,
            storage_context=storage_context
        )

        return index
```

### 8.2 查询处理

```python
class RAGService:

    async def query_document(
        self,
        index: VectorStoreIndex,
        query: str,
        top_k: int = 5
    ) -> dict:
        """查询文档

        Args:
            index: 文档索引
            query: 查询字符串
            top_k: 返回的top结果数量

        Returns:
            查询结果字典
        """
        # ✅ 创建查询引擎
        query_engine = index.as_query_engine(
            similarity_top_k=top_k,
            response_mode="compact"
        )

        # ✅ 执行查询
        response = query_engine.query(query)

        return {
            "answer": str(response),
            "source_nodes": [
                {
                    "text": node.node.text,
                    "score": node.score,
                    "metadata": node.node.metadata
                }
                for node in response.source_nodes
            ]
        }
```

---
