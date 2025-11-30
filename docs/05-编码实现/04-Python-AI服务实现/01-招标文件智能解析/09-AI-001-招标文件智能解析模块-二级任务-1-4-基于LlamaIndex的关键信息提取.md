# Python FastAPI AI 服务任务详细计划 - AI-001 - AI-001: 招标文件智能解析模块 - 二级任务 1.4: 基于LlamaIndex的关键信息提取

**预计工作量**: 5 人天
**完成进度**: 0% (0/5 类别)

#### 1.4.1 数据定义

**待完成任务**:
- [ ] 定义 `ExtractedInfo` 数据结构
  ```python
  class ProjectBasicInfo(BaseModel):
      """项目基本信息"""
      project_name: str
      client_name: str
      budget: Optional[float]
      deadline: Optional[datetime]
      location: Optional[str]

  class TechnicalRequirement(BaseModel):
      """技术要求"""
      requirement_id: str
      category: str  # 'technical'|'business'|'compliance'
      title: str
      description: str
      is_mandatory: bool
      priority: str  # 'high'|'medium'|'low'

  class ExtractedInfo(BaseModel):
      """提取的关键信息"""
      basic_info: ProjectBasicInfo
      technical_requirements: List[TechnicalRequirement]
      business_terms: List[Dict[str, Any]]
      scoring_criteria: List[Dict[str, Any]]
  ```

- [ ] 设计 `project_requirements` 表（Java服务负责）
  ```sql
  CREATE TABLE project_requirements (
      id UUID PRIMARY KEY,
      project_id UUID NOT NULL,
      requirement_type VARCHAR(50),  -- 'technical'|'business'|'compliance'
      category VARCHAR(100),
      title VARCHAR(200) NOT NULL,
      description TEXT,
      priority VARCHAR(20),
      is_mandatory BOOLEAN DEFAULT TRUE,
      match_status VARCHAR(20),  -- 'pending'|'matched'|'unmatched'
      match_score DECIMAL(5, 2),
      FOREIGN KEY (project_id) REFERENCES projects(id)
  );
  ```

#### 1.4.2 前端实现

**待完成任务**:
- [ ] 创建信息提取结果展示页面
  ```typescript
  // apps/frontend/src/pages/documents/ExtractedInfo.tsx
  import { ProDescriptions } from '@ant-design/pro-descriptions';
  import { ProList } from '@ant-design/pro-list';

  export default function ExtractedInfo({ documentId }: Props) {
      // 展示项目基本信息（ProDescriptions）
      // 展示技术要求列表（ProList）
      // 展示商务条款列表
      // 展示评分标准
  }
  ```

- [ ] 创建需求匹配状态展示
  ```typescript
  // 显示每个需求的匹配状态（matched/unmatched）
  // 显示匹配分数
  // 支持手动调整匹配结果
  ```

#### 1.4.3 Java后端实现

**待完成任务**:
- [ ] 创建 `ProjectRequirement` 实体
  ```java
  @Data
  @TableName("project_requirements")
  public class ProjectRequirement extends BaseEntity {
      @TableField("project_id")
      private String projectId;

      @TableField("requirement_type")
      private String requirementType;

      @TableField("title")
      private String title;

      @TableField("description")
      private String description;

      @TableField("is_mandatory")
      private Boolean isMandatory;

      @TableField("priority")
      private String priority;

      @TableField("match_status")
      private String matchStatus;

      @TableField("match_score")
      private BigDecimal matchScore;
  }
  ```

- [ ] 创建 `ProjectRequirementService`
  ```java
  @Service
  public class ProjectRequirementService {

      /**
       * 批量保存提取的需求（Python服务回调）
       * 需求编号: REQ-AI-001
       */
      @Transactional
      public void batchSave(String projectId, List<RequirementDTO> requirements) {
          for (RequirementDTO req : requirements) {
              ProjectRequirement entity = new ProjectRequirement();
              entity.setProjectId(projectId);
              entity.setRequirementType(req.getCategory());
              entity.setTitle(req.getTitle());
              entity.setDescription(req.getDescription());
              entity.setIsMandatory(req.getIsMandatory());
              entity.setPriority(req.getPriority());
              entity.setMatchStatus("pending");

              projectRequirementMapper.insert(entity);
          }
      }
  }
  ```

