# Python FastAPI AI 服务任务详细计划 - AI-002 - AI-002: 智能内容生成引擎 - 二级任务 2.5: API接口整合和测试

**预计工作量**: 4 人天
**完成进度**: 0% (0/5 类别)

#### 2.5.1 数据定义

**待完成任务**:
- [ ] 定义统一的AI服务响应格式（已完成）

#### 2.5.2 前端实现

**待完成任务**:
- [ ] 整合所有AI服务API
  ```typescript
  // apps/frontend/src/services/ai.service.ts
  export const aiService = {
      // RAG相关
      ragRetrieve: (query: string, topK: number = 5) => { ... },

      // 生成相关
      generateContent: (type: string, projectId: string, documentId: string) => { ... },

      // 助手相关
      chatWithAssistant: (assistantType: string, message: string) => { ... },

      // 优化相关
      optimizeContent: (content: string, type: string) => { ... },

      // 任务状态查询
      getTaskStatus: (taskId: string) => { ... },
  };
  ```

#### 2.5.3 Java后端实现

**待完成任务**:
- [ ] 创建AI服务统一代理（可选）

#### 2.5.4 Python后端实现

**待完成任务**:
- [ ] 整合所有API路由
  ```python
  # apps/backend-python/app/api/v1/__init__.py
  from fastapi import APIRouter
  from app.api.v1 import (
      document_parser,
      information_extraction,
      rag,
      generation,
      assistant,
      optimization
  )

  api_router = APIRouter()
  api_router.include_router(document_parser.router)
  api_router.include_router(information_extraction.router)
  api_router.include_router(rag.router)
  api_router.include_router(generation.router)
  api_router.include_router(assistant.router)
  api_router.include_router(optimization.router)
  ```

- [ ] 完善API文档
  ```python
  # apps/backend-python/app/main.py
  app = FastAPI(
      title="AI标书智能创作平台 - AI服务",
      description="""
## 智能内容生成引擎 API

提供以下AI能力：

### 1. RAG检索增强生成
- 企业知识库管理
- 语义检索
- 上下文增强生成

### 2. 智能内容生成
- 技术方案生成
- 实施方案生成
- 团队介绍生成
- 案例引用生成

### 3. AI助手矩阵
- 技术专家助手
- 商务专家助手
- 合规专家助手
- 质量审查助手

### 4. 内容优化
- 内容润色
- 简化/扩展
- 正式化
- 术语检查
      """,
      version="1.0.0",
      docs_url="/docs",
      redoc_url="/redoc"
  )
  ```

#### 2.5.5 部署配置

**待完成任务**:
- [ ] 编写集成测试脚本
  ```bash
  # scripts/test-ai-002-integration.sh
  #!/bin/bash

  echo "===== 测试AI-002: 智能内容生成引擎 ====="

  echo "1. 测试RAG检索..."
  curl -X POST http://localhost:8001/api/v1/ai/rag-retrieve \
    -H "Content-Type: application/json" \
    -d '{"query": "云计算技术", "top_k": 5}'

  echo "\n2. 测试内容生成..."
  curl -X POST http://localhost:8001/api/v1/ai/generate-content \
    -H "Content-Type: application/json" \
    -d '{
      "generation_type": "technical_solution",
      "project_id": "test-project-id",
      "document_id": "test-doc-id",
      "requirements": [{"title": "云平台搭建", "description": "需要搭建私有云平台"}],
      "word_count": 500
    }'

  echo "\n3. 测试AI助手..."
  curl -X POST http://localhost:8001/api/v1/ai/assistant-chat \
    -H "Content-Type: application/json" \
    -d '{
      "assistant_type": "technical_expert",
      "message": "如何设计一个高可用的云平台架构？"
    }'

  echo "\n4. 测试内容优化..."
  curl -X POST http://localhost:8001/api/v1/ai/optimize-content \
    -H "Content-Type: application/json" \
    -d '{
      "content": "我们公司有很多年的经验。",
      "optimization_type": "formalize"
    }'
  ```

**验收标准**:
- [ ] 所有API端点正常响应
- [ ] API文档完整准确
- [ ] 集成测试全部通过
- [ ] 错误处理正确

---
