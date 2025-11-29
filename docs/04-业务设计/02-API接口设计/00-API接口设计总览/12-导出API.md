---
文档类型: 架构文档
需求编号: DOC-2025-11-001
创建日期: 2025-11-15
创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
最后更新: 2025-11-26
更新者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
状态: 已批准
---

# AI标书智能创作平台 - API接口设计 - 📤 导出API

> **服务**: Java Spring Boot
> **端口**: 8080

### 1. 导出为PDF

```yaml
POST /api/v1/export/pdf
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body:
  document_id: string (required)
  options: {
    page_size: string (A4|A3|Letter, default: A4)
    orientation: string (portrait|landscape, default: portrait)
    include_toc: boolean (default: true)
    watermark: string (optional)
  }

Response: 202 Accepted
  {
    "success": true,
    "data": {
      "task_id": "uuid",
      "status": "processing"
    }
  }
```

### 2. 导出为Word

```yaml
POST /api/v1/export/word
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body:
  document_id: string (required)
  options: {
    include_images: boolean (default: true)
    include_comments: boolean (default: false)
  }

Response: 202 Accepted
```

### 3. 获取导出任务状态

```yaml
GET /api/v1/export/tasks/{task_id}
Authorization: Bearer {access_token}

Response: 200 OK
  {
    "success": true,
    "data": {
      "task_id": "uuid",
      "status": "success",
      "download_url": "string",
      "file_size": number,
      "expires_at": "datetime"
    }
  }
```
