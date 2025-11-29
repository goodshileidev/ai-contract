---
文档类型: 架构文档
需求编号: DOC-2025-11-001
创建日期: 2025-11-15
创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
最后更新: 2025-11-26
更新者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
状态: 已批准
---

# AI标书智能创作平台 - API接口设计 - 🤖 AI服务API

> **服务**: Python FastAPI
> **端口**: 8001
> **技术栈**: LlamaIndex (主力) + LangChain (备用) + Elasticsearch
> **说明**: 所有AI相关功能由独立的Python服务提供

### 1. 分析需求

```yaml
POST /api/v1/ai/analyze-requirements
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body:
  project_id: string (required)
  document_id: string (optional)
  content: string (optional, if document_id not provided)

Response: 202 Accepted
  {
    "success": true,
    "data": {
      "task_id": "uuid",
      "status": "processing",
      "estimated_time": 60
    },
    "message": "需求分析任务已提交"
  }
```

### 2. 匹配能力

```yaml
POST /api/v1/ai/match-capabilities
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body:
  project_id: string (required)
  requirements: array (optional)

Response: 202 Accepted
  {
    "success": true,
    "data": {
      "task_id": "uuid",
      "status": "processing"
    }
  }
```

### 3. 生成内容

```yaml
POST /api/v1/ai/generate-content
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body:
  document_id: string (required)
  section_id: string (optional)
  prompt: string (required)
  context: object (optional)
  model: string (gpt-4|gpt-3.5-turbo|claude-3, default: gpt-4)
  temperature: number (0-2, default: 0.7)
  max_tokens: number (default: 2000)

Response: 200 OK
  {
    "success": true,
    "data": {
      "task_id": "uuid",
      "content": "string",
      "tokens_used": {
        "prompt": number,
        "completion": number,
        "total": number
      },
      "model": "string",
      "created_at": "datetime"
    }
  }

Errors:
  - 400: INVALID_PROMPT
  - 429: RATE_LIMIT_EXCEEDED - AI调用超过限额
  - 500: AI_SERVICE_ERROR - AI服务错误
```

### 4. 质量审核

```yaml
POST /api/v1/ai/review-quality
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body:
  document_id: string (required)
  check_types: string[] (plagiarism|completeness|compliance|quality)

Response: 202 Accepted
  {
    "success": true,
    "data": {
      "task_id": "uuid",
      "status": "processing"
    }
  }
```

### 5. 获取AI任务状态

```yaml
GET /api/v1/ai/tasks/{task_id}
Authorization: Bearer {access_token}

Response: 200 OK
  {
    "success": true,
    "data": {
      "id": "uuid",
      "task_type": "string",
      "status": "string",
      "progress": number,
      "result": object,
      "error_message": string,
      "started_at": "datetime",
      "completed_at": "datetime",
      "duration_seconds": number
    }
  }
```
