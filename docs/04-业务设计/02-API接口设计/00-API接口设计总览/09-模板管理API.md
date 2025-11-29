---
文档类型: 架构文档
需求编号: DOC-2025-11-001
创建日期: 2025-11-15
创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
最后更新: 2025-11-26
更新者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
状态: 已批准
---

# AI标书智能创作平台 - API接口设计 - 📋 模板管理API

> **服务**: Java Spring Boot
> **端口**: 8080

### 1. 获取模板列表

```yaml
GET /api/v1/templates
Authorization: Bearer {access_token}

Query Parameters:
  page: integer
  pageSize: integer
  category: string
  industry: string
  scope: string (public|organization|private)
  search: string

Response: 200 OK (Paginated)
```

### 2. 获取模板详情

```yaml
GET /api/v1/templates/{template_id}
Authorization: Bearer {access_token}

Response: 200 OK
  {
    "success": true,
    "data": {
      "id": "uuid",
      "name": "string",
      "description": "string",
      "category": "string",
      "industry": "string",
      "content": {...},
      "structure": [...],
      "variables": [...],
      "usage_count": number,
      "rating": number
    }
  }
```

### 3. 创建模板

```yaml
POST /api/v1/templates
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body:
  name: string (required)
  description: string
  category: string
  template_type: string
  content: object (required)
  structure: array
  variables: array

Response: 201 Created
```