- [ ] 创建 REST API
  ```java
  @RestController
  @RequestMapping("/api/v1/requirements")
  public class RequirementController {

      @GetMapping
      public Result<Page<ProjectRequirement>> list(@RequestParam String projectId) {
          // 查询项目需求列表
      }

      @PostMapping("/batch")
      public Result<Void> batchCreate(@RequestBody BatchRequirementRequest request) {
          // Python服务回调：批量创建需求
          requirementService.batchSave(request.getProjectId(), request.getRequirements());
          return Result.success();
      }
  }
  ```

#### 1.4.4 Python后端实现

**待完成任务**:
- [ ] 集成 LlamaIndex 0.14.8
  ```python
  # apps/backend-python/app/services/ai/llama_index_service.py
  from llama_index import VectorStoreIndex, ServiceContext, Document
  from llama_index.llms import OpenAI
  from llama_index.embeddings import OpenAIEmbedding

  class LlamaIndexService:
      """LlamaIndex RAG服务"""

      def __init__(self):
          self.llm = OpenAI(model="gpt-4-turbo-preview", api_key=settings.OPENAI_API_KEY)
          self.embed_model = OpenAIEmbedding(api_key=settings.OPENAI_API_KEY)

      async def extract_info(self, document_text: str) -> ExtractedInfo:
          """
          使用LlamaIndex提取关键信息
          需求编号: REQ-AI-001
          """
          # 1. 创建文档索引
          documents = [Document(text=document_text)]
          service_context = ServiceContext.from_defaults(
              llm=self.llm,
              embed_model=self.embed_model
          )
          index = VectorStoreIndex.from_documents(
              documents,
              service_context=service_context
          )

          # 2. 查询项目基本信息
          query_engine = index.as_query_engine()
          basic_info_response = await query_engine.aquery(
              "请提取以下信息：项目名称、招标单位、项目预算、提交截止时间、项目地点"
          )

          # 3. 查询技术要求
          tech_req_response = await query_engine.aquery(
              "请提取所有技术要求，包括：要求标题、详细描述、是否强制、优先级"
          )

          # 4. 解析LLM响应，构建结构化数据
          # TODO: 使用GPT-4输出JSON格式，直接解析

          return ExtractedInfo(
              basic_info=ProjectBasicInfo(...),
              technical_requirements=[...],
              business_terms=[...],
              scoring_criteria=[...]
          )
  ```

- [ ] 创建信息提取API
  ```python
  # apps/backend-python/app/api/v1/information_extraction.py
  @router.post("/extract-requirements")
  async def extract_requirements(document_id: str):
      """
      提取招标文件的关键信息
      需求编号: REQ-AI-001
      """
      # 1. 从Java服务获取文档内容
      doc = await java_client.get_document(document_id)

      # 2. 使用LlamaIndex提取信息
      llama_service = LlamaIndexService()
      extracted = await llama_service.extract_info(doc['plain_text'])

      # 3. 回调Java服务，保存提取结果
      await java_client.save_requirements(
          doc['project_id'],
          extracted.technical_requirements
      )

      return extracted
  ```

- [ ] 实现Celery异步任务
  ```python
  @celery_app.task
  def extract_info_task(document_id: str):
      """异步提取信息任务"""
      # 调用extract_requirements逻辑
      pass
  ```

#### 1.4.5 部署配置

**待完成任务**:
- [ ] 确保 LlamaIndex 0.14.8 已安装 ✅
- [ ] 配置 OPENAI_API_KEY 环境变量
- [ ] 配置 Celery 队列：`ai_extraction`

---
