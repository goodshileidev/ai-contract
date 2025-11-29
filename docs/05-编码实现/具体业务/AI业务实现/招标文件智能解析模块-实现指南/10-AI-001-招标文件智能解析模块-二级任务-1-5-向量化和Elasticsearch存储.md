# Python FastAPI AI 服务任务详细计划 - AI-001 - AI-001: 招标文件智能解析模块 - 二级任务 1.5: 向量化和Elasticsearch存储

**预计工作量**: 4 人天
**完成进度**: 0% (0/5 类别)

#### 1.5.1 数据定义

**待完成任务**:
- [ ] 设计 Elasticsearch 索引结构
  ```json
  {
    "mappings": {
      "properties": {
        "document_id": { "type": "keyword" },
        "project_id": { "type": "keyword" },
        "content": { "type": "text", "analyzer": "ik_max_word" },
        "embedding": {
          "type": "dense_vector",
          "dims": 1536,
          "index": true,
          "similarity": "cosine"
        },
        "metadata": { "type": "object" },
        "created_at": { "type": "date" }
      }
    }
  }
  ```

#### 1.5.2 前端实现

**待完成任务**:
- [ ] 创建语义搜索界面
  ```typescript
  // apps/frontend/src/components/search/SemanticSearch.tsx
  import { ProFormText } from '@ant-design/pro-form';

  export function SemanticSearch() {
      const [results, setResults] = useState([]);

      const handleSearch = async (query: string) => {
          const response = await fetch('http://localhost:8001/api/v1/ai/semantic-search', {
              method: 'POST',
              body: JSON.stringify({ query, top_k: 10 })
          });
          setResults(await response.json());
      };

      return (
          <div>
              <ProFormText
                  name="query"
                  placeholder="输入搜索关键词..."
                  fieldProps={{
                      onPressEnter: (e) => handleSearch(e.target.value)
                  }}
              />
              <SearchResults results={results} />
          </div>
      );
  }
  ```

#### 1.5.3 Java后端实现

**待完成任务**:
- [ ] 无需Java后端支持（纯Python实现）

#### 1.5.4 Python后端实现

**待完成任务**:
- [ ] 实现 Elasticsearch 向量存储服务
  ```python
  # apps/backend-python/app/services/ai/elasticsearch_store.py
  from elasticsearch import AsyncElasticsearch
  from llama_index.vector_stores import ElasticsearchStore
  from app.services.ai.embedding_service import EmbeddingService

  class ElasticsearchVectorStore:
      """Elasticsearch向量存储（主力方案）"""

      def __init__(self):
          self.es_client = AsyncElasticsearch(
              hosts=[settings.ELASTICSEARCH_URL],
              basic_auth=(settings.ELASTICSEARCH_USER, settings.ELASTICSEARCH_PASSWORD)
          )
          self.index_name = "aibidcomposer-vectors"

      async def initialize_index(self):
          """初始化索引"""
          if not await self.es_client.indices.exists(index=self.index_name):
              await self.es_client.indices.create(
                  index=self.index_name,
                  body={
                      "mappings": {
                          "properties": {
                              "content": {"type": "text"},
                              "embedding": {
                                  "type": "dense_vector",
                                  "dims": 1536,
                                  "index": True,
                                  "similarity": "cosine"
                              },
                              "document_id": {"type": "keyword"},
                              "project_id": {"type": "keyword"},
                              "created_at": {"type": "date"}
                          }
                      }
                  }
              )

      async def add_documents(
          self,
          documents: List[Dict[str, Any]],
          embeddings: List[List[float]]
      ):
          """添加文档到向量库"""
          from elasticsearch.helpers import async_bulk

          actions = []
          for i, (doc, embedding) in enumerate(zip(documents, embeddings)):
              actions.append({
                  "_index": self.index_name,
                  "_id": doc.get("id", f"doc_{i}"),
                  "_source": {
                      "content": doc.get("text", ""),
                      "embedding": embedding,
                      "document_id": doc.get("document_id", ""),
                      "project_id": doc.get("project_id", ""),
                      "created_at": doc.get("created_at", "")
                  }
              })

          await async_bulk(self.es_client, actions)

      async def semantic_search(
          self,
          query_text: str,
          top_k: int = 10
      ) -> List[Dict[str, Any]]:
          """语义搜索"""
          # 1. 获取查询向量
          embedding_service = EmbeddingService()
          query_embedding = await embedding_service.embed_text(query_text)

          # 2. 执行kNN搜索
          response = await self.es_client.search(
              index=self.index_name,
              body={
                  "knn": {
                      "field": "embedding",
                      "query_vector": query_embedding,
                      "k": top_k,
                      "num_candidates": top_k * 10
                  }
              },
              size=top_k
          )

          return [
              {
                  "id": hit["_id"],
                  "score": hit["_score"],
                  "content": hit["_source"]["content"],
                  "metadata": {
                      "document_id": hit["_source"]["document_id"],
                      "project_id": hit["_source"]["project_id"]
                  }
              }
              for hit in response["hits"]["hits"]
          ]
  ```

- [ ] 实现向量化服务
  ```python
  # apps/backend-python/app/services/ai/embedding_service.py
  from openai import AsyncOpenAI

  class EmbeddingService:
      """向量嵌入服务"""

      def __init__(self):
          self.client = AsyncOpenAI(api_key=settings.OPENAI_API_KEY)

      async def embed_text(self, text: str) -> List[float]:
          """文本转向量"""
          response = await self.client.embeddings.create(
              model="text-embedding-ada-002",
              input=text
          )
          return response.data[0].embedding

      async def embed_documents(self, documents: List[str]) -> List[List[float]]:
          """批量嵌入"""
          response = await self.client.embeddings.create(
              model="text-embedding-ada-002",
              input=documents
          )
          return [item.embedding for item in response.data]
  ```

- [ ] 创建API端点
  ```python
  @router.post("/vectorize-document")
  async def vectorize_document(document_id: str):
      """文档向量化"""
      # 1. 获取文档内容
      doc = await java_client.get_document(document_id)

      # 2. 文本嵌入
      embedding_service = EmbeddingService()
      embedding = await embedding_service.embed_text(doc['plain_text'])

      # 3. 存储到Elasticsearch
      es_store = ElasticsearchVectorStore()
      await es_store.add_documents(
          documents=[{"id": document_id, "text": doc['plain_text']}],
          embeddings=[embedding]
      )

      return {"status": "success"}

  @router.post("/semantic-search")
  async def semantic_search(query: str, top_k: int = 10):
      """语义搜索"""
      es_store = ElasticsearchVectorStore()
      results = await es_store.semantic_search(query, top_k)
      return results
  ```

#### 1.5.5 部署配置

**待完成任务**:
- [ ] 确保 Elasticsearch 9.2.1 已部署 ✅
- [ ] 初始化向量索引（启动时自动执行）
- [ ] 配置 Elasticsearch 环境变量

---
