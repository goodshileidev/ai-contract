# Python FastAPI AI 服务任务详细计划 - AI-001 - AI-001: 招标文件智能解析模块 - 二级任务 1.1: PDF文档解析引擎

**预计工作量**: 3 人天
**完成进度**: 0% (0/5 类别)

#### 1.1.1 数据定义

**待完成任务**:
- [ ] 定义 `ParsedDocument` 数据模型
  ```python
  # app/models/parsed_document.py
  from pydantic import BaseModel
  from typing import List, Dict, Any, Optional
  from datetime import datetime

  class ParsedDocument(BaseModel):
      """解析后的文档数据模型"""
      document_id: str
      file_name: str
      file_type: str  # 'pdf' | 'docx' | 'xlsx'
      file_size: int
      page_count: int
      parsed_content: Dict[str, Any]  # 结构化内容
      plain_text: str  # 纯文本
      metadata: Dict[str, Any]
      created_at: datetime
  ```

- [ ] 定义 `DocumentMetadata` 结构
  ```python
  class DocumentMetadata(BaseModel):
      """文档元数据"""
      title: Optional[str]
      author: Optional[str]
      created_date: Optional[datetime]
      modified_date: Optional[datetime]
      keywords: List[str]
      page_count: int
  ```

- [ ] 定义 `ParsedPage` 结构（PDF页面）
  ```python
  class ParsedPage(BaseModel):
      """PDF页面解析结果"""
      page_number: int
      text_content: str
      images: List[str]  # Base64 encoded images
      tables: List[Dict[str, Any]]
      bounding_boxes: List[Dict[str, float]]
  ```

- [ ] 设计 PostgreSQL 存储表结构
  ```sql
  -- Java服务负责创建表
  -- Python服务通过REST API写入数据
  CREATE TABLE bidding_documents (
      id UUID PRIMARY KEY,
      project_id UUID NOT NULL,
      file_name VARCHAR(255) NOT NULL,
      file_path TEXT NOT NULL,
      file_size BIGINT NOT NULL,
      file_type VARCHAR(50) NOT NULL,
      parsed_status VARCHAR(20) DEFAULT 'pending',
      parsed_content JSONB,  -- 存储ParsedDocument JSON
      parsed_at TIMESTAMP WITH TIME ZONE,
      parse_error TEXT,
      FOREIGN KEY (project_id) REFERENCES projects(id)
  );
  ```

**验收标准**:
- [ ] 所有数据模型通过 Pydantic 验证
- [ ] 数据模型支持 JSON 序列化/反序列化
- [ ] 表结构设计文档已更新

---

#### 1.1.2 前端实现

**待完成任务**:
- [ ] 创建文件上传组件
  ```typescript
  // apps/frontend/src/components/upload/DocumentUpload.tsx
  import { ProFormUploadButton } from '@ant-design/pro-form';
  import { message } from 'antd';

  export function DocumentUpload({ projectId, onSuccess }: Props) {
      return (
          <ProFormUploadButton
              name="file"
              label="上传招标文件"
              max={1}
              fieldProps={{
                  accept: '.pdf,.docx,.xlsx',
                  customRequest: async ({ file, onSuccess, onError }) => {
                      try {
                          const formData = new FormData();
                          formData.append('file', file);
                          formData.append('project_id', projectId);

                          const response = await fetch('http://localhost:8001/api/v1/ai/parse-document', {
                              method: 'POST',
                              body: formData,
                          });

                          if (response.ok) {
                              onSuccess(await response.json());
                              message.success('文件上传成功，正在解析...');
                          }
                      } catch (error) {
                          onError(error);
                          message.error('上传失败');
                      }
                  }
              }}
              extra="支持 PDF、Word、Excel 格式，最大 50MB"
          />
      );
  }
  ```

