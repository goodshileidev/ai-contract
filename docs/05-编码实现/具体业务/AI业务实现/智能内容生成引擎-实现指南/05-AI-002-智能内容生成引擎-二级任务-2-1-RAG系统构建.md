# Python FastAPI AI 服务任务详细计划 - AI-002 - AI-002: 智能内容生成引擎 - 二级任务 2.1: RAG系统构建

**预计工作量**: 5 人天
**完成进度**: 0% (0/5 类别)

#### 2.1.1 数据定义

**待完成任务**:
- [ ] 定义知识库文档数据模型
  ```python
  # apps/backend-python/app/models/knowledge_base.py
  from pydantic import BaseModel
  from typing import List, Dict, Any, Optional
  from datetime import datetime

  class KnowledgeDocument(BaseModel):
      """知识库文档"""
      doc_id: str
      doc_type: str  # 'product'|'case'|'personnel'|'certification'
      title: str
      content: str
      metadata: Dict[str, Any]
      embedding: Optional[List[float]]  # 向量表示
      created_at: datetime

  class RAGContext(BaseModel):
      """RAG上下文"""
      query: str
      retrieved_docs: List[KnowledgeDocument]
      relevance_scores: List[float]
      total_retrieved: int
  ```

- [ ] 设计PostgreSQL知识库表（Java服务负责）
  ```sql
  -- Java服务管理的企业能力表
  CREATE TABLE company_capabilities (
      id UUID PRIMARY KEY,
      organization_id UUID NOT NULL,
      capability_type VARCHAR(50),  -- 'product'|'service'|'case'|'certificate'
      title VARCHAR(200) NOT NULL,
      description TEXT,
      content TEXT,  -- 详细内容
      tags TEXT[],
      embedding_status VARCHAR(20),  -- 'pending'|'completed'
      created_at TIMESTAMP WITH TIME ZONE,
      FOREIGN KEY (organization_id) REFERENCES organizations(id)
  );
  ```

#### 2.1.2 前端实现

