# Python FastAPI AI 服务任务详细计划 - AI-001 - AI-001: 招标文件智能解析模块 - 二级任务 1.6: API接口整合和测试

**预计工作量**: 2 人天
**完成进度**: 0% (0/5 类别)

#### 1.6.1 数据定义

**待完成任务**:
- [ ] 定义API请求/响应模型（已在各子任务完成）

#### 1.6.2 前端实现

**待完成任务**:
- [ ] 集成所有API到前端服务层
  ```typescript
  // apps/frontend/src/services/ai.service.ts
  export const aiService = {
      parseDocument: (file: File, projectId: string) => { ... },
      extractRequirements: (documentId: string) => { ... },
      semanticSearch: (query: string) => { ... },
  };
  ```

#### 1.6.3 Java后端实现

**待完成任务**:
- [ ] 创建统一的AI服务代理（可选）
  ```java
  @Service
  public class AIServiceProxy {
      @Value("${ai.service.url}")
      private String aiServiceUrl;

      public <T> T callAIService(String endpoint, Object request, Class<T> responseType) {
          // 统一调用Python AI服务
      }
  }
  ```

#### 1.6.4 Python后端实现

**待完成任务**:
- [ ] 整合所有API到主路由
  ```python
  # apps/backend-python/app/api/v1/__init__.py
  from fastapi import APIRouter
  from app.api.v1 import (
      document_parser,
      information_extraction,
      semantic_search
  )

  api_router = APIRouter()
  api_router.include_router(document_parser.router)
  api_router.include_router(information_extraction.router)
  api_router.include_router(semantic_search.router)
  ```

- [ ] 添加API文档
  ```python
  # apps/backend-python/app/main.py
  app = FastAPI(
      title="AI标书智能创作平台 - AI服务",
      description="提供文档解析、信息提取、语义搜索等AI能力",
      version="1.0.0",
      docs_url="/docs",
      redoc_url="/redoc"
  )
  ```

#### 1.6.5 部署配置

**待完成任务**:
- [ ] 编写集成测试脚本
  ```bash
  # scripts/test-ai-integration.sh
  #!/bin/bash

  echo "测试文档上传..."
  curl -X POST http://localhost:8001/api/v1/ai/parse-document \
    -F "file=@test.pdf" \
    -F "project_id=test-project-id"

  echo "测试信息提取..."
  curl -X POST http://localhost:8001/api/v1/ai/extract-requirements \
    -H "Content-Type: application/json" \
    -d '{"document_id": "test-doc-id"}'

  echo "测试语义搜索..."
  curl -X POST http://localhost:8001/api/v1/ai/semantic-search \
    -H "Content-Type: application/json" \
    -d '{"query": "技术要求", "top_k": 10}'
  ```

---