- [ ] 创建解析进度显示组件
  ```typescript
  // apps/frontend/src/components/parsing/ParsingProgress.tsx
  import { Progress, Card, Spin } from 'antd';

  export function ParsingProgress({ taskId }: { taskId: string }) {
      const { data, isLoading } = useQuery({
          queryKey: ['parsing-progress', taskId],
          queryFn: () => fetch(`http://localhost:8001/api/v1/ai/tasks/${taskId}`).then(r => r.json()),
          refetchInterval: 1000,  // 每秒轮询
      });

      return (
          <Card title="文档解析中...">
              <Progress percent={data?.progress || 0} status="active" />
              <p>{data?.status_message}</p>
          </Card>
      );
  }
  ```

- [ ] 创建解析结果展示页面
  ```typescript
  // apps/frontend/src/pages/documents/ParsingResult.tsx
  import { ProDescriptions } from '@ant-design/pro-descriptions';

  export default function ParsingResult({ documentId }: Props) {
      // 展示解析后的结构化内容
      // - 项目基本信息
      // - 技术要求
      // - 商务条款
      // - 评分标准
  }
  ```

- [ ] 集成到项目详情页
  ```typescript
  // apps/frontend/src/pages/projects/ProjectDetail.tsx
  // 添加"上传招标文件"按钮
  // 显示已上传文件列表
  // 支持查看解析结果
  ```

**验收标准**:
- [ ] 上传组件支持拖拽上传
- [ ] 实时显示上传进度
- [ ] 解析进度实时更新
- [ ] 解析结果正确展示

---

#### 1.1.3 Java后端实现

**待完成任务**:
- [ ] 创建 `BiddingDocument` 实体（已存在，需验证字段完整性）
  ```java
  // apps/backend-java/ac-dao-postgres/src/main/java/com/aibidcomposer/dao/entity/BiddingDocument.java
  @Data
  @TableName(value = "bidding_documents", autoResultMap = true)
  public class BiddingDocument extends BaseEntity {
      @TableField("project_id")
      private String projectId;

      @TableField("file_name")
      private String fileName;

      @TableField("file_path")
      private String filePath;

      @TableField("file_size")
      private Long fileSize;

      @TableField("file_type")
      private String fileType;

      @TableField("parsed_status")
      private String parsedStatus;  // 'pending'|'processing'|'success'|'failed'

      @TableField(value = "parsed_content", typeHandler = JacksonTypeHandler.class)
      private Map<String, Object> parsedContent;

      @TableField("parsed_at")
      private LocalDateTime parsedAt;

      @TableField("parse_error")
      private String parseError;
  }
  ```

- [ ] 创建 `BiddingDocumentService` 业务逻辑
  ```java
  // apps/backend-java/ac-service/src/main/java/com/aibidcomposer/service/BiddingDocumentService.java
  @Service
  @RequiredArgsConstructor
  public class BiddingDocumentService {

      /**
       * 创建招标文件记录（文件上传时调用）
       * 需求编号: REQ-AI-001
       */
      public BiddingDocument createRecord(String projectId, String fileName,
                                          String filePath, Long fileSize, String fileType) {
          BiddingDocument doc = new BiddingDocument();
          doc.setProjectId(projectId);
          doc.setFileName(fileName);
          doc.setFilePath(filePath);
          doc.setFileSize(fileSize);
          doc.setFileType(fileType);
          doc.setParsedStatus("pending");

          biddingDocumentMapper.insert(doc);

          // 发送RabbitMQ消息通知Python服务开始解析
          rabbitTemplate.convertAndSend("ai.parse.queue", doc.getId());

          return doc;
      }

      /**
       * 更新解析状态（Python服务回调）
       * 需求编号: REQ-AI-001
       */
      public void updateParseStatus(String docId, String status,
                                    Map<String, Object> content, String error) {
          BiddingDocument doc = biddingDocumentMapper.selectById(docId);
          doc.setParsedStatus(status);
          doc.setParsedContent(content);
          doc.setParsedAt(LocalDateTime.now());
          doc.setParseError(error);

          biddingDocumentMapper.updateById(doc);
      }
  }
  ```

- [ ] 创建 `BiddingDocumentController` REST API
  ```java
  // apps/backend-java/ac-web-api/src/main/java/com/aibidcomposer/web/controller/BiddingDocumentController.java
  @RestController
  @RequestMapping("/api/v1/bidding-documents")
  @RequiredArgsConstructor
  public class BiddingDocumentController {

      private final BiddingDocumentService biddingDocumentService;

      /**
       * 获取招标文件列表
       * 需求编号: REQ-AI-001
       */
      @GetMapping
      public Result<Page<BiddingDocument>> list(@RequestParam String projectId,
                                                  @RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "20") int pageSize) {
          Page<BiddingDocument> result = biddingDocumentService.listByProject(projectId, page, pageSize);
          return Result.success(result);
      }

      /**
       * 获取解析结果
       * 需求编号: REQ-AI-001
       */
      @GetMapping("/{id}")
      public Result<BiddingDocument> get(@PathVariable String id) {
          BiddingDocument doc = biddingDocumentService.getById(id);
          return Result.success(doc);
      }
  }
  ```

- [ ] 配置 RabbitMQ 消息队列（Java → Python通信）
  ```java
  // apps/backend-java/ac-service/src/main/java/com/aibidcomposer/config/RabbitMQConfig.java
  @Configuration
  public class RabbitMQConfig {

      @Bean
      public Queue aiParseQueue() {
          return new Queue("ai.parse.queue", true);  // durable=true
      }

      @Bean
      public Queue aiParseResultQueue() {
          return new Queue("ai.parse.result.queue", true);
      }
  }
  ```

**验收标准**:
- [ ] BiddingDocument实体映射正确
- [ ] Java服务可以创建解析记录
- [ ] Java服务可以查询解析结果
- [ ] RabbitMQ消息发送成功

---

#### 1.1.4 Python后端实现

**待完成任务**:
- [ ] 创建 FastAPI 文件上传端点
  ```python
  # apps/backend-python/app/api/v1/document_parser.py
  from fastapi import APIRouter, UploadFile, File, Form, HTTPException
  from app.services.ai.document_parser import DocumentParserService
  from app.models.parsed_document import ParsedDocument

  router = APIRouter(prefix="/api/v1/ai", tags=["Document Parser"])

  @router.post("/parse-document")
  async def parse_document(
      file: UploadFile = File(...),
      project_id: str = Form(...)
  ):
      """
      解析招标文件
      需求编号: REQ-AI-001
      """
      # 1. 验证文件类型和大小
      if file.content_type not in ['application/pdf', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document']:
          raise HTTPException(status_code=400, detail="不支持的文件类型")

      if file.size > 50 * 1024 * 1024:  # 50MB
          raise HTTPException(status_code=400, detail="文件大小超过限制")

      # 2. 保存文件到MinIO（通过Java服务API）
      file_path = await save_to_storage(file, project_id)

      # 3. 创建解析任务记录（调用Java服务API）
      doc_record = await create_document_record(project_id, file.filename, file_path, file.size)

      # 4. 异步解析（发送到Celery任务队列）
      from app.tasks.document_parsing import parse_pdf_task
      task = parse_pdf_task.delay(doc_record['id'], file_path)

      return {
          "task_id": task.id,
          "document_id": doc_record['id'],
          "status": "processing",
          "message": "文档解析任务已提交"
      }
  ```

- [ ] 实现 PDF 解析服务
  ```python
  # apps/backend-python/app/services/ai/document_parser.py
  import pdfplumber
  from typing import Dict, Any, List

  class DocumentParserService:
      """文档解析服务"""

      async def parse_pdf(self, file_path: str) -> ParsedDocument:
          """
          解析PDF文档
          需求编号: REQ-AI-001
          """
          pages = []

          with pdfplumber.open(file_path) as pdf:
              for i, page in enumerate(pdf.pages):
                  # 提取文本
                  text = page.extract_text()

                  # 提取表格
                  tables = page.extract_tables()

                  # 提取图片（可选）
                  # images = page.images

                  pages.append({
                      "page_number": i + 1,
                      "text_content": text,
                      "tables": tables,
                  })

          # 合并所有页面文本
          plain_text = "\n\n".join([p["text_content"] for p in pages])

          return ParsedDocument(
              document_id=...,
              file_name=...,
              file_type="pdf",
              page_count=len(pages),
              parsed_content={"pages": pages},
              plain_text=plain_text,
              metadata={}
          )
  ```

- [ ] 实现 Celery 异步任务
  ```python
  # apps/backend-python/app/tasks/document_parsing.py
  from celery import Task
  from app.tasks.celery_app import celery_app
  from app.services.ai.document_parser import DocumentParserService

  @celery_app.task(bind=True, max_retries=3)
  def parse_pdf_task(self: Task, document_id: str, file_path: str):
      """
      异步解析PDF任务
      需求编号: REQ-AI-001
      """
      try:
          # 1. 更新Java服务：状态=processing
          await update_java_status(document_id, "processing")

          # 2. 解析PDF
          parser = DocumentParserService()
          parsed_doc = await parser.parse_pdf(file_path)

          # 3. 更新Java服务：状态=success，内容=parsed_doc
          await update_java_status(
              document_id,
              "success",
              content=parsed_doc.dict()
          )

          return {"status": "success", "document_id": document_id}

      except Exception as e:
          # 失败：更新Java服务状态=failed
          await update_java_status(document_id, "failed", error=str(e))

          # 重试
          if self.request.retries < self.max_retries:
              raise self.retry(exc=e, countdown=60 * (self.request.retries + 1))

          raise
  ```

- [ ] 实现与Java服务通信
  ```python
  # apps/backend-python/app/services/java_api_client.py
  import httpx
  from typing import Dict, Any

  class JavaAPIClient:
      """Java服务API客户端"""

      def __init__(self):
          self.base_url = "http://backend-java:8080"
          self.client = httpx.AsyncClient()

      async def update_document_status(
          self,
          document_id: str,
          status: str,
          content: Dict[str, Any] = None,
          error: str = None
      ):
          """
          更新Java服务中的文档解析状态
          需求编号: REQ-AI-001
          """
          response = await self.client.put(
              f"{self.base_url}/api/v1/bidding-documents/{document_id}/parse-status",
              json={
                  "status": status,
                  "content": content,
                  "error": error
              }
          )
          response.raise_for_status()
          return response.json()
  ```

**验收标准**:
- [ ] POST /api/v1/ai/parse-document 接口正常
- [ ] PDF解析提取文本正确
- [ ] PDF解析提取表格正确
- [ ] Celery异步任务执行成功
- [ ] 解析结果正确写回Java服务

---

#### 1.1.5 部署配置

**待完成任务**:
- [ ] 更新 Python 服务 Dockerfile
  ```dockerfile
  # apps/backend-python/Dockerfile
  FROM python:3.11-slim

  # 安装系统依赖（PDF解析需要）
  RUN apt-get update && apt-get install -y \
      libpoppler-cpp-dev \
      poppler-utils \
      tesseract-ocr \
      tesseract-ocr-chi-sim \
      && rm -rf /var/lib/apt/lists/*

  WORKDIR /app

  # 复制依赖文件
  COPY pyproject.toml ./
  RUN pip install --no-cache-dir -e .

  # 复制应用代码
  COPY app /app/app

  EXPOSE 8001

  CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8001"]
  ```

- [ ] 添加环境变量
  ```bash
  # .env
  # Python AI服务配置
  PYTHON_AI_SERVICE_URL=http://backend-python:8001
  MAX_UPLOAD_SIZE=52428800  # 50MB

  # Celery配置
  CELERY_BROKER_URL=amqp://rabbitmq:rabbitmq@rabbitmq:5672/
  CELERY_RESULT_BACKEND=redis://:redis-password@redis:6379/1

  # Java服务连接
  JAVA_SERVICE_URL=http://backend-java:8080
  JAVA_SERVICE_API_KEY=secret_key_here
  ```

- [ ] 配置 docker-compose.yml
  ```yaml
  # docker-compose.yml
  services:
    backend-python:
      build:
        context: ./apps/backend-python
        dockerfile: Dockerfile
      ports:
        - "8001:8001"
      environment:
        - CELERY_BROKER_URL=${CELERY_BROKER_URL}
        - JAVA_SERVICE_URL=${JAVA_SERVICE_URL}
      depends_on:
        - rabbitmq
        - redis
        - backend-java
      volumes:
        - ./apps/backend-python/app:/app/app  # 开发模式热重载

    ai-worker:
      build:
        context: ./apps/backend-python
        dockerfile: Dockerfile
      command: celery -A app.tasks.celery_app worker --loglevel=info -Q pdf_parsing
      depends_on:
        - rabbitmq
        - redis
  ```

- [ ] 配置健康检查
  ```python
  # apps/backend-python/app/main.py
  @app.get("/health")
  async def health_check():
      """健康检查端点"""
      return {
          "status": "healthy",
          "service": "python-ai-service",
          "version": "1.0.0"
      }
  ```

**验收标准**:
- [ ] Docker镜像构建成功
- [ ] 服务启动正常
- [ ] 健康检查端点返回200
- [ ] Celery Worker连接成功

---