**待完成任务**:
- [ ] 创建知识库管理页面
  ```typescript
  // apps/frontend/src/pages/knowledge/KnowledgeBase.tsx
  import { ProTable } from '@ant-design/pro-table';
  import { Button, Tag } from 'antd';

  export default function KnowledgeBase() {
      const columns = [
          { title: 'ID', dataIndex: 'id', width: 100 },
          { title: '类型', dataIndex: 'capability_type', render: (type) => (
              <Tag color={type === 'product' ? 'blue' : 'green'}>{type}</Tag>
          )},
          { title: '标题', dataIndex: 'title' },
          { title: '向量化状态', dataIndex: 'embedding_status', render: (status) => (
              <Tag color={status === 'completed' ? 'success' : 'processing'}>{status}</Tag>
          )},
          { title: '创建时间', dataIndex: 'created_at', valueType: 'dateTime' },
          {
              title: '操作',
              render: (_, record) => (
                  <>
                      <Button onClick={() => handleVectorize(record.id)}>向量化</Button>
                      <Button onClick={() => handleEdit(record)}>编辑</Button>
                  </>
              )
          }
      ];

      const handleVectorize = async (id: string) => {
          // 调用Python服务向量化API
          await fetch(`http://localhost:8001/api/v1/ai/vectorize-capability/${id}`, {
              method: 'POST'
          });
          message.success('向量化任务已提交');
      };

      return (
          <ProTable
              columns={columns}
              request={async (params) => {
                  // 从Java服务获取知识库列表
                  const response = await fetch('http://localhost:8080/api/v1/capabilities');
                  return response.json();
              }}
              rowKey="id"
              search={false}
          />
      );
  }
  ```

- [ ] 创建RAG检索测试页面
  ```typescript
  // apps/frontend/src/pages/knowledge/RAGTest.tsx
  import { ProForm, ProFormText, ProFormDigit } from '@ant-design/pro-form';

  export function RAGTest() {
      const [retrievedDocs, setRetrievedDocs] = useState([]);

      const testRetrieval = async (values: any) => {
          const response = await fetch('http://localhost:8001/api/v1/ai/rag-retrieve', {
              method: 'POST',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify({
                  query: values.query,
                  top_k: values.top_k || 5
              })
          });

          const result = await response.json();
          setRetrievedDocs(result.retrieved_docs);
      };

      return (
          <div>
              <ProForm onFinish={testRetrieval}>
                  <ProFormText name="query" label="查询" placeholder="输入查询内容..." />
                  <ProFormDigit name="top_k" label="返回数量" initialValue={5} />
              </ProForm>

              <div>
                  <h3>检索结果:</h3>
                  {retrievedDocs.map((doc, idx) => (
                      <Card key={idx} style={{ marginBottom: 16 }}>
                          <p><strong>标题:</strong> {doc.title}</p>
                          <p><strong>相关度:</strong> {doc.relevance_score.toFixed(2)}</p>
                          <p>{doc.content.substring(0, 200)}...</p>
                      </Card>
                  ))}
              </div>
          </div>
      );
  }
  ```

#### 2.1.3 Java后端实现

**待完成任务**:
- [ ] 创建 `CompanyCapability` 实体
  ```java
  // apps/backend-java/src/main/java/com/aibidcomposer/entity/CompanyCapability.java
  @Data
  @TableName("company_capabilities")
  public class CompanyCapability extends BaseEntity {
      @TableField("organization_id")
      private String organizationId;

      @TableField("capability_type")
      private String capabilityType;  // 'product'|'service'|'case'|'certificate'

      @TableField("title")
      private String title;

      @TableField("description")
      private String description;

      @TableField("content")
      private String content;  // 详细内容（供RAG使用）

      @TableField("tags")
      private String tags;  // JSON数组

      @TableField("embedding_status")
      private String embeddingStatus;  // 'pending'|'processing'|'completed'
  }
  ```

- [ ] 创建 `CapabilityService`
  ```java
  // apps/backend-java/src/main/java/com/aibidcomposer/service/CapabilityService.java
  @Service
  @RequiredArgsConstructor
  public class CapabilityService {

      private final CapabilityMapper capabilityMapper;
      private final RabbitTemplate rabbitTemplate;

      /**
       * 创建企业能力记录
       * 需求编号: REQ-AI-002
       */
      public CompanyCapability create(CreateCapabilityRequest request) {
          CompanyCapability capability = new CompanyCapability();
          capability.setOrganizationId(request.getOrganizationId());
          capability.setCapabilityType(request.getCapabilityType());
          capability.setTitle(request.getTitle());
          capability.setContent(request.getContent());
          capability.setEmbeddingStatus("pending");

          capabilityMapper.insert(capability);

          // 发送RabbitMQ消息通知Python服务进行向量化
          rabbitTemplate.convertAndSend("ai.vectorize.queue", capability.getId());

          return capability;
      }

      /**
       * 更新向量化状态（Python服务回调）
       */
      public void updateEmbeddingStatus(String capabilityId, String status) {
          CompanyCapability capability = capabilityMapper.selectById(capabilityId);
          capability.setEmbeddingStatus(status);
          capabilityMapper.updateById(capability);
      }

      /**
       * 查询所有待向量化的能力
       */
      public List<CompanyCapability> getPendingEmbedding(String organizationId) {
          LambdaQueryWrapper<CompanyCapability> wrapper = new LambdaQueryWrapper<>();
          wrapper.eq(CompanyCapability::getOrganizationId, organizationId)
                 .eq(CompanyCapability::getEmbeddingStatus, "pending");
          return capabilityMapper.selectList(wrapper);
      }
  }
  ```

- [ ] 创建REST API
  ```java
  @RestController
  @RequestMapping("/api/v1/capabilities")
  @RequiredArgsConstructor
  public class CapabilityController {

      private final CapabilityService capabilityService;

      @PostMapping
      public Result<CompanyCapability> create(@RequestBody CreateCapabilityRequest request) {
          CompanyCapability capability = capabilityService.create(request);
          return Result.success(capability);
      }

      @GetMapping
      public Result<Page<CompanyCapability>> list(
          @RequestParam String organizationId,
          @RequestParam(defaultValue = "1") int page,
          @RequestParam(defaultValue = "20") int pageSize
      ) {
          // 分页查询
          Page<CompanyCapability> result = capabilityService.list(organizationId, page, pageSize);
          return Result.success(result);
      }

      @PutMapping("/{id}/embedding-status")
      public Result<Void> updateEmbeddingStatus(
          @PathVariable String id,
          @RequestParam String status
      ) {
          // Python服务回调更新向量化状态
          capabilityService.updateEmbeddingStatus(id, status);
          return Result.success();
      }
  }
  ```

#### 2.1.4 Python后端实现

**待完成任务**:
- [ ] 实现 LlamaIndex RAG 服务
  ```python
  # apps/backend-python/app/services/ai/rag_service.py
  from llama_index import VectorStoreIndex, ServiceContext, Document
  from llama_index.vector_stores import ElasticsearchStore
  from llama_index.llms import OpenAI
  from llama_index.embeddings import OpenAIEmbedding
  from typing import List, Dict, Any
  from app.services.ai.elasticsearch_store import ElasticsearchVectorStore

  class RAGService:
      """RAG检索增强生成服务（基于LlamaIndex）"""

      def __init__(self):
          # 初始化LLM
          self.llm = OpenAI(
              model="gpt-4-turbo-preview",
              api_key=settings.OPENAI_API_KEY,
              temperature=0.7
          )

          # 初始化嵌入模型
          self.embed_model = OpenAIEmbedding(api_key=settings.OPENAI_API_KEY)

          # 初始化Elasticsearch向量存储
          self.es_store = ElasticsearchVectorStore()

          # 创建服务上下文
          self.service_context = ServiceContext.from_defaults(
              llm=self.llm,
              embed_model=self.embed_model
          )

      async def build_index(self, documents: List[Dict[str, Any]]) -> VectorStoreIndex:
          """
          构建向量索引
          需求编号: REQ-AI-002
          """
          # 1. 转换为LlamaIndex Document对象
          llama_docs = []
          for doc in documents:
              llama_docs.append(Document(
                  text=doc['content'],
                  metadata={
                      'doc_id': doc['id'],
                      'title': doc['title'],
                      'doc_type': doc['type']
                  }
              ))

          # 2. 使用Elasticsearch作为向量存储构建索引
          index = VectorStoreIndex.from_documents(
              llama_docs,
              service_context=self.service_context,
              vector_store=self.es_store.vector_store  # 使用Elasticsearch
          )

          return index

      async def retrieve(
          self,
          query: str,
          top_k: int = 5,
          organization_id: Optional[str] = None
      ) -> RAGContext:
          """
          RAG检索
          需求编号: REQ-AI-002
          """
          # 1. 从Java服务获取组织的所有能力数据
          capabilities = await self.java_client.get_capabilities(organization_id)

          # 2. 构建索引
          index = await self.build_index(capabilities)

          # 3. 创建查询引擎
          query_engine = index.as_query_engine(
              similarity_top_k=top_k,
              response_mode="compact"  # 紧凑模式，只返回相关内容
          )

          # 4. 执行检索
          response = await query_engine.aquery(query)

          # 5. 提取检索到的文档
          retrieved_docs = []
          for node in response.source_nodes:
              retrieved_docs.append({
                  'doc_id': node.metadata.get('doc_id'),
                  'title': node.metadata.get('title'),
                  'content': node.text,
                  'relevance_score': node.score
              })

          return RAGContext(
              query=query,
              retrieved_docs=retrieved_docs,
              relevance_scores=[node.score for node in response.source_nodes],
              total_retrieved=len(retrieved_docs)
          )

      async def generate_with_rag(
          self,
          query: str,
          context: RAGContext,
          generation_prompt: str
      ) -> str:
          """
          基于RAG上下文生成内容
          需求编号: REQ-AI-002
          """
          # 1. 构建增强Prompt
          context_text = "\n\n".join([
              f"[{doc['title']}]\n{doc['content']}"
              for doc in context.retrieved_docs
          ])

          full_prompt = f"""
{generation_prompt}

参考上下文：
{context_text}

用户需求：
{query}

请基于以上参考上下文生成内容。
"""

          # 2. 调用LLM生成
          response = await self.llm.acomplete(full_prompt)

          return response.text
  ```

- [ ] 创建向量化Celery任务
  ```python
  # apps/backend-python/app/tasks/vectorization.py
  from app.tasks.celery_app import celery_app
  from app.services.ai.rag_service import RAGService
  from app.clients.java_api_client import JavaAPIClient

  @celery_app.task(bind=True, max_retries=3)
  def vectorize_capability_task(self: Task, capability_id: str):
      """
      向量化企业能力任务
      需求编号: REQ-AI-002
      """
      try:
          java_client = JavaAPIClient()

          # 1. 更新Java服务：状态=processing
          await java_client.update_embedding_status(capability_id, "processing")

          # 2. 获取能力数据
          capability = await java_client.get_capability(capability_id)

          # 3. 向量化并存储到Elasticsearch
          rag_service = RAGService()
          await rag_service.build_index([{
              'id': capability['id'],
              'title': capability['title'],
              'content': capability['content'],
              'type': capability['capability_type']
          }])

          # 4. 更新Java服务：状态=completed
          await java_client.update_embedding_status(capability_id, "completed")

          return {"status": "success", "capability_id": capability_id}

      except Exception as e:
          await java_client.update_embedding_status(capability_id, "failed")
          if self.request.retries < self.max_retries:
              raise self.retry(exc=e, countdown=60)
          raise
  ```

- [ ] 创建RAG API端点
  ```python
  # apps/backend-python/app/api/v1/rag.py
  from fastapi import APIRouter, HTTPException
  from pydantic import BaseModel

  router = APIRouter(prefix="/api/v1/ai", tags=["RAG"])

  class RAGRetrieveRequest(BaseModel):
      query: str
      top_k: int = 5
      organization_id: Optional[str] = None

  @router.post("/rag-retrieve")
  async def rag_retrieve(request: RAGRetrieveRequest):
      """
      RAG检索
      需求编号: REQ-AI-002
      """
      rag_service = RAGService()
      context = await rag_service.retrieve(
          query=request.query,
          top_k=request.top_k,
          organization_id=request.organization_id
      )

      return {
          "query": context.query,
          "retrieved_docs": context.retrieved_docs,
          "total_retrieved": context.total_retrieved
      }

  @router.post("/vectorize-capability/{capability_id}")
  async def vectorize_capability(capability_id: str):
      """
      触发能力向量化
      需求编号: REQ-AI-002
      """
      from app.tasks.vectorization import vectorize_capability_task

      task = vectorize_capability_task.delay(capability_id)

      return {
          "task_id": task.id,
          "status": "processing",
          "message": "向量化任务已提交"
      }
  ```

#### 2.1.5 部署配置

**待完成任务**:
- [ ] 确保 LlamaIndex 0.14.8 已安装 ✅
- [ ] 配置 Elasticsearch 向量索引
  ```yaml
  # docker-compose.yml
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:9.2.1
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=true
      - ELASTIC_PASSWORD=${ELASTICSEARCH_PASSWORD}
      - "ES_JAVA_OPTS=-Xms2g -Xmx2g"
    ports:
      - "9200:9200"
    volumes:
      - elasticsearch_data:/usr/share/elasticsearch/data
  ```

- [ ] 配置 Celery 向量化队列
  ```python
  # apps/backend-python/app/tasks/celery_app.py
  celery_app = Celery(
      'aibidcomposer',
      broker=settings.RABBITMQ_URL,
      backend=settings.REDIS_URL
  )

  celery_app.conf.task_routes = {
      'app.tasks.vectorization.vectorize_capability_task': {'queue': 'vectorization'},
  }
  ```

**验收标准**:
- [ ] RAG检索能够返回相关文档
- [ ] 相关性评分准确
- [ ] 向量化任务成功执行
- [ ] Elasticsearch索引正常工作

---
